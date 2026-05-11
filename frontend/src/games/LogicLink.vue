<script setup lang="ts">
import { ref, onMounted } from 'vue'
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

// JUEGO

const agarrarPieza = (evento: DragEvent, textoDeLaPieza: string) => {
  evento.dataTransfer?.setData('texto', textoDeLaPieza) //memoria del navegador
}

const soltarPieza = async (evento: DragEvent) => {
  if (estado.value !== 'jugando') return

  const textoSoltado = evento.dataTransfer?.getData('texto')//leemos el tecto de la memoria del navegador
  if (!textoSoltado) return

  piezaSoltada.value = textoSoltado //pieza visualmente
  
  const retoActual = retos.value[retoActualId.value]

  if (textoSoltado === retoActual.respuestaCorrecta) {
   
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
        alert("Te has quedado sin saldo suficiente. Necesitas ${COSTE_JUEGO} Bytes para seguir jugando.");
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
    alert(`ACCESO DENEGADO. Necesitas al menos ${COSTE_JUEGO} Bytes para jugar a CodeLink.`);
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
          <p class="instruccion-texto">Arrastra el bloque de código correcto al hueco para completar la sintaxis.</p>
          <ul class="info-lista">
            <li><span class="valor-base">Acierto</span> <span class="separador">→</span> <span class="valor-final">+75 Bytes</span></li>
            <li><span class="valor-base">Fallo</span> <span class="separador">→</span> <span class="valor-error">-100 Bytes</span></li>
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
                'fallo': estado === 'error' 
              }"
              @dragover.prevent 
              @drop="soltarPieza"
            >
              {{ piezaSoltada ? piezaSoltada : 'suelta aqui' }}
            </div>
            
            <span class="texto-codigo">{{ retos[retoActualId].codigoPosterior }}</span>
          </div>

          <div class="resultados">
            <div v-if="estado === 'correcto'" class="mensaje exito">¡SINTAXIS CORRECTA! +75 BYTES</div>
            <div v-if="estado === 'error'" class="mensaje error">ERROR -100 BYTES</div>
          </div>

          <hr class="divisor">

          <div class="zona-opciones" v-if="estado === 'jugando'">
            <div 
              v-for="(opcion, index) in opciones" 
              :key="index"
              class="pieza-draggable"
              draggable="true"
              @dragstart="agarrarPieza($event, opcion)"
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
}

.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 80px;
  background-color: #11151D;
  border-bottom: 2px solid #1E2532;
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
  letter-spacing: 1px;
  color: #64748B;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.enlace:hover { color: #E2E8F0; }
.enlace.activo {
  color: #00E5FF;
  border-bottom: 2px solid #00E5FF;
}

.info-usuario {
  display: flex;
  align-items: center;
  gap: 25px;
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
  letter-spacing: 1px;
}

.valor-bytes {
  font-family: 'Consolas', monospace;
  font-weight: bold;
  color: #FACC15;
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
}

.panel-lateral {
  width: 250px;
  flex-shrink: 0;
}

.boton-volver {
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748B;
  cursor: pointer;
  margin-bottom: 15px;
}

.boton-volver:hover { color: #E2E8F0; }

.tarjeta-info {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 20px;
}

.info-titulo {
  font-size: 0.75rem;
  font-weight: 800;
  color: #64748B;
  margin-bottom: 15px;
  text-transform: uppercase;
}

.instruccion-texto {
  font-size: 0.85rem;
  color: #94A3B8;
  margin-bottom: 15px;
  line-height: 1.5;
}

.info-lista {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-lista li {
  display: flex;
  justify-content: space-between;
  font-family: 'Consolas', monospace;
  font-size: 0.85rem;
  background: #0B0E14;
  padding: 8px 12px;
  border-radius: 4px;
}

.valor-base { color: #94A3B8; }
.valor-final { color: #00E5FF; font-weight: bold; }
.valor-error { color: #EF4444; font-weight: bold; }

.mesa-central {
  width: 100%;
  max-width: 800px;
}

.cabecera-juego {
  margin-bottom: 30px;
  text-align: center;
}

.titulo-juego {
  font-size: 2.5rem;
  font-weight: 800;
  color: #F8FAFC;
}

.tablero-juego {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 40px;
}

.zona-codigo {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  background: #0B0E14;
  padding: 30px;
  border-radius: 6px;
  border: 1px solid #1E2532;
  font-family: 'Consolas', monospace;
  font-size: 1.2rem;
  line-height: 2;
  margin-bottom: 20px;
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
  font-size: 0.85rem;
  background: rgba(11, 14, 20, 0.5);
}

.hueco-drop.con-pieza {
  border: 2px solid #00E5FF;
  background: rgba(0, 229, 255, 0.1);
  color: #00E5FF;
  font-size: 1.1rem;
}

.hueco-drop.acierto { border-color: #10B981; color: #10B981; }
.hueco-drop.fallo { border-color: #EF4444; color: #EF4444; }

.divisor {
  border: 0;
  height: 1px;
  background-color: #1E2532;
  margin: 30px 0;
}

.zona-opciones {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
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
}

.pieza-draggable:hover { border-color: #00E5FF; }

.mensaje {
  padding: 10px 20px;
  border-radius: 4px;
  font-weight: bold;
  text-align: center;
}

.mensaje h3 {
  margin: 0 0 5px 0;
  font-size: 1.2rem;
}

.espaciado-inferior {
  margin-bottom: 20px;
}

.exito { background: rgba(16, 185, 129, 0.1); border: 1px solid #10B981; color: #10B981; }
.error { background: rgba(239, 68, 68, 0.1); border: 1px solid #EF4444; color: #EF4444; }

.centrado {
  text-align: center;
}

.boton-primario {
  background-color: #E2E8F0;
  color: #0B0E14;
  border: none;
  border-radius: 4px;
  padding: 14px 28px;
  font-weight: 800;
  cursor: pointer;
}

.boton-primario:hover { background-color: #00E5FF; }
</style>