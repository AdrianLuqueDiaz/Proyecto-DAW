<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const cartas = ref<any[]>([])
const seleccionadas = ref<any[]>([])
const bloquearTablero = ref(false)

const preguntaActual = ref<any>(null)
const mostrarPregunta = ref(false)
const opcionesRespuestas = ref<string[]>([])
const cartasEnJuego = ref<any[]>([])

const juegoTerminado = computed(() => {
  return cartas.value.length > 0 && cartas.value.every(c => c.resuelta)
})

const cargarCartas = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/cartas')
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
  const [c1, c2] = seleccionadas.value

  if (c1.idPareja === c2.idPareja) {
    cartasEnJuego.value = [c1, c2]
    lanzarPregunta(c1.idPareja)
  } else {
    setTimeout(() => {
      c1.girada = false
      c2.girada = false
      soltarTurno()
    }, 1000)
  }
}

const lanzarPregunta = async (idPareja: number) => {
  try {
    const res = await fetch(`http://localhost:8080/api/preguntas/${idPareja}`)
    const pregunta = await res.json()
    
    if (pregunta) {
      preguntaActual.value = pregunta
      opcionesRespuestas.value = [
        pregunta.respuestaCorrecta, 
        pregunta.falsa1, 
        pregunta.falsa2, 
        pregunta.falsa3
      ].sort(() => Math.random() - 0.5)
      
      mostrarPregunta.value = true
    } else {
      resolverCartasEnJuego(true)
    }
  } catch (error) {
    console.error(error)
    resolverCartasEnJuego(true)
  }
}

const responder = (opcionSeleccionada: string) => {
  const esCorrecta = opcionSeleccionada === preguntaActual.value.respuestaCorrecta
  resolverCartasEnJuego(esCorrecta)
  mostrarPregunta.value = false
}

const resolverCartasEnJuego = (exito: boolean) => {
  const [c1, c2] = cartasEnJuego.value

  if (exito) {
    c1.resuelta = true
    c2.resuelta = true
  } else {
    c1.girada = false
    c2.girada = false
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
  cargarCartas()
}

onMounted(cargarCartas)
</script>

<template>
  <div class="contenedor-juego">
    
    <div class="cabecera-juego">
      <button @click="router.push('/home')" class="btn-volver"><< VOLVER_AL_HUB</button>
      <h2 class="titulo-neon">MEMORY_LEAK // FASE_1</h2>
    </div>

    <div v-if="cartas.length === 0" class="texto-cargando">
      > CONECTANDO..
    </div>

    <div class="cuadricula-tablero">
      <div 
        v-for="carta in cartas" 
        :key="carta.id" 
        class="carta-hex"
        :class="{ 'esta-girada': carta.girada || carta.resuelta, 'esta-resuelta': carta.resuelta }"
        @click="seleccionarCarta(carta)"
      >
        <div class="carta-interior">
          <div class="carta-frontal">
            <span class="icono-glitch">?</span>
          </div>
          <div class="carta-dorso">
            <span class="texto-carta">{{ carta.texto }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="mostrarPregunta" class="capa-modal">
      <div class="modal-ciber modal-pregunta">
        <h3 class="titulo-modal titulo-alerta">¡CORTAFUEGOS DETECTADO!</h3>
        <p class="texto-modal texto-pregunta">{{ preguntaActual.enunciado }}</p>
        
        <div class="cuadricula-respuestas">
          <button 
            v-for="(opcion, index) in opcionesRespuestas" 
            :key="index"
            @click="responder(opcion)"
            class="btn-respuesta"
          >
            {{ opcion }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="juegoTerminado && !mostrarPregunta" class="capa-modal">
      <div class="modal-ciber">
        <h3 class="titulo-modal">SISTEMA_RESTAURADO</h3>
        <p class="texto-modal">> Fugas reparadas.</p>
        <div class="acciones-modal">
          <button @click="reiniciarJuego" class="btn-accion">REINICIAR()</button>
          <button @click="router.push('/home')" class="btn-salir">SALIR</button>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.contenedor-juego {
  min-height: 100vh; 
  background: #020808; 
  padding: 40px;
  display: flex; 
  flex-direction: column; 
  align-items: center;
  font-family: 'Consolas', monospace;
}

.cabecera-juego { 
  width: 100%; 
  max-width: 800px; 
  margin-bottom: 20px; 
}

.titulo-neon { 
  color: #00ffcc; 
  text-shadow: 0 0 15px #00ffcc; 
  font-size: 2rem; 
  margin: 0; 
}

.btn-volver { 
  background: transparent; 
  border: none; 
  color: #558888; 
  cursor: pointer; 
  margin-bottom: 10px; 
}

.btn-volver:hover { 
  color: #00ffcc; 
}

.texto-cargando { 
  color: #ff0055; 
  margin-top: 50px; 
  font-size: 1.2rem; 
  animation: parpadeo 1s infinite; 
}

@keyframes parpadeo { 
  0%, 100% { opacity: 1; } 
  50% { opacity: 0; } 
}

.cuadricula-tablero {
  display: grid; 
  grid-template-columns: repeat(4, 1fr); 
  gap: 20px;
  margin-top: 50px; 
  max-width: 800px;
}

.carta-hex { 
  width: 150px; 
  height: 180px; 
  perspective: 1000px; 
  cursor: pointer; 
}

.carta-interior {
  position: relative; 
  width: 100%; 
  height: 100%;
  transition: transform 0.6s; 
  transform-style: preserve-3d;
}

.carta-hex.esta-girada .carta-interior { 
  transform: rotateY(180deg); 
}

.carta-frontal, .carta-dorso {
  position: absolute; 
  width: 100%; 
  height: 100%; 
  backface-visibility: hidden;
  display: flex; 
  justify-content: center; 
  align-items: center;
  box-sizing: border-box; 
  border-radius: 4px;
}

.carta-frontal {
  background: rgba(0, 20, 20, 0.8); 
  border: 2px solid #1a3333;
  color: #00ffcc; 
  font-size: 3rem; 
  text-shadow: 0 0 10px rgba(0, 255, 204, 0.5);
}

.carta-dorso {
  transform: rotateY(180deg); 
  background: rgba(0, 255, 204, 0.1);
  border: 2px solid #00ffcc; 
  color: #fff; 
  font-weight: bold; 
  text-align: center; 
  padding: 10px;
}

.esta-resuelta .carta-dorso {
  background: rgba(255, 215, 0, 0.1); 
  border-color: #ffd700; 
  color: #ffd700;
  box-shadow: 0 0 15px #ffd700;
}

.capa-modal {
  position: fixed; 
  top: 0; left: 0; 
  width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.85); 
  backdrop-filter: blur(5px);
  display: flex; 
  justify-content: center; 
  align-items: center; 
  z-index: 100;
}

.modal-ciber {
  background: rgba(0, 10, 10, 0.95); 
  border: 2px solid #00ffcc;
  box-shadow: 0 0 30px rgba(0, 255, 204, 0.3); 
  padding: 40px; 
  text-align: center; 
  max-width: 400px;
}

.modal-pregunta { 
  max-width: 600px; 
  border-color: #ff0055; 
  box-shadow: 0 0 30px rgba(255, 0, 85, 0.3); 
}

.titulo-alerta { 
  color: #ff0055 !important; 
  text-shadow: 0 0 10px #ff0055 !important; 
}

.texto-pregunta { 
  font-size: 1.2rem; 
  margin-bottom: 30px; 
  line-height: 1.5; 
}

.cuadricula-respuestas { 
  display: grid; 
  grid-template-columns: 1fr 1fr; 
  gap: 15px; 
}

.btn-respuesta {
  background: rgba(0, 255, 204, 0.05); 
  border: 1px solid #00ffcc; 
  color: #00ffcc;
  padding: 15px; 
  cursor: pointer; 
  transition: 0.2s; 
  font-family: inherit; 
  font-size: 1rem;
}

.btn-respuesta:hover { 
  background: rgba(0, 255, 204, 0.2); 
  box-shadow: 0 0 10px rgba(0, 255, 204, 0.5); 
}

.titulo-modal { 
  color: #00ffcc; 
  font-size: 1.5rem; 
  margin-top: 0; 
  text-shadow: 0 0 10px #00ffcc; 
}

.texto-modal { 
  color: #e0fbfb; 
  margin: 20px 0 40px 0; 
}

.acciones-modal { 
  display: flex; 
  justify-content: center; 
  gap: 20px; 
}

.btn-accion, .btn-salir { 
  padding: 10px 20px; 
  cursor: pointer; 
  transition: 0.3s; 
  font-family: inherit;
}

.btn-accion { 
  background: rgba(0, 255, 204, 0.1); 
  border: 1px solid #00ffcc; 
  color: #00ffcc; 
}

.btn-accion:hover { 
  background: #00ffcc; 
  color: #000; 
  box-shadow: 0 0 15px #00ffcc; 
}

.btn-salir { 
  background: transparent; 
  border: 1px solid #ff0055; 
  color: #ff0055; 
}

.btn-salir:hover { 
  background: #ff0055; 
  color: #fff; 
  box-shadow: 0 0 15px #ff0055; 
}
</style>