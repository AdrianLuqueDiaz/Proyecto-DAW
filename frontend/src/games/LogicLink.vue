<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { registrarPartida } from '@/services/statsService'

const router = useRouter()
const auth = useAuthStore()
const API_URL = import.meta.env.VITE_API_BASE_URL

const COSTE_JUEGO = 75;

const retos = ref<any[]>([])
const retoActualId = ref(0)
const opciones = ref<string[]>([])
const estado = ref('jugando') // jugando, correcto, error, terminado
const piezaSoltada = ref<string | null>(null)

const piezaSeleccionadaMovil = ref<string | null>(null)

const seleccionarPiezaMovil = (textoPieza: string) => {
  if (estado.value !== 'jugando') return
  
  if (piezaSeleccionadaMovil.value === textoPieza) {
    piezaSeleccionadaMovil.value = null // Desmarcar si toca la misma
  } else {
    piezaSeleccionadaMovil.value = textoPieza // Marcar para colocar
  }
}

const colocarPiezaMovil = () => {
  if (piezaSeleccionadaMovil.value && estado.value === 'jugando') {
    validarRespuesta(piezaSeleccionadaMovil.value)
    piezaSeleccionadaMovil.value = null 
  }
}

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarRetos = async () => {
  try {
    const res = await fetch(`${API_URL}/logiclink`)
    if (res.ok) {
      let datos = await res.json()
      retos.value = datos.sort(() => Math.random() - 0.5)
      prepararReto()
    }
  } catch (error) {
    console.error("Error cargando los enlaces lógicos:", error)
  }
}

const prepararReto = () => {
  if (retos.value.length === 0) return
  
  estado.value = 'jugando'
  piezaSoltada.value = null
  piezaSeleccionadaMovil.value = null 
  
  const retoActual = retos.value[retoActualId.value]
  const arrayOpciones = [
    retoActual.respuestaCorrecta, 
    retoActual.opcionFalsa1, 
    retoActual.opcionFalsa2
  ]
  opciones.value = arrayOpciones.sort(() => Math.random() - 0.5)
}

const actualizarBytes = async (cantidad: number) => {
  if (!auth.jugador) return;
  try {
    const res = await fetch(`${API_URL}/usuarios/${auth.jugador}/bytes?cantidad=${cantidad}`, {
      method: 'PUT'
    });
    if (res.ok) {
      const usuario = await res.json();
      auth.bytes = usuario.saldoBytes;
      auth.guardarEnLocal();
    }
  } catch (error) {
    console.error("Error al actualizar bytes:", error);
  }
}


const agarrarPieza = (evento: DragEvent, textoDeLaPieza: string) => {
  evento.dataTransfer?.setData('texto', textoDeLaPieza)
}

const soltarPieza = (evento: DragEvent) => {
  if (estado.value !== 'jugando') return

  const textoSoltado = evento.dataTransfer?.getData('texto')
  if (!textoSoltado) return

  validarRespuesta(textoSoltado)
}


const validarRespuesta = async (textoRespuesta: string) => {
  piezaSoltada.value = textoRespuesta 
  
  const retoActual = retos.value[retoActualId.value]

  if (textoRespuesta === retoActual.respuestaCorrecta) {
    estado.value = 'correcto'
    await actualizarBytes(75)
    await registrarPartida('Logic Link', 75)

    setTimeout(() => {
      if (retoActualId.value < retos.value.length - 1) {
        retoActualId.value++ 
        prepararReto()
      } else {
        estado.value = 'terminado' 
      }
    }, 1000)

  } else {
    estado.value = 'error'
    await actualizarBytes(-75)
    await registrarPartida('Logic Link', -75)

    setTimeout(() => {
      const bytesActuales = Number(auth.bytes) || 0;
      if (bytesActuales < COSTE_JUEGO) {
        alert("Te has quedado sin saldo suficiente. Necesitas los Bytes de entrada mínimos para seguir jugando.");
        router.push('/home'); 
      } else {
        piezaSoltada.value = null
        estado.value = 'jugando'
      }
    }, 1000)
  }
}

onMounted(() => {
  const bytesActuales = Number(auth.bytes) || 0;
  if (auth.bytes < COSTE_JUEGO) {
    alert(`ACCESO DENEGADO. Necesitas al menos ${COSTE_JUEGO} Bytes para jugar a Logic Link.`);
    router.push('/home');
    return;
  }
  cargarRetos()
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
          <h4 class="info-titulo">Instrucciones</h4>
          <p class="instruccion-texto">
            <span class="solo-pc">Arrastra</span>
            <span class="solo-movil">Toca</span>
            el bloque de código correcto al hueco para completar la sintaxis.
            <span class="solo-movil"> Luego toca el hueco para colocarlo.</span>
          </p>
          <ul class="info-lista">
            <li><span class="valor-base">Acierto</span> <span class="separador">→</span> <span class="valor-exito">+75 Bytes</span></li>
            <li><span class="valor-base">Fallo</span> <span class="separador">→</span> <span class="valor-error">-75 Bytes</span></li>
          </ul>
        </div>
      </aside>

      <div class="mesa-central">
        
        <div class="cabecera-juego">
          <h1 class="titulo-juego">Logic Link</h1>
          <p class="subtitulo-juego">Reconstruye los fragmentos de código dañados.</p>
        </div>

        <div v-if="retos.length > 0 && estado !== 'terminado'" class="tablero-juego">
          
          <div class="zona-codigo">
            <span class="texto-codigo">{{ retos[retoActualId].codigoAnterior }}</span>
            
            <div 
              class="hueco-drop" 
              :class="{ 
                'con-pieza': piezaSoltada, 
                'acierto': estado === 'correcto', 
                'fallo': estado === 'error',
                'esperando-pieza-movil': piezaSeleccionadaMovil 
              }"
              @dragover.prevent 
              @drop="soltarPieza"
              @click="colocarPiezaMovil" 
            >
              {{ piezaSoltada ? piezaSoltada : (piezaSeleccionadaMovil ? 'toca aquí' : 'suelta aquí') }}
            </div>
            
            <span class="texto-codigo">{{ retos[retoActualId].codigoPosterior }}</span>
          </div>

          <div class="resultados">
            <div v-if="estado === 'correcto'" class="mensaje exito">¡SINTAXIS CORRECTA! +75 BYTES</div>
            <div v-if="estado === 'error'" class="mensaje error">ERROR -75 BYTES</div>
          </div>

          <hr class="divisor">

          <div class="zona-opciones" v-if="estado === 'jugando'">
            <div 
              v-for="(opcion, index) in opciones" 
              :key="index"
              class="pieza-draggable"
              :class="{ 'seleccionada-movil': piezaSeleccionadaMovil === opcion }" 
              draggable="true"
              @dragstart="agarrarPieza($event, opcion)"
              @click="seleccionarPiezaMovil(opcion)" 
            >
              {{ opcion }}
            </div>
          </div>
          
        </div>

        <div v-else-if="estado === 'terminado'" class="tablero-juego centrado">
          <div class="mensaje exito espaciado-inferior">
            <h3>Sistemas Restaurados</h3>
            <p>Has unido todos los bloques de código disponibles.</p>
          </div>
          <button @click="cargarRetos" class="boton-primario">JUGAR OTRA VEZ</button>
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

.navegacion { 
  display: flex; 
  height: 100%; 
}

.enlace { 
  display: flex;
  align-items: center;
  padding: 0 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748B;
  cursor: pointer;
  transition: 0.2s;
}

.enlace.activo {
  color: #00E5FF;
  border-bottom: 2px solid #00E5FF;
}

.info-usuario { 
  display: flex; 
  align-items: center; 
  gap: 20px; 
}

.bloque-bytes {
  display: flex;
  align-items: baseline;
  gap: 8px;
  background: #1E2532;
  padding: 6px 12px;
  border-radius: 4px;
}

.valor-bytes {
  font-family: 'Consolas', monospace;
  color: #FACC15;
  font-weight: bold;
}

.nombre-jugador {
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
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.boton-salir:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
  border-color: #EF4444;
}


@media (max-width: 1024px) {
  .cabecera { 
    height: auto; 
    flex-direction: column; 
    padding: 20px; 
    gap: 15px; 
  }

  .navegacion { 
    height: auto;
    gap: 5px; 
    width: 100%;
    justify-content: center;
  }

  .enlace { 
    padding: 10px 12px; 
    border-bottom: none !important;
  }

  .info-usuario { 
    width: 100%;
    justify-content: center;
    gap: 15px; 
  }
}


.contenido-juego { 
    flex-direction: column; 
    align-items: center; 
    padding: 10px 10px; 
    gap: 20px;
  }
@media (min-width: 900px) {
  .contenido-juego { padding: 40px; gap: 40px; }
}

@media (max-width: 900px) {
  .contenido-juego {
    flex-direction: column;
    align-items: center;
  }
}

.panel-lateral {
  width: 100%;
}

@media (min-width: 900px) {
  .panel-lateral { width: 250px; flex-shrink: 0; }
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

.boton-volver:hover { 
  color: #E2E8F0; 
}

.tarjeta-info {
  background: #11151D;
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #1E2532;
}

.info-titulo {
  margin-top: 0;
  color: #00E5FF;
  font-size: 1.1rem;
  margin-bottom: 10px;
}

.instruccion-texto {
  color: #94A3B8;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 15px;
}

.info-lista {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-lista li {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.valor-base { color: #E2E8F0; }
.separador { color: #64748B; }
.valor-exito { color: #10B981; font-weight: bold; }
.valor-error { color: #EF4444; font-weight: bold; }

.mesa-central {
  width: 100%;
  max-width: 800px;
}

.cabecera-juego {
  text-align: center;
  margin-bottom: 30px;
}

.titulo-juego {
  font-size: 2.5rem;
  margin: 0 0 10px 0;
  color: #E2E8F0;
  letter-spacing: 1px;
}

.subtitulo-juego {
  color: #94A3B8;
  margin: 0;
  font-size: 1rem;
}

.tablero-juego {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 25px;
}

@media (min-width: 768px) {
  .tablero-juego { padding: 40px; }
}

.zona-codigo {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
  background: #0B0E14;
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #1E2532;
  font-family: 'Consolas', monospace;
  font-size: 1rem;
}

@media (min-width: 768px) {
  .zona-codigo { font-size: 1.2rem; padding: 30px; }
}

.texto-codigo {
  color: #38BDF8;
}

.hueco-drop {
  min-width: 120px;
  height: 40px;
  border: 2px dashed #334155;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #64748B;
  background: rgba(11, 14, 20, 0.5);
  transition: all 0.2s ease;
  cursor: pointer; 
}

.hueco-drop.esperando-pieza-movil {
  border: 2px dashed #00E5FF;
  background: rgba(0, 229, 255, 0.1);
  color: #00E5FF;
}

.hueco-drop.con-pieza {
  border: 2px solid #00E5FF;
  color: #00E5FF;
  background: rgba(0, 229, 255, 0.05);
}

.hueco-drop.acierto {
  border: 2px solid #10B981;
  color: #10B981;
  background: rgba(16, 185, 129, 0.1);
}

.hueco-drop.fallo {
  border: 2px solid #EF4444;
  color: #EF4444;
  background: rgba(239, 68, 68, 0.1);
}

.resultados {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.mensaje {
  font-weight: bold;
  padding: 12px 24px;
  border-radius: 4px;
  text-align: center;
  width: 100%;
}

.exito {
  background: rgba(16, 185, 129, 0.1);
  color: #10B981;
  border: 1px solid #10B981;
}

.error {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
  border: 1px solid #EF4444;
}

.divisor {
  border: 0;
  border-top: 1px solid #1E2532;
  margin: 30px 0;
}

.zona-opciones {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: center;
}

.pieza-draggable {
  padding: 12px 24px;
  background-color: #1E2532;
  border: 1px solid #334155;
  border-radius: 4px;
  color: #F8FAFC;
  font-family: 'Consolas', monospace;
  cursor: grab;
  transition: all 0.2s ease;
  user-select: none; 
  -webkit-user-select: none;
}

.pieza-draggable:active {
  cursor: grabbing;
}

.pieza-draggable:hover {
  border-color: #64748B;
  transform: translateY(-2px);
}

.pieza-draggable.seleccionada-movil {
  border-color: #00E5FF;
  background-color: rgba(0, 229, 255, 0.15);
}

.centrado {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px 20px;
}

.espaciado-inferior {
  margin-bottom: 30px;
}

.boton-primario {
  background: #00E5FF;
  color: #0B0E14;
  border: none;
  padding: 14px 32px;
  font-size: 1rem;
  font-weight: 900;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.boton-primario:hover {
  background: #00B8CC;
  transform: scale(1.05);
}


.solo-pc { display: inline; }
.solo-movil { display: none; }

@media (max-width: 768px) {
  .solo-pc { display: none; }
  .solo-movil { display: inline; }
  
  .pieza-draggable {
    cursor: pointer;
  }
}
</style>