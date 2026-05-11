<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { registrarPartida } from '@/services/statsService';

const API_URL = import.meta.env.VITE_API_BASE_URL

const auth = useAuthStore()
const router = useRouter()

const COSTE_JUEGO = 10;


const numeroObjetivo = ref(0)
const bits = ref([0, 0, 0, 0]) 
const mensaje = ref('')
const juegoTerminado = ref(false)
const tiempo = ref(20)
let intervalo: any = null

const iniciarTemporizador = () => {
  tiempo.value = 10
  if (intervalo) clearInterval(intervalo)
  intervalo = setInterval(() => {
    if (tiempo.value > 0) {
      tiempo.value--
    } else {
      comprobarRespuesta()
    }
  }, 1000)
}

const generarNumero = () => {
  numeroObjetivo.value = Math.floor(Math.random() * 16)
  bits.value = [0, 0, 0, 0]
  mensaje.value = ''
  juegoTerminado.value = false
  iniciarTemporizador()
}

const cambiarBit = (index: number) => {
  if (juegoTerminado.value) return
  bits.value[index] = bits.value[index] === 0 ? 1 : 0
}

const comprobarRespuesta = async () => {
  if (intervalo) clearInterval(intervalo)
  if (juegoTerminado.value) return

  const resultadoDecimal = (bits.value[0]! * 8) + (bits.value[1]! * 4) + (bits.value[2]! * 2) + (bits.value[3]! * 1)
  
  let cantidad = 0

  if (resultadoDecimal === numeroObjetivo.value && tiempo.value > 0) {
    mensaje.value = "VALOR CORRECTO"
    cantidad = 10
  } else {
    mensaje.value = "ERROR"
    cantidad = -10
  }

  juegoTerminado.value = true

  try {
    const url = `${API_URL}/usuarios/${auth.jugador}/bytes?cantidad=${cantidad}`
    const response = await fetch(url, { method: 'PUT' })
    if (response.ok) {
      const usuario = await response.json()
      auth.bytes = usuario.saldoBytes

      if (typeof auth.guardarEnLocal === 'function') {
        auth.guardarEnLocal()
      }
    }
  } catch (error) {
    console.error("Error de red")
  }
  registrarPartida('Decibit', cantidad);
}

const cerrarSesion = () => {
  if (intervalo) clearInterval(intervalo)
  auth.logout()
  router.push('/login')
}

onMounted(() => {
    const bytesActuales = Number(auth.bytes) || 0;
  if (auth.bytes < COSTE_JUEGO) {
    alert(`ACCESO DENEGADO. Necesitas al menos los Bytes de entrada minimos para jugar a DeciBit.`);
    router.push('/home');
    return;
  }
})

onUnmounted(() => { if (intervalo) clearInterval(intervalo) }) //hace que se resetee el temporizador
</script>

<template>
  <div class="contenedor-principal">
    <header class="cabecera">
      <div class="logo">
        <span class="logo-bit">BIT</span>
        <span class="logo-hub">HUB</span>
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

    <main class="contenido">
      <div class="boton-volver" @click="router.push('/home')">
          ← Volver al Home
        </div>
      <section class="header-contenido">
        <p class="subtitulo">MÓDULO DE ENTRENAMIENTO</p>
        <h1 class="titulo-principal">DeciBit</h1>
      </section>

      <section class="panel-juego">
        <div class="tarjeta-central">
          <p style="font-family: monospace; font-weight: bold;">
            TEMPORIZADOR: <span style="color: #EF4444;">{{ tiempo }}s</span>
          </p>
          
          <p class="instruccion">CONVIERTE EL DECIMAL A BINARIO</p>
          <div class="numero-target">{{ numeroObjetivo }}</div>

          <div class="contenedor-bits">
            <div 
              v-for="(bit, index) in bits" :key="index"
              :class="['caja-bit', { 'activo': bit === 1 }]"
              @click="cambiarBit(index)" 
            >
              {{ bit }}
            </div>
          </div>

          <div class="acciones">
            <button v-if="!juegoTerminado" @click="comprobarRespuesta" class="btn-accion">COMPROBAR</button>
            <button v-else @click="generarNumero" class="btn-siguiente">CONTINUAR</button>
          </div>

          <p :class="['mensaje-sistema', { 'error': mensaje.includes('ERROR') }]">{{ mensaje }}</p>
        </div>
      </section>
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
}

.valor-bytes { 
  font-family: 'Consolas', monospace; 
  font-weight: bold; 
  color: #FACC15; 
}

.nombre-jugador { 
  font-family: 'Consolas', monospace; 
  font-size: 0.9rem; 
}

.boton-salir { 
  background: transparent; 
  border: 1px solid #334155; 
  color: #94A3B8; 
  padding: 8px 16px; 
  border-radius: 4px; 
  cursor: pointer; 
}

.contenido { 
  max-width: 1000px; 
  margin: 0 auto; 
  padding: 40px 20px; 
}

.boton-volver {
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748B;
  cursor: pointer;
  margin-bottom: 30px;
  display: inline-block;
}

.header-contenido { 
  text-align: center; 
  margin-bottom: 40px; 
}

.subtitulo { 
  font-family: 'Consolas', monospace; 
  font-size: 0.8rem; 
  color: #00E5FF; margin-bottom: 10px; 
  letter-spacing: 2px; 
}

.titulo-principal { font-size: 3rem; 
  font-weight: 800; 
  color: #F8FAFC; 
  margin: 0; 
}

.panel-juego { 
  display: flex; 
  justify-content: center; 
  width: 100%; 
}

.tarjeta-central {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 12px;
  padding: 50px 20px;
  width: 100%;
  max-width: 600px;
  text-align: center;
}

.timer-text { 
  font-family: 'Consolas', monospace; 
  font-weight: bold; 
  margin-bottom: 20px; 
}

.tiempo-valor { 
  color: #EF4444; 
}

.instruccion { 
  font-family: 'Consolas', monospace; 
  font-size: 0.85rem; 
  color: #64748B; 
  letter-spacing: 1px; 
  margin-bottom: 20px; 
}

.numero-target { font-family: 'Consolas', monospace; 
font-size: 6rem; 
font-weight: 900; 
color: #FACC15; 
margin-bottom: 30px; 
}

.contenedor-bits {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 40px;
}

.caja-bit {
  width: 70px;
  height: 70px;
  background-color: #1E2532;
  border: 2px solid #334155;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Consolas', monospace;
  font-size: 2rem;
  font-weight: bold;
  color: #64748B;
  cursor: pointer;
  transition: 0.2s;
}

.caja-bit.activo {
  border-color: #00E5FF;
  color: #00E5FF;
  box-shadow: 0 0 15px rgba(0, 229, 255, 0.2);
}

.btn-accion {
  background-color: #00E5FF;
  color: #0B0E14;
  border: none;
  padding: 16px 40px;
  font-weight: 800;
  border-radius: 4px;
  cursor: pointer;
}

.btn-siguiente {
  background: transparent;
  border: 2px solid #00E5FF;
  color: #00E5FF;
  padding: 16px 40px;
  font-weight: 800;
  border-radius: 4px;
  cursor: pointer;
}

.mensaje-sistema { 
  margin-top: 30px; 
  font-family: 'Consolas', monospace; 
  font-weight: bold; 
  color: #10B981; 
}

.mensaje-sistema.error { 
  color: #EF4444; 
}

@media (max-width: 1024px) {
  .cabecera { 
    height: auto; 
    flex-direction: column; 
    padding: 20px; 
    gap: 15px; 
  }

  .navegacion { 
    gap: 10px; 
  }

  .enlace { 
    padding: 5px 10px; 
  }

  .info-usuario { 
    flex-wrap: wrap; 
    justify-content: center; 
  }
}

@media (max-width: 600px) {
  .titulo-principal { 
    font-size: 2.2rem; 
  }

  .numero-target { 
    font-size: 4.5rem; 
  }

  .contenedor-bits {
     gap: 8px; 
    }

  .caja-bit { 
    width: 60px; 
    height: 60px; 
    font-size: 1.5rem; 
  }

  .tarjeta-central { 
    padding: 30px 15px; 
  }
}
</style>