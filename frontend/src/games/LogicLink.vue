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
    piezaSeleccionadaMovil.value = null
  } else {
    piezaSeleccionadaMovil.value = textoPieza
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

// LÓGICA DE VALIDACIÓN UNIFICADA
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
        alert("Te has quedado sin saldo suficiente. Necesitas los Bytes de entrada minimos para seguir jugando.");
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
    alert(`ACCESO DENEGADO. Necesitas al menos los Bytes de entrada minimos para jugar a CodeLink.`);
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
          ← Volver al Módulo Principal
        </div>
        <div class="tarjeta-info">
          <h4 class="info-titulo">Instrucciones</h4>
          <p class="instruccion-texto">
            <span class="solo-pc">Arrastra</span>
            <span class="solo-movil">Toca</span>
            el bloque de código correcto al hueco para completar la sintaxis.
            <span class="solo-movil"> Luego toca el hueco.</span>
          </p>
          <ul class="info-lista">
            <li><span class="valor-base">Acierto</span> <span class="separador">→</span> <span class="valor-final">+75 Bytes</span></li>
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
              {{ piezaSoltada ? piezaSoltada : 'suelta aqui' }}
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
.contenedor-principal {
  min-height: 100vh;
  background-color: #0B0E14;
  color: #E2E8F0;
  font-family: 'Inter', system-ui, sans-serif;
  display: flex;
  flex-direction: column;
  width: 100%;
  overflow-x: hidden;
}

.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  min-height: 80px;
  background-color: #11151D;
  border-bottom: 2px solid #1E2532;
  flex-wrap: wrap;
}

@media (min-width: 768px) {
  .cabecera {
    padding: 0 40px;
    height: 80px;
    flex-wrap: nowrap;
  }
}

.logo {
  font-family: 'Consolas', monospace;
  font-size: 1.3rem;
  font-weight: 900;
  letter-spacing: 2px;
  padding: 15px 0;
}

@media (min-width: 768px) {
  .logo { font-size: 1.5rem; }
}

.logo-bit { color: #E2E8F0; }
.logo-hub { color: #00E5FF; }

.navegacion {
  display: flex;
  order: 3;
  width: 100%;
  justify-content: center;
  border-top: 1px solid #1E2532;
}

@media (min-width: 768px) {
  .navegacion {
    order: 2;
    width: auto;
    height: 100%;
    border-top: none;
  }
}

.enlace {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 1px;
  color: #64748B;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

@media (min-width: 768px) {
  .enlace { padding: 0 20px; font-size: 0.85rem; }
}

.enlace.activo {
  color: #00E5FF;
  border-bottom: 2px solid #00E5FF;
}

.info-usuario {
  display: flex;
  align-items: center;
  gap: 15px;
  order: 2;
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
  font-weight: bold;
  color: #FACC15;
}

.nombre-jugador {
  display: none;
}

@media (min-width: 480px) {
  .nombre-jugador {
    display: inline;
    font-family: 'Consolas', monospace;
    font-size: 0.9rem;
  }
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
}

.contenido-juego {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
  gap: 20px;
}

@media (min-width: 900px) {
  .contenido-juego { padding: 40px; gap: 40px; }
}

.panel-lateral {
  width: 100%;
}

@media (min-width: 900px) {
  .panel-lateral { width: 250px; flex-shrink: 0; }
}

.mesa-central {
  width: 100%;
  max-width: 800px;
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
  flex-wrap: wrap;
  gap: 10px;
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
}

.hueco-drop.esperando-pieza-movil {
  border-color: #00E5FF;
  background: rgba(0, 229, 255, 0.05);
}

.hueco-drop.con-pieza {
  border: 2px solid #00E5FF;
  color: #00E5FF;
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
  cursor: pointer;
  transition: all 0.2s ease;
}

.pieza-draggable.seleccionada-movil {
  border-color: #00E5FF;
  background-color: rgba(0, 229, 255, 0.1);
  transform: translateY(-2px);
}

@media (max-width: 900px) {
  .contenido-juego {
    flex-direction: column;
    align-items: center;
  }
}
</style>