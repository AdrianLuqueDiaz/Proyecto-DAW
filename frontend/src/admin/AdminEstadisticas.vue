<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth' 

const API_URL = import.meta.env.VITE_API_BASE_URL


const router = useRouter()
const auth = useAuthStore()


const listaJuegos = ref<any[]>([])

const mensajeAlerta = ref('')
const colorAlerta = ref('') 

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarJuegos = async () => {
  try {
    const respuesta = await fetch(`${API_URL}/estadisticas`)
    if (respuesta.ok) {
        listaJuegos.value = await respuesta.json()
    }
  } catch (error) {
    console.error('Error al cargar la lista de juegos:', error)
  }
}

onMounted(() => {
  cargarJuegos()
})


</script>

<template>
  <div class="contenedor-principal">
    
    <header class="cabecera">
      <div class="logo">
        <span class="logo-bit">BIT</span><span class="logo-hub">HUB</span>
      </div>

      <nav class="navegacion">
        <span class="enlace" @click="router.push('/home')">INICIO</span>
        <span class="enlace" @click="router.push('/clasificacion')">CLASIFICACIÓN</span>
        <span class="enlace activo">ADMINISTRACIÓN</span>
      </nav>

      <div class="info-usuario">
        <div class="bloque-bytes">
          <span class="etiqueta-bytes">BYTES</span>
          <span class="valor-bytes">{{ auth.bytes }}</span>
        </div>
        <span class="nombre-jugador">{{ auth.jugador || 'ADMIN' }}</span>
        <button class="boton-salir" @click="cerrarSesion">SALIR</button>
      </div>
    </header>

    <main class="contenido">
      <div class="boton-volver" @click="router.push('/home')">
          ← Volver al Home
        </div>
      <section class="contenido-header">
        <p class="subtitulo">HERRAMIENTAS DE ADMINISTRADOR</p>
        <h1 class="titulo-principal">Visualizador de Estadisticas</h1>
      </section>

      <div v-if="mensajeAlerta" :class="['mensaje-estado', 'alerta-global', colorAlerta]">
        {{ mensajeAlerta }}
      </div>
      

      <!-- Lista juegos -->
      <section class="panel-formulario panel-margen">
        <h2 class="titulo-seccion">Estadisticas</h2>
        
        <div v-if="listaJuegos.length === 0" class="mensaje-vacio">
          No hay estadisticas de juegos en la base de datos.
        </div>

        <div v-else class="contenedor-tabla">
          <table class="tabla-preguntas">
            <thead>
              <tr>
                <th>JUEGO</th>
                <th>PARTIDAS JUGADAS</th>
                <th>BYTES REPARTIDOS</th>
                
              </tr>
            </thead>
            <tbody>
                <tr v-for="juego in listaJuegos" :key="juego.id">
                    <td class="celda-id">{{ juego.nombreJuego }}</td> 
                    <td class="celda-enunciado">{{ juego.partidasJugadas }}</td>
                    <td class="valor-bytes">{{ juego.bytesRepartidos }}</td>
                </tr>
            </tbody>
          </table>
        </div>
      </section>

    </main>
  </div>
</template>

<style scoped>
.contenedor-principal {
  min-height: 100vh;
  background-color: #0B0E14;
  color: #E2E8F0;
  font-family: 'Inter', system-ui, sans-serif;
}

.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 80px;
  background-color: #11151D;
  border-bottom: 2px solid #1E2532;
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

.contenido {
  max-width: 1000px; 
  margin: 0 auto;
  padding: 60px 20px;
}

.contenido-header {
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

.boton-volver:hover {
color: #E2E8F0;
}

.subtitulo {
  font-family: 'Consolas', monospace;
  font-size: 0.8rem;
  color: #00E5FF;
  letter-spacing: 2px;
  margin: 0 0 10px 0;
}

.titulo-principal {
  font-size: 3rem;
  font-weight: 800;
  margin: 0;
  color: #F8FAFC;
  letter-spacing: -1px;
}

.titulo-seccion {
  font-size: 1.2rem;
  font-weight: 700;
  margin-top: 0;
  margin-bottom: 25px;
  color: #F8FAFC;
}

.panel-formulario {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 40px;
  margin-top: 30px;
}


.panel-margen {
  margin-top: 30px;
}

.alerta-global {
  margin-bottom: 30px;
}

.grupo-cajitas {
  margin-bottom: 25px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

label {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 1px;
  color: #94A3B8;
}

.etiqueta-correcta { color: #10B981; }
.etiqueta-falsa { color: #EF4444; }

.cajitas-texto {
  width: 100%;
  background-color: #0B0E14;
  border: 1px solid #1E2532;
  border-radius: 4px;
  padding: 14px 16px;
  color: #E2E8F0;
  font-size: 0.95rem;
  font-family: 'Consolas', monospace;
  outline: none;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.cajitas-texto:focus { border-color: #00E5FF; }

.area-texto {
  height: 100px;
  resize: vertical;
}

.cuadricula-opciones {
  display: grid;
  grid-template-columns: repeat(1, 2fr);
  gap: 20px;
  margin-bottom: 20px;
}

.boton-guardar {
  width: 100%;
  background-color: #E2E8F0;
  color: #0B0E14;
  border: none;
  border-radius: 4px;
  padding: 16px;
  font-weight: 800;
  font-size: 0.95rem;
  letter-spacing: 1px;
  cursor: pointer;
  margin-top: 10px;
  transition: transform 0.1s ease, background 0.2s ease;
}

.boton-guardar:hover {
  background-color: #00E5FF;
  transform: translateY(-2px);
}

.mensaje-estado {
  padding: 15px;
  text-align: center;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 600;
}

.success {
  color: #10B981;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid #10B981;
}

.error {
  color: #EF4444;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid #EF4444;
}




.mensaje-vacio {
  text-align: center;
  color: #64748B;
  font-style: italic;
  padding: 20px 0;
}

.contenedor-tabla {
  overflow-x: auto;
  border: 1px solid #1E2532;
  border-radius: 6px;
  background-color: #0B0E14;
}

.tabla-preguntas {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.9rem;
}

.tabla-preguntas th {
  padding: 16px;
  background-color: #11151D;
  color: #94A3B8;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 1px;
  border-bottom: 2px solid #1E2532;
}

.tabla-preguntas td {
  padding: 16px;
  border-bottom: 1px solid #1E2532;
  color: #E2E8F0;
}

.tabla-preguntas tbody tr:last-child td {
  border-bottom: none;
}

.tabla-preguntas tbody tr:hover {
  background-color: #131823;
}

.celda-id {
  font-family: 'Consolas', monospace;
  color: #00E5FF;
  font-weight: bold;
}

.celda-enunciado {
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.celda-accion {
  text-align: right;
}

.boton-borrar-fila {
  background-color: transparent;
  color: #EF4444;
  border: 1px solid #EF4444;
  border-radius: 4px;
  padding: 8px 16px;
  font-weight: 600;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.boton-borrar-fila:hover {
  background-color: #EF4444;
  color: #fff;
}
</style>