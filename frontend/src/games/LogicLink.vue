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
            <li><span class="valor-base">Acierto</span> <span class="separador">→</span> <span class="exito">+75 Bytes</span></li>
            <li><span class="valor-base">Fallo</span> <span class="separador">→</span> <span class="error">-75 Bytes</span></li>
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

.logo-bit {
  color: #E2E8F0;
}

.logo-hub {
  color: #00E5FF;
}

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

.enlace:hover {
  color: #E2E8F0;
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

.etiqueta-bytes {
  font-size: 0.7rem;
  font-weight: 700;
  color: #64748B;
  text-transform: uppercase;
}

.valor-bytes {
  font-family: 'Consolas', monospace;
  color: #FACC15;
  font-weight: bold;
  font-size: 1.1rem;
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
  border-radius: 4px;
  cursor: pointer;
}

.boton-salir:hover {
  background: #EF4444;
  border-color: #EF4444;
  color: #fff;
}

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

.mesa-central {
  flex: 1;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
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
  color: #00E5FF;
}

.tarjeta-info {
  background: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 20px;
}

.info-titulo {
  color: #00E5FF;
  font-size: 0.8rem;
  font-weight: 800;
  text-transform: uppercase;
  margin-bottom: 15px;
  text-align: center;
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
  font-size: 0.85rem;
  background: #0B0E14;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #1E2532;
}

.exito {
  color: #10B981;
  font-weight: bold;
}

.error {
  color: #EF4444;
  font-weight: bold;
}

.cabecera-juego {
  text-align: center;
  margin-bottom: 30px;
}

.titulo-juego {
  font-size: 2.8rem;
  font-weight: 900;
  color: #F8FAFC;
  margin: 0;
  letter-spacing: -1px;
}

.subtitulo-juego {
  color: #64748B;
  font-size: 1rem;
  margin-top: 5px;
}

.tablero-juego {
  background: #11151D;
  border: 1px solid #1E2532;
  border-radius: 8px;
  padding: 35px;
  width: 100%;
}

.zona-codigo {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
  background: #0B0E14;
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #1E2532;
  font-family: 'Consolas', monospace;
  font-size: 1.2rem;
}

.texto-codigo {
  color: #38BDF8;
}

.hueco-drop {
  min-width: 130px;
  height: 45px;
  border: 2px dashed #334155;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #64748B;
  background: rgba(11, 14, 20, 0.5);
  transition: 0.2s;
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
  min-height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  width: 100%;
}

.mensaje {
  font-weight: bold;
  padding: 12px 24px;
  border-radius: 4px;
  text-align: center;
  width: 100%;
  letter-spacing: 1px;
}

.mensaje.exito {
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid #10B981;
}

.mensaje.error {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid #EF4444;
}

.zona-opciones {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: center;
  margin-top: 20px;
}

.pieza-draggable {
  padding: 14px 28px;
  background-color: #1E2532;
  border: 1px solid #334155;
  border-radius: 4px;
  color: #F8FAFC;
  font-family: 'Consolas', monospace;
  cursor: grab;
  transition: 0.2s;
  font-weight: bold;
}

.pieza-draggable:hover {
  border-color: #00E5FF;
  transform: translateY(-3px);
  background: #252D3D;
}

.centrado {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.espaciado-inferior {
  margin-bottom: 25px;
}

.boton-primario {
  background: #00E5FF;
  color: #0B0E14;
  border: none;
  padding: 12px 24px;
  font-weight: 900;
  border-radius: 4px;
  cursor: pointer;
  transition: 0.2s;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.boton-primario:hover {
  background: #00B8CC;
  transform: scale(1.05);
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
    width: 100%;
    justify-content: center;
    gap: 5px;
  }

  .enlace {
    padding: 10px;
    border-bottom: none !important;
    font-size: 0.75rem;
  }

  .info-usuario {
    width: 100%;
    justify-content: center;
    gap: 15px;
  }

  .contenido-juego {
    flex-direction: column;
    align-items: center;
    padding: 15px 15px;
    gap: 20px;
  }

  .panel-lateral {
    width: 100%;
    max-width: 500px;
    order: 1;
  }

  .mesa-central {
    width: 100%;
    order: 2;
  }

  .boton-volver {
    margin-bottom: 10px;
  }

  .titulo-juego {
    font-size: 2rem;
    text-align: center;
  }

  .subtitulo-juego {
    text-align: center;
    font-size: 0.9rem;
  }

  .tablero-juego {
    padding: 20px;
  }

  .zona-codigo {
    font-size: 1rem;
    padding: 15px;
    gap: 10px;
  }

  .hueco-drop {
    min-width: 100px;
    height: 35px;
    font-size: 0.8rem;
  }
}
</style>