<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

// Definimos el tipo exacto para que TypeScript sea estricto
interface Carta {
  texto: string;
  valor: number;
}

// 1. La baraja esperando a Java
const cartasPosibles = ref<Carta[]>([])

// 2. Las manos de los dos jugadores
const miMano = ref<Carta[]>([])
const manoBanca = ref<Carta[]>([])

const estado = ref<string>('jugando') // jugando, evaluando, ganado, perdido, empate

const totalGB = computed<number>(() => {
  return miMano.value.reduce((suma, c) => suma + c.valor, 0)
})

const totalBanca = computed<number>(() => {
  return manoBanca.value.reduce((suma, c) => suma + c.valor, 0)
})

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarCartas = async () => {
  try {
    const respuesta = await fetch('http://localhost:8080/api/codejack/cartas')
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
    const respuesta = await fetch(`http://localhost:8080/api/usuarios/${auth.jugador}/bytes?cantidad=${cantidad}`, {
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
  }
}

const plantarse = () => {
  estado.value = 'evaluando' 

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
  } else if (totalGB.value > totalBanca.value) {
    estado.value = 'ganado' 
    actualizarBytes(75) 
  } else if (totalGB.value < totalBanca.value) {
    estado.value = 'perdido' 
    actualizarBytes(-50) 
  } else {
    estado.value = 'empate' 
    actualizarBytes(0) 
  }
}

const nuevaRonda = () => {
  miMano.value = []
  manoBanca.value = []
  estado.value = 'jugando'
}

onMounted(() => {
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
        <span v-if="auth.rol === 'ADMIN'" class="enlace" @click="router.push('/preguntas')">PREGUNTAS</span>
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
          <h4 class="info-titulo">Guía de Conversión</h4>
          <ul class="info-lista">
            <li><span class="valor-base">1024 MB</span> <span class="separador">→</span> <span class="valor-final">1 GB</span></li>
            <li><span class="valor-base">2048 MB</span> <span class="separador">→</span> <span class="valor-final">2 GB</span></li>
            <li><span class="valor-base">512 MB</span> <span class="separador">→</span> <span class="valor-final">0.5 GB</span></li>
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
/* ESTILOS GENERALES Y CABECERA */
.contenedor-principal {
  min-height: 100vh; background-color: #0B0E14; color: #E2E8F0;
  font-family: 'Inter', system-ui, sans-serif; display: flex; flex-direction: column;
}

.cabecera {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 40px; height: 80px; background-color: #11151D; border-bottom: 2px solid #1E2532; flex-shrink: 0;
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

.logo { font-family: 'Consolas', monospace; font-size: 1.5rem; font-weight: 900; letter-spacing: 2px; }
.logo-bit { color: #E2E8F0; }
.logo-hub { color: #00E5FF; }

.navegacion { display: flex; height: 100%; }
.enlace { display: flex; align-items: center; padding: 0 20px; font-size: 0.85rem; font-weight: 600; letter-spacing: 1px; color: #64748B; cursor: pointer; transition: all 0.2s ease; border-bottom: 2px solid transparent; }
.enlace:hover { color: #E2E8F0; }
.enlace.activo { color: #00E5FF; border-bottom: 2px solid #00E5FF; }

.info-usuario { display: flex; align-items: center; gap: 25px; }
.bloque-bytes { display: flex; align-items: baseline; gap: 8px; background: #1E2532; padding: 6px 12px; border-radius: 4px; }
.etiqueta-bytes { font-size: 0.7rem; font-weight: 700; color: #64748B; letter-spacing: 1px; }
.valor-bytes { font-family: 'Consolas', monospace; font-weight: bold; color: #FACC15; }
.nombre-jugador { font-family: 'Consolas', monospace; font-size: 0.9rem; color: #E2E8F0; }

.boton-salir { background: transparent; border: 1px solid #334155; color: #94A3B8; padding: 8px 16px; font-size: 0.75rem; font-weight: bold; letter-spacing: 1px; border-radius: 4px; cursor: pointer; transition: all 0.2s; }
.boton-salir:hover { background: #EF4444; border-color: #EF4444; color: #fff; }

/* ÁREA DE JUEGO PRINCIPAL */
.contenido-juego {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 40px 20px;
  gap: 40px;
  position: relative;
}

/* PANEL LATERAL (CHULETA) */
.panel-lateral {
  width: 250px;
  flex-shrink: 0;
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

.info-lista {
  list-style: none;
  padding: 0; margin: 0;
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

.valor-base { color: #94A3B8; }
.separador { color: #334155; }
.valor-final { color: #00E5FF; font-weight: bold; }

/* MESA CENTRAL */
.mesa-central {
  width: 100%;
  max-width: 700px;
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

.tablero-juego {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 40px;
  margin-bottom: 30px;
}

.etiqueta-estado {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #0B0E14;
  padding: 12px 20px;
  border-radius: 4px;
  border: 1px solid #1E2532;
  margin-bottom: 20px;
}

.etiqueta-estado .nombre {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 1px;
  color: #64748B;
}

.etiqueta-estado {
  font-family: 'Consolas', monospace;
  font-size: 1.1rem;
  font-weight: bold;
  color: #E2E8F0;
}

.tu-turno {
  border-color: #334155;
}

.tu-turno .nombre { color: #00E5FF; }

.memoria {
  color: #EF4444 !important;
  font-size: 0.9rem !important;
}

.divisor {
  border: 0;
  height: 1px;
  background-color: #1E2532;
  margin: 30px 0;
}

/* CARTAS */
.zona-cartas {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  min-height: 140px;
}

.zona-cartas.mini { min-height: 90px; }

.carta-dato {
  width: 90px;
  height: 130px;
  background-color: #1E2532;
  border: 1px solid #334155;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.2s ease, border-color 0.2s ease;
}

.zona-cartas.mini .carta-dato {
  width: 60px; height: 80px;
  background-color: #0B0E14;
}

.carta-dato:hover {
  transform: translateY(-5px);
  border-color: #00E5FF;
}

.carta-interior {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.icono-dato {
  font-size: 1.5rem;
  color: #64748B;
}

.texto-carta {
  font-family: 'Consolas', monospace;
  font-weight: bold;
  font-size: 0.9rem;
  color: #F8FAFC;
}

.mini .texto-carta { font-size: 0.75rem; color: #94A3B8; }

/* MENSAJES DE RESULTADO */
.resultados {
  margin-bottom: 30px;
}

.mensaje {
  padding: 20px;
  border-radius: 6px;
  text-align: center;
}

.mensaje h3 { margin: 0 0 5px 0; font-size: 1.2rem; font-weight: 800; }
.mensaje p { margin: 0; font-size: 0.9rem; }

.exito { background: rgba(16, 185, 129, 0.1); border: 1px solid #10B981; color: #10B981; }
.error { background: rgba(239, 68, 68, 0.1); border: 1px solid #EF4444; color: #EF4444; }
.neutral { background: rgba(250, 204, 21, 0.1); border: 1px solid #FACC15; color: #FACC15; }

/* CONTROLES */
.controles {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.boton-primario {
  background-color: #E2E8F0;
  color: #0B0E14;
  border: none;
  border-radius: 4px;
  padding: 14px 28px;
  font-weight: 800;
  font-size: 0.85rem;
  letter-spacing: 1px;
  cursor: pointer;
  transition: transform 0.1s ease, background 0.2s ease;
}

.boton-primario:hover { background-color: #00E5FF; transform: translateY(-2px); }

.boton-secundario {
  background: transparent;
  border: 1px solid #334155;
  color: #94A3B8;
  border-radius: 4px;
  padding: 14px 28px;
  font-weight: 800;
  font-size: 0.85rem;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.boton-secundario:hover { border-color: #E2E8F0; color: #E2E8F0; }
</style>