<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth' 

const API_URL = import.meta.env.VITE_API_BASE_URL

const router = useRouter()
const auth = useAuthStore()

const preguntaNueva = ref({
  idPareja: 100, 
  enunciado: '',
  respuestaCorrecta: '',
  falsa1: '',
  falsa2: '',
  falsa3: ''
})

const listaPreguntas = ref<any[]>([])

const mensajeAlerta = ref('')
const colorAlerta = ref('') 

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

// Cargar todas las preguntas al entrar en la página
const cargarPreguntas = async () => {
  try {
    const respuesta = await fetch(`${API_URL}/preguntas`)
    if (respuesta.ok) {
      listaPreguntas.value = await respuesta.json()
    }
  } catch (error) {
    console.error('Error al cargar la lista de preguntas:', error)
  }
}

onMounted(() => {
  cargarPreguntas()
})

const guardarPregunta = async () => {
  try {
    const respuesta = await fetch(`${API_URL}/preguntas/add`, {
      method: 'POST', 
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(preguntaNueva.value) 
    })

    if (respuesta.ok) {
      mensajeAlerta.value = 'Pregunta guardada correctamente.'
      colorAlerta.value = 'success'
      
      preguntaNueva.value.enunciado = ''
      preguntaNueva.value.respuestaCorrecta = ''
      preguntaNueva.value.falsa1 = ''
      preguntaNueva.value.falsa2 = ''
      preguntaNueva.value.falsa3 = ''

      // recargar la tabla para que salga la nueva
      await cargarPreguntas()
    } else {
      mensajeAlerta.value = 'Error: El servidor no pudo procesar los datos.'
      colorAlerta.value = 'error'
    }

  } catch (error) {
    console.error(error)
    mensajeAlerta.value = 'Error: Imposible conectar con el servidor central.'
    colorAlerta.value = 'error'
  }
}

const borrarPregunta = async (id: number) => {
  if (
    !confirm(`¿Estás seguro de que quieres eliminar la pregunta?`)
  )
    return

  try {
    const respuesta = await fetch(`${API_URL}/preguntas/${id}`, {
      method: 'DELETE', 
    })

    if (respuesta.ok) {
      mensajeAlerta.value = `Pregunta eliminada correctamente.`
      colorAlerta.value = 'success'
      
      // Refrescar la tabla para que desaparezca la borrada
      await cargarPreguntas()
    } else {
      mensajeAlerta.value = 'Error: No se encontró la pregunta o hubo un problema en el servidor.'
      colorAlerta.value = 'error'
    }

  } catch (error) {
    console.error(error)
    mensajeAlerta.value = 'Error: Imposible conectar con el servidor central.'
    colorAlerta.value = 'error'
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
        <h1 class="titulo-principal">Gestión de Datos</h1>
      </section>

      <div v-if="mensajeAlerta" :class="['mensaje-estado', 'alerta-global', colorAlerta]">
        {{ mensajeAlerta }}
      </div>

      <!-- Crear pregunta -->
      <section class="panel-formulario">
        <h2 class="titulo-seccion">Añadir Nueva Pregunta</h2>
        <form @submit.prevent="guardarPregunta" class="formulario">
          
          <div class="fila-formulario">
            <div class="grupo-cajitas">
              <label>CATEGORÍA</label>
              <select v-model="preguntaNueva.idPareja" class="cajitas-texto">
                <option :value="100">STRINGS (100)</option>
                <option :value="200">BOOLEANS (200)</option>
                <option :value="300">ARRAYS (300)</option>
                <option :value="400">NUMBERS (400)</option>
                <option :value="500">FUNCTIONS (500)</option>
                <option :value="600">OBJECTS (600)</option>
              </select>
            </div>
          </div>

          <div class="grupo-cajitas">
            <label>ENUNCIADO DEL PROBLEMA</label>
            <textarea v-model="preguntaNueva.enunciado" class="cajitas-texto area-texto" placeholder="Escribe aquí la expresión o problema a resolver..."></textarea>
          </div>

          <div class="grupo-cajitas">
            <label class="etiqueta-correcta">SOLUCIÓN</label>
            <input v-model="preguntaNueva.respuestaCorrecta" type="text" class="cajitas-texto" placeholder="Introduce la respuesta" />
          </div>

          <div class="cuadricula-opciones">
            <div class="grupo-cajitas">
              <label class="etiqueta-falsa">OPCIÓN FALSA 01</label>
              <input v-model="preguntaNueva.falsa1" type="text" class="cajitas-texto" placeholder="Miente" />
            </div>
            <div class="grupo-cajitas">
              <label class="etiqueta-falsa">OPCIÓN FALSA 02</label>
              <input v-model="preguntaNueva.falsa2" type="text" class="cajitas-texto" placeholder="Engaña" />
            </div>
            <div class="grupo-cajitas">
              <label class="etiqueta-falsa">OPCIÓN FALSA 03</label>
              <input v-model="preguntaNueva.falsa3" type="text" class="cajitas-texto" placeholder="Inventa" />
            </div>
          </div>

          <button type="submit" class="boton-guardar">
            GUARDAR PREGUNTA
          </button>
        </form>
      </section>

      <!-- Eliminar pregunta -->
      <section class="panel-formulario panel-margen">
        <h2 class="titulo-seccion">Preguntas Existentes</h2>
        
        <div v-if="listaPreguntas.length === 0" class="mensaje-vacio">
          No hay preguntas guardadas en la base de datos.
        </div>

        <div v-else class="contenedor-tabla">
          <table class="tabla-preguntas">
            <thead>
              <tr>
                <th>CATEGORÍA</th>
                <th>ENUNCIADO</th>
                <th class="celda-accion">ACCIÓN</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="pregunta in listaPreguntas" :key="pregunta.id">
                <td>{{ pregunta.idPareja }}</td>
                <td class="celda-enunciado">{{ pregunta.enunciado }}</td>
                <td class="celda-accion">
                  <button class="boton-borrar-fila" @click="borrarPregunta(pregunta.id)">
                    Eliminar
                  </button>
                </td>
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
  width: 100%;
  background-color: #0B0E14;
  color: #E2E8F0;
  font-family: 'Inter', system-ui, sans-serif;
  margin: 0;
  padding: 0;
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
  box-sizing: border-box;
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
  color: #bac8db;
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
  color: #bac8db;
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
  border: 1px solid #bac8db;
  color: #bac8db;
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
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  padding: 60px 20px;
  box-sizing: border-box;
}

.contenido-header {
  margin-bottom: 30px;
}

.boton-volver {
  display: inline-block;
  font-size: 0.8rem;
  font-weight: 600;
  color: #bac8db;
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
  box-sizing: border-box;
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

.etiqueta-correcta {
  color: #10B981;
}

.etiqueta-falsa {
  color: #EF4444;
}

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

.cajitas-texto:focus {
  border-color: #00E5FF;
}

.area-texto {
  height: 100px;
  resize: vertical;
}

.cuadricula-opciones {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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
  width: 100%;
}

.tabla-preguntas {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 0.9rem;
  min-width: 600px;
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

@media (max-width: 900px) {
  .titulo-principal {
    font-size: 2.5rem;
  }
  .cuadricula-opciones {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  .panel-formulario {
    padding: 25px;
  }
}

@media (max-width: 768px) {
  .cabecera {
    flex-direction: column;
    height: auto;
    padding: 20px;
    gap: 15px;
  }
  .navegacion {
    height: auto;
  }
  .enlace {
    padding: 10px;
  }
  .info-usuario {
    width: 100%;
    justify-content: center;
    gap: 15px;
  }
  .titulo-principal {
    font-size: 2rem;
  }
  .contenido {
    padding: 40px 20px;
  }
}
</style>