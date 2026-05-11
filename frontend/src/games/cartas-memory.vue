<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { registrarPartida } from '@/services/statsService';

const API_URL = import.meta.env.VITE_API_BASE_URL

const router = useRouter()
const auth = useAuthStore()

const COSTE_JUEGO = 50;

const bancoDePreguntas = ref<any[]>([])
const cartas = ref<any[]>([])
const seleccionadas = ref<any[]>([])
const cartasEnJuego = ref<any[]>([])
const bloquearTablero = ref(false)

const preguntaActual = ref<any>(null)
const mostrarPregunta = ref(false)
const opcionesRespuestas = ref<string[]>([])

const balancePartida = ref(0)

const juegoTerminado = computed(() => {
  return cartas.value.length > 0 && cartas.value.every(c => c.resuelta)
})

const finalizarPartidaEnBD = async (cantidadTotal: number) => {
  if (!auth.jugador) return;
  try {
    const res = await fetch(`${API_URL}/usuarios/${auth.jugador}/bytes?cantidad=${cantidadTotal}`, {
      method: 'PUT'
    });
    if (res.ok) {
      const usuarioActualizado = await res.json();

      auth.bytes = usuarioActualizado.saldoBytes;
      auth.partidasJugadas = usuarioActualizado.partidasJugadas;
      auth.guardarEnLocal();
      console.log("Progreso guardado en Logic Slots");
      

      registrarPartida('Logic Slots', cantidadTotal);
    }
  } catch (error) {
    console.error("Error al guardar progreso:", error);
  }
}

const actualizarMarcadorLocal = (cantidad: number) => {
  balancePartida.value += cantidad;
  auth.bytes += cantidad;
  auth.guardarEnLocal();
}

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarPreguntas = async () => {
  try {
    const res = await fetch(`${API_URL}/preguntas`)
    let todasLasPreguntas = await res.json()
    
    bancoDePreguntas.value = todasLasPreguntas.map((p: any) => {
      const opciones = [p.respuestaCorrecta, p.falsa1, p.falsa2, p.falsa3]
      return {
        ...p,
        opcionesBarajadas: opciones.sort(() => Math.random() - 0.5)
      }
    }).sort(() => Math.random() - 0.5) 
  } catch (error) {
    console.error("Error cargando preguntas:", error)
  }
}

const cargarCartas = async () => {
  try {
    const res = await fetch(`${API_URL}/cartas`)
    const datos = await res.json()
    cartas.value = datos.map((c: any) => ({ 
      ...c, 
      girada: false, 
      resuelta: false 
    })).sort(() => Math.random() - 0.5)
  } catch (error) { 
    console.error(error) 
  }
}

const seleccionarCarta = (carta: any) => {
  if (bloquearTablero.value || carta.girada || carta.resuelta) return
  carta.girada = true
  seleccionadas.value.push(carta)
  if (seleccionadas.value.length === 2) {
    comprobarPareja()
  }
}

const comprobarPareja = () => {
  bloquearTablero.value = true
  const carta1 = seleccionadas.value[0]
  const carta2 = seleccionadas.value[1]

  if (carta1.idPareja === carta2.idPareja) {
    cartasEnJuego.value = [carta1, carta2]
    lanzarPregunta(carta1.idPareja)
  } else {
    actualizarMarcadorLocal(-50);

    setTimeout(() => {
      carta1.girada = false
      carta2.girada = false
      soltarTurno()
    }, 1000)
  }
}

const lanzarPregunta = (idPareja: number) => { 
  const pregunta = bancoDePreguntas.value.find(p => p.idPareja === idPareja)
  if (pregunta) {
    preguntaActual.value = pregunta
    opcionesRespuestas.value = pregunta.opcionesBarajadas
    mostrarPregunta.value = true
  } else {
    resolverCartasEnJuego(true)
  }
}

const responder = (opcionSeleccionada: string) => {
  const esCorrecta = opcionSeleccionada === preguntaActual.value.respuestaCorrecta
  resolverCartasEnJuego(esCorrecta)
  mostrarPregunta.value = false
}

const resolverCartasEnJuego = (exito: boolean) => {
  const carta1 = cartasEnJuego.value[0]
  const carta2 = cartasEnJuego.value[1]

  if (exito) {
    carta1.resuelta = true
    carta2.resuelta = true
    
    actualizarMarcadorLocal(500);
    
    setTimeout(() => {
      if (juegoTerminado.value) {

        finalizarPartidaEnBD(balancePartida.value);
      }
    }, 500);

  } else {
    carta1.girada = false
    carta2.girada = false
    
    // RESTAMOS 50 POR FALLAR LA PREGUNTA
    actualizarMarcadorLocal(-50);
  }
  
  cartasEnJuego.value = []
  soltarTurno()
}

const soltarTurno = () => {
  seleccionadas.value = []
  bloquearTablero.value = false
}

const reiniciarJuego = () => {
  cartas.value = []
  balancePartida.value = 0 
  cargarCartas() 
  cargarPreguntas()
}

onMounted(() => {
  if (auth.bytes < COSTE_JUEGO) {
    alert(`ACCESO DENEGADO. Necesitas al menos ${COSTE_JUEGO} Bytes para jugar a Logic Slots.`);
    router.push('/home');
    return;
  }

  actualizarMarcadorLocal(-COSTE_JUEGO);

  cargarCartas()
  cargarPreguntas()
})
</script>

<template>
  <div class="pantalla-entera">
    
    <header class="barra-superior">
      <div class="logo">
        <span class="texto-bit">BIT</span><span class="texto-hub">HUB</span>
      </div>

      <nav class="menu-navegacion">
        <span class="enlace activo" @click="router.push('/home')">INICIO</span>
        <span class="enlace" @click="router.push('/clasificacion')">CLASIFICACIÓN</span>
        <span v-if="auth.rol === 'ADMIN'" class="enlace" @click="router.push('/admin')">ADMINISTRACIÓN</span>
      </nav>

      <div class="zona-usuario">
        <div class="caja-bytes">
          <span class="etiqueta-bytes">BYTES</span>
          <span class="numero-bytes">{{ auth.bytes }}</span>
        </div>
        <span class="nombre-usuario">{{ auth.jugador || 'INVITADO' }}</span>
        <button class="boton-salir" @click="cerrarSesion">SALIR</button>
      </div>
    </header>

    <main class="zona-juego">
      
      <div class="encabezado-juego">
        <div class="boton-volver" @click="router.push('/home')">
          ← Volver al Home
        </div>
        <h1 class="titulo">Logic Slots</h1>
        <p class="subtitulo">Empareja los conceptos y valida la lógica.</p>
      </div>

      <div v-if="cartas.length === 0" class="mensaje-cargando">
        <span class="texto-cargando">Sincronizando datos...</span>
      </div>

      <div class="tablero">
        <div 
          v-for="carta in cartas" 
          :key="carta.id" 
          class="carta"
          :class="{ 'girada': carta.girada || carta.resuelta, 'resuelta': carta.resuelta }"
          @click="seleccionarCarta(carta)"
        >
          <div class="interior-carta">
            <div class="frente-carta">
              <span class="icono-interrogacion">?</span>
            </div>
            <div class="dorso-carta">
              <span class="texto-carta">{{ carta.texto }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="mostrarPregunta" class="capa-oscura">
        <div class="caja-flotante caja-grande">
          <div class="cabecera-caja">
            <h3 class="titulo-caja">Validación de Lógica Requerida</h3>
          </div>
          <div class="cuerpo-caja">
            <p class="texto-pregunta">{{ preguntaActual.enunciado }}</p>
            
            <div class="caja-respuestas">
              <button 
                v-for="(opcion, index) in opcionesRespuestas" 
                :key="index"
                @click="responder(opcion)"
                class="boton-respuesta"
              >
                {{ opcion }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="juegoTerminado && !mostrarPregunta" class="capa-oscura">
        <div class="caja-flotante">
          <h3 class="titulo-caja">Módulo Completado</h3>
          <p class="texto-caja">Has resuelto correctamente todas las validaciones lógicas del tablero.</p>
          <div class="botones-accion">
            <button @click="reiniciarJuego" class="boton-principal">JUGAR DE NUEVO</button>
            <button @click="router.push('/home')" class="boton-secundario">SALIR</button>
          </div>
        </div>
      </div>

    </main>
  </div>
</template>

<style scoped>
.pantalla-entera {
  min-height: 100vh;
  width: 100%;
  background-color: #0B0E14;
  color: #E2E8F0;
  font-family: 'Inter', system-ui, sans-serif;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

.barra-superior {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 80px;
  background-color: #11151D;
  border-bottom: 2px solid #1E2532;
  box-sizing: border-box;
  flex-shrink: 0;
}

.logo {
  font-family: 'Consolas', monospace;
  font-size: 1.5rem;
  font-weight: 900;
  letter-spacing: 2px;
}

.texto-bit { color: #E2E8F0; }
.texto-hub { color: #00E5FF; }

.menu-navegacion {
  display: flex;
  height: 100%;
}

.enlace {
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 1px;
  color: #64748B;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.enlace:hover { color: #E2E8F0; }
.enlace.activo {
  color: #00E5FF;
  border-bottom: 2px solid #00E5FF;
}

.zona-usuario {
  display: flex;
  align-items: center; 
  gap: 25px;
}

.caja-bytes {
  display: flex;
  align-items: baseline;
  gap: 8px;
  background: #1E2532;
  padding: 6px 12px;
  border-radius: 4px;
}

.etiqueta-bytes {
  font-size: 0.7rem;
  font-weight: 700;
  color: #64748B;
  letter-spacing: 1px;
}

.numero-bytes {
  font-family: 'Consolas', monospace;
  font-weight: bold;
  color: #FACC15;
}

.nombre-usuario {
  font-family: 'Consolas', monospace;
  font-size: 0.9rem;
  color: #E2E8F0;
}

.boton-salir {
  background: transparent;
  border: 1px solid #334155;
  color: #94A3B8;
  padding: 8px 16px;
  font-size: 0.75rem;
  font-weight: bold;
  letter-spacing: 1px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.boton-salir:hover {
  background: #EF4444;
  border-color: #EF4444;
  color: #fff;
}

.zona-juego {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  box-sizing: border-box;
}

.encabezado-juego {
  width: 100%;
  max-width: 700px;
  margin-bottom: 30px;
}

.boton-volver {
  display: inline-block;
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748B;
  cursor: pointer;
  margin-bottom: 15px;
  transition: color 0.2s;
}

.boton-volver:hover { color: #E2E8F0; }

.titulo {
  font-size: 3rem;
  font-weight: 800;
  margin: 0;
  color: #F8FAFC;
  letter-spacing: -1px;
}

.subtitulo {
  font-family: 'Consolas', monospace;
  font-size: 0.85rem;
  color: #00E5FF;
  margin-top: 5px;
}

.tablero {
  display: grid; 
  grid-template-columns: repeat(4, 1fr); 
  gap: 15px;
  max-width: 700px;
  width: 100%;
}

.carta { 
  width: 100%; 
  aspect-ratio: 3 / 4; 
  perspective: 1000px; 
  cursor: pointer; 
}

.interior-carta {
  position: relative; 
  width: 100%; 
  height: 100%;
  transition: transform 0.4s ease-in-out; 
  transform-style: preserve-3d;
}

.carta.girada .interior-carta { transform: rotateY(180deg); }

.frente-carta, .dorso-carta {
  position: absolute; 
  width: 100%; 
  height: 100%; 
  backface-visibility: hidden;
  display: flex; 
  justify-content: center; 
  align-items: center;
  box-sizing: border-box; 
  border-radius: 6px;
  padding: 10px;
}

.frente-carta {
  background-color: #1E2532; 
  border: 1px solid #334155;
}

.icono-interrogacion {
  font-family: 'Consolas', monospace;
  color: #334155; 
  font-size: 2.5rem; 
  font-weight: bold;
}

.dorso-carta {
  transform: rotateY(180deg); 
  background-color: #11151D;
  border: 2px solid #00E5FF; 
}

.texto-carta {
  font-family: 'Consolas', monospace;
  font-weight: bold; 
  font-size: 0.8rem;
  text-align: center; 
  word-break: break-word;
}

.resuelta .dorso-carta {
  background-color: rgba(16, 185, 129, 0.1); 
  border-color: #10B981; 
  color: #10B981;
}

.capa-oscura {
  position: fixed; 
  top: 0; left: 0; 
  width: 100%; height: 100%;
  background: rgba(11, 14, 20, 0.95); 
  display: flex; 
  justify-content: center; 
  align-items: center; 
  z-index: 100;
  padding: 20px;
  box-sizing: border-box;
}

.caja-flotante {
  background: #11151D; 
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 30px; 
  text-align: center; 
  max-width: 500px;
  width: 100%;
}

.caja-grande { padding: 0; text-align: left; }

.cabecera-caja {
  background-color: #1E2532;
  padding: 20px;
  border-bottom: 1px solid #334155;
}

.cuerpo-caja { padding: 25px; }

.titulo-caja { color: #F8FAFC; font-size: 1.2rem; font-weight: 800; margin: 0; }

.texto-caja { color: #94A3B8; margin: 15px 0 30px 0; font-size: 0.95rem; }

.texto-pregunta { 
  font-family: 'Consolas', monospace;
  font-size: 1rem; 
  color: #E2E8F0;
  margin-bottom: 25px;
}

.caja-respuestas { 
  display: grid; 
  grid-template-columns: 1fr; 
  gap: 10px; 
}

.boton-respuesta {
  background: #0B0E14; 
  border: 1px solid #334155; 
  color: #E2E8F0;
  border-radius: 4px;
  padding: 14px; 
  cursor: pointer; 
  transition: all 0.2s ease;
  font-weight: 600;
  text-align: left;
}

.boton-respuesta:hover { border-color: #00E5FF; color: #00E5FF; }

.botones-accion { display: flex; gap: 10px; justify-content: center; }

.boton-principal {
  background-color: #E2E8F0;
  color: #0B0E14;
  border: none;
  border-radius: 4px;
  padding: 12px 20px;
  font-weight: 800;
  cursor: pointer;
  transition: background 0.2s;
}

.boton-principal:hover { background-color: #00E5FF; }

.boton-secundario {
  background: transparent;
  border: 1px solid #334155;
  color: #94A3B8;
  padding: 12px 20px;
  border-radius: 4px;
  font-weight: 800;
  cursor: pointer;
}

@media (max-width: 768px) {
  .barra-superior {
    flex-direction: column;
    height: auto;
    padding: 20px;
    gap: 15px;
  }
  .menu-navegacion { height: auto; width: 100%; justify-content: center; }
  .enlace { padding: 10px; font-size: 0.75rem; }
  .zona-usuario { width: 100%; justify-content: center; gap: 15px; border-top: 1px solid #1E2532; padding-top: 15px; }
  .titulo { font-size: 2.2rem; text-align: center; }
  .subtitulo { text-align: center; }
  .encabezado-juego { text-align: center; }
  .tablero { grid-template-columns: repeat(3, 1fr); gap: 10px; }
}

@media (max-width: 480px) {
  .tablero { grid-template-columns: repeat(2, 1fr); }
  .texto-carta { font-size: 0.7rem; }
  .caja-flotante { padding: 20px; }
  .cuerpo-caja { padding: 15px; }
}
</style>