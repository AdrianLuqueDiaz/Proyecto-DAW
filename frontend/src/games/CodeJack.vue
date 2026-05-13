<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { registrarPartida } from '@/services/statsService';

const API_URL = import.meta.env.VITE_API_BASE_URL

const router = useRouter()
const auth = useAuthStore()

const COSTE_JUEGO = 50;


interface Carta {
  texto: string;
  valor: number;
}

const cartasPosibles = ref<Carta[]>([])

const miMano = ref<Carta[]>([])
const manoBanca = ref<Carta[]>([])

const estado = ref<string>('jugando') // jugando, evaluando, ganado, perdido, empate

  const totalGB = computed<number>(() => {
  let total = 0
  miMano.value.forEach((carta) => {
    total = total + carta.valor
  })

  return total
})

const totalBanca = computed<number>(() => {
  let total = 0
  manoBanca.value.forEach(carta => total += carta.valor)
  return total
})

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarCartas = async () => {
  try {
    const respuesta = await fetch(`${API_URL}/cartas-codejack`)
    if (respuesta.ok) {
      cartasPosibles.value = await respuesta.json()
    } else {
      console.error("Error: No se pudo cargar la baraja del servidor.")
    }
  } catch (error) {
    console.error("Error de conexión con el servidor central:", error)
  }
}

const actualizarBytes = async (cantidad: number) => {
  if (!auth.jugador) return;

  try {
    const respuesta = await fetch(`${API_URL}/usuarios/${auth.jugador}/bytes?cantidad=${cantidad}`, {
      method: 'PUT'
    });

    if (respuesta.ok) {
      const usuarioActualizado = await respuesta.json();
      
      auth.bytes = usuarioActualizado.saldoBytes;
      auth.partidasJugadas = usuarioActualizado.partidasJugadas;
      auth.guardarEnLocal();
      
    } else {
      console.error("El servidor rechazó la actualización.");
    }
  } catch (error) {
    console.error("Fallo en la sincronización de datos:", error);
  }
}

const robarCarta = () => {
  if (cartasPosibles.value.length === 0) return 

  const azar = Math.floor(Math.random() * cartasPosibles.value.length)
  const cartaRobada = cartasPosibles.value[azar]
  
  if (cartaRobada) {
    miMano.value.push(cartaRobada)
  }

  if (totalGB.value > 21) {
    estado.value = 'perdido'
    actualizarBytes(-50) 

    registrarPartida('Code-Jack 21', -50) 
  }
}

const plantarse = () => {
  estado.value = 'evaluando' 

  // La banca roba cartas hasta llegar al menos a 17
  while (totalBanca.value < 17) {
    const azar = Math.floor(Math.random() * cartasPosibles.value.length)
    const cartaRobada = cartasPosibles.value[azar]
    if (cartaRobada) {
      manoBanca.value.push(cartaRobada)
    }
  }


  if (totalBanca.value > 21) {
    estado.value = 'ganado' 
    actualizarBytes(75) 
    registrarPartida('Code-Jack 21', 75) 
  } else if (totalGB.value > totalBanca.value) {
    estado.value = 'ganado' 
    actualizarBytes(75) 
    registrarPartida('Code-Jack 21', 75) 
  } else if (totalGB.value < totalBanca.value) {
    estado.value = 'perdido' 
    actualizarBytes(-50) 
    registrarPartida('Code-Jack 21', -50) 
  } else {
    estado.value = 'empate' 
    actualizarBytes(0) 
    registrarPartida('Code-Jack 21', 0)
  }
}

const nuevaRonda = () => {
  miMano.value = []
  manoBanca.value = []
  estado.value = 'jugando'
}

onMounted(() => {
  if (auth.bytes < COSTE_JUEGO) {
    alert(`ACCESO DENEGADO. Necesitas al menos ${COSTE_JUEGO} Bytes para jugar a Code-Jack 21.`);
    router.push('/home');
    return;
  }
  cargarCartas()
  
})
</script>

<template>
  <div class="contenedor-principal">
    
    <header class="cabecera">
      <div class="logo">
        <span class="logo-bit">BIT</span><span class="logo-hub">HUB</span>
      </div>

      <nav class="navegacion">
        <span class="enlace activo" @click="router.push('/home')">INICIO</span>
        <span class="enlace" @click="router.push('/clasificacion')">CLASIFICACIÓN</span>
        <span v-if="auth.rol === 'ADMIN'" class="enlace" @click="router.push('/admin')">ADMINISTRACIÓN</span>
      </nav>

      <div class="info-usuario">
        <div class="bloque-bytes">
          <span class="etiqueta-bytes">BYTES</span>
          <span class="valor-bytes">{{ auth.bytes }}</span>
        </div>
        
       

        <span class="nombre-jugador">{{ auth.jugador || 'INVITADO' }}</span>
        <button class="boton-salir" @click="cerrarSesion">SALIR</button>
      </div>
    </header>

    <main class="contenido-juego">
      
      <aside class="panel-lateral">
        
        <div class="boton-volver" @click="router.push('/home')">
          ← Volver al Home
        </div>

        <div class="tarjeta-info">
          <h4 class="info-titulo">Guía de Conversión</h4>
          <ul class="info-lista">
            <li><span class="valor-base">1024 MB</span> <span class="separador">→</span> <span class="valor-final">1 GB</span></li>
            <li><span class="valor-base">512 MB</span> <span class="separador">→</span> <span class="valor-final">0.5 GB</span></li>
            <li><span class="valor-base">1.048.576 KB</span> <span class="separador">→</span> <span class="valor-final">1 GB</span></li>

          </ul>
        </div>
      </aside>

      <div class="mesa-central">
        
        <div class="cabecera-juego">
          <h1 class="titulo-juego">Code-Jack 21</h1>
          <p class="subtitulo-juego">Supera a la IA sin exceder los 21 GB de memoria.</p>
        </div>

        <div class="tablero-juego">
          <div class="zona-jugador">
            <div class="etiqueta-estado">
              <span class="nombre">SISTEMA CENTRAL</span>
              <span class="memoria">{{ totalBanca }} GB</span>
            </div>
            
            <div class="zona-cartas mini">
              <div v-for="(carta, index) in manoBanca" :key="index" class="carta-dato">
                <span class="texto-carta">{{ carta.texto }}</span>
              </div>
            </div>
          </div>

          <hr class="divisor">

          <div class="zona-jugador">
            <div class="etiqueta-estado tu-turno">
              <span class="nombre">TU MEMORIA</span>
              <span v-if="estado === 'jugando'" class="memoria">[ Cálculo Oculto ]</span>
              <span v-else class="memoria">{{ totalGB }} GB</span>
            </div>
            
            <div class="zona-cartas">
              <div v-for="(carta, index) in miMano" :key="index" class="carta-dato">
                <div class="carta-interior">
                  <span class="icono-dato">≡</span>
                  <span class="texto-carta">{{ carta.texto }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="resultados" v-if="estado !== 'jugando' && estado !== 'evaluando'">
          <div v-if="estado === 'ganado'" class="mensaje exito">
            <h3>Operación Exitosa</h3>
            <p>Has superado la validación del sistema.</p>
          </div>
          <div v-if="estado === 'perdido'" class="mensaje error">
            <h3>Desbordamiento de Memoria</h3>
            <p>El sistema ha bloqueado la operación.</p>
          </div>
          <div v-if="estado === 'empate'" class="mensaje neutral">
            <h3>Conexión Neutralizada</h3>
            <p>Se ha producido un empate de recursos.</p>
          </div>
        </div>

        <div class="controles">
          <button v-if="estado === 'jugando'" @click="robarCarta" class="boton-primario">EXTRAER DATOS</button>
          <button v-if="estado === 'jugando' && miMano.length > 0" @click="plantarse" class="boton-secundario">DETENER EXTRACCIÓN</button>
          
          <button v-if="estado !== 'jugando' && estado !== 'evaluando'" @click="nuevaRonda" class="boton-primario">NUEVO INTENTO</button>
        </div>

      </div>
    </main>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.contenedor-principal {
  min-height: 100vh;
  width: 100%;
  background-color: #0B0E14;
  color: #E2E8F0;
  font-family: 'Inter', system-ui, sans-serif;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}


.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px; 
  height: 80px; 
  background-color: #11151D; 
  border-bottom: 2px solid #1E2532;
  width: 100%;
  flex-shrink: 0;
}

.logo {
  font-family: 'Consolas', monospace;
  font-size: 1.5rem;
  font-weight: 900;
  letter-spacing: 2px;
}
.logo-bit { color: #E2E8F0; }
.logo-hub { color: #00E5FF; }

.navegacion { display: flex; height: 100%; }

.enlace { 
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #bac8db;
  cursor: pointer;
  transition: 0.2s;
}
.enlace:hover { color: #E2E8F0; }
.enlace.activo {
  color: #00E5FF;
  border-bottom: 2px solid #00E5FF;
}

.info-usuario { display: flex; align-items: center; gap: 20px; }

.bloque-bytes {
  display: flex;
  align-items: baseline;
  gap: 8px;
  background: #1E2532;
  padding: 6px 12px;
  border-radius: 4px;
}
.etiqueta-bytes { font-size: 0.7rem; font-weight: 700; color: #bac8db; text-transform: uppercase; }
.valor-bytes { font-family: 'Consolas', monospace; color: #FACC15; font-weight: bold; font-size: 1.1rem; }
.nombre-jugador { font-family: 'Consolas', monospace; font-size: 0.9rem; color: #E2E8F0; }

.boton-salir { 
  background: transparent; border: 1px solid #334155; color: #bac8db;
  padding: 8px 16px; font-size: 0.75rem; border-radius: 4px; cursor: pointer; font-weight: bold; transition: 0.2s;
}
.boton-salir:hover { background: #EF4444; border-color: #EF4444; color: #fff; }


.contenido-juego {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 20px;
  gap: 40px;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.panel-lateral { 
  width: 260px; 
  flex-shrink: 0; 
}

.boton-volver { 
  font-size: 0.85rem;
  color: #64748B;
  cursor: pointer;
  margin-bottom: 20px;
  font-weight: 600;
  transition: color 0.2s;
  text-align: center;
  display: block;
}
.boton-volver:hover { color: #E2E8F0; }

.tarjeta-info {
  background: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 20px;
}

.info-titulo {
  font-size: 0.7rem;
  color: #64748B;
  text-transform: uppercase;
  text-align: center;
  margin-bottom: 15px;
  font-weight: 800;
}

.info-lista {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.info-lista li { 
  display: flex;
  justify-content: space-between;
  font-family: 'Consolas', monospace;
  font-size: 0.8rem;
  background: #0B0E14;
  padding: 8px;
  border-radius: 4px;
}

.valor-final { 
  color: #00E5FF; 
  font-weight: bold; 
}

.mesa-central {
  width: 100%;
  max-width: 700px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cabecera-juego { 
  text-align: center; 
  margin-bottom: 30px; 
}
.titulo-juego {
  font-size: 2.5rem;
  font-weight: 900;
  color: #F8FAFC;
  margin: 0;
}
.subtitulo-juego { 
  color: #64748B; 
  font-size: 0.95rem; 
}

.tablero-juego {
  background: #11151D;
  border: 1px solid #1E2532;
  border-radius: 8px;
  padding: 30px;
  width: 100%;
  margin-bottom: 25px;
}

.etiqueta-estado { 
  display: flex;
  justify-content: space-between;
  background: #0B0E14;
  padding: 12px 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-family: 'Consolas', monospace;
  border: 1px solid #1E2532;
}

.tu-turno { border-color: #334155; }
.tu-turno .nombre { color: #00E5FF; }
.memoria { color: #EF4444; font-weight: bold; font-size: 1.1rem; }

.divisor { 
  border: 0; 
  height: 1px; 
  background: #1E2532; 
  margin: 25px 0; 
}

.zona-cartas {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  min-height: 120px;
}
.zona-cartas.mini { min-height: 80px; }

.carta-dato { 
  width: 85px;
  height: 120px;
  background: #1E2532;
  border: 1px solid #334155;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: 0.2s;
}
.mini .carta-dato { width: 60px; height: 85px; background: #0B0E14; }
.carta-dato:hover { transform: translateY(-5px); border-color: #00E5FF; }

.texto-carta {
  font-family: 'Consolas', monospace;
  font-size: 0.75rem;
  font-weight: bold;
  text-align: center;
  color: #F8FAFC;
}

.resultados { 
  width: 100%; 
  margin-bottom: 20px; 
}

.mensaje {
  padding: 15px;
  border-radius: 6px;
  text-align: center;
  border: 1px solid transparent;
}
.mensaje.ganado { background: rgba(16, 185, 129, 0.1); border-color: #10B981; color: #10B981; }
.mensaje.perdido { background: rgba(239, 68, 68, 0.1); border-color: #EF4444; color: #EF4444; }
.mensaje.empate { background: rgba(245, 158, 11, 0.1); border-color: #F59E0B; color: #F59E0B; }

.controles { 
  display: flex; 
  gap: 15px; 
  justify-content: center; 
  width: 100%; 
}

.boton-primario {
  background: #E2E8F0;
  color: #0B0E14;
  border: none;
  padding: 14px 28px;
  font-weight: 800;
  border-radius: 4px;
  cursor: pointer;
  transition: 0.2s;
}
.boton-primario:hover { background: #00E5FF; }

.boton-secundario {
  background: transparent;
  border: 1px solid #334155;
  color: #94A3B8;
  padding: 14px 28px;
  font-weight: 800;
  border-radius: 4px;
  cursor: pointer;
}


@media (max-width: 1024px) {

  .cabecera { height: auto; flex-direction: column; padding: 20px; gap: 15px; }
  .navegacion { height: auto; width: 100%; justify-content: center; gap: 5px; }
  .enlace { padding: 10px; border-bottom: none !important; font-size: 0.75rem; }
  .info-usuario { width: 100%; justify-content: center; gap: 15px; }

  
  .contenido-juego { 
    flex-direction: column; 
    align-items: center; 
    padding: 20px 10px; 
  }
  .panel-lateral { width: 100%; max-width: 500px; order: 1; }
  .mesa-central { width: 100%; order: 2; }
}

@media (max-width: 600px) {
  .tablero-juego { padding: 15px; }
  .titulo-juego { font-size: 1.8rem; }
  .carta-dato { width: 70px; height: 100px; }
  .etiqueta-estado { flex-direction: column; gap: 5px; text-align: center; }
}
</style>