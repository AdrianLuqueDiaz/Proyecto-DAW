<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { registrarPartida } from '@/services/statsService'

const router = useRouter()
const auth = useAuthStore()
const API_URL = import.meta.env.VITE_API_BASE_URL

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
            <li><span class="valor-base">Acierto</span> <span class="separador">→</span> <span class="valor-final">+50 Bytes</span></li>
            <li><span class="valor-base">Fallo</span> <span class="separador">→</span> <span class="valor-error">-10 Bytes</span></li>
          </ul>
        </div>
      </aside>

      <div class="mesa-central">
        
        <div class="cabecera-juego">
          <h1 class="titulo-juego">Logic Link</h1>
          <p class="subtitulo-juego">Reconstruye los fragmentos de código dañados.</p>
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
  letter-spacing: 1px;
  color: #64748B;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
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
  display: inline-block;
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748B;
  cursor: pointer;
  margin-bottom: 15px;
  transition: color 0.2s;
}

.boton-volver:hover {
  color: #E2E8F0;
}

.tarjeta-info {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 20px;
}

.info-titulo {
  font-size: 0.75rem;
  font-weight: 800;
  letter-spacing: 1px;
  color: #64748B;
  margin: 0 0 15px 0;
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
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-lista li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-family: 'Consolas', monospace;
  font-size: 0.85rem;
  background: #0B0E14;
  padding: 8px 12px;
  border-radius: 4px;
}

.valor-base {
  color: #94A3B8;
}

.separador {
  color: #334155;
}

.valor-final {
  color: #00E5FF;
  font-weight: bold;
}

.valor-error {
  color: #EF4444;
  font-weight: bold;
}

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
  margin: 0 0 5px 0;
  color: #F8FAFC;
  letter-spacing: -1px;
}

.subtitulo-juego {
  font-size: 0.95rem;
  color: #64748B;
  margin: 0;
}

</style>