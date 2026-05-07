<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const API_URL = import.meta.env.VITE_API_BASE_URL

const nuevoUsuario = ref({
  alias: '',
  correo: '',
  contrasena: ''
})

const mensajeAlerta = ref('')
const colorAlerta = ref('') 

const registrarUsuario = async () => {
  if (!nuevoUsuario.value.alias || !nuevoUsuario.value.correo || !nuevoUsuario.value.contrasena) {
    mensajeAlerta.value = 'Error: Faltan credenciales obligatorias'
    colorAlerta.value = 'error'
    return
  }

  try {
    const respuesta = await fetch(`${API_URL}/usuarios/registro`, {
      method: 'POST', 
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(nuevoUsuario.value) 
    })

    if (respuesta.ok) {
      mensajeAlerta.value = 'Registro completado. Redirigiendo...'
      colorAlerta.value = 'exito'
      
      setTimeout(() => {
        router.push('/login')
      }, 2000)
      
    } else {
      mensajeAlerta.value = 'Error: El servidor rechazó los datos'
      colorAlerta.value = 'error'
    }

  } catch (error) {
    console.error(error)
    mensajeAlerta.value = 'Error: Imposible conectar al servidor'
    colorAlerta.value = 'error'
  }
}
</script>

<template>
  <div class="contenedor">
    <div class="tarjeta">
      
      <div class="cabecera-tarjeta">
        <div class="logo">
          <span class="logo-bit">BIT</span><span class="logo-hub">HUB</span>
        </div>
      </div>
      
      <div class="contenido-tarjeta">
        <h1 class="titulo-principal">Crear Cuenta</h1>
        <p class="subtitulo">Registra un nuevo usuario en el sistema</p>

        <div class="credenciales">
          <label>ALIAS</label>
          <input v-model="nuevoUsuario.alias" placeholder="Introduce tu alias" />
        </div>

        <div class="credenciales">
          <label>CORREO ELECTRÓNICO</label>
          <input v-model="nuevoUsuario.correo" type="email" placeholder="usuario@bithub.com" />
        </div>

        <div class="credenciales">
          <label>CONTRASEÑA</label>
          <input v-model="nuevoUsuario.contrasena" type="password" placeholder="••••••••" @keyup.enter="registrarUsuario"/>
        </div>

        <button @click="registrarUsuario" class="btn-conectar">
          CREAR CUENTA
        </button>

        <div class="enlace-volver" @click="router.push('/login')">
          ¿Ya tienes cuenta? Iniciar Sesión
        </div>

        <div v-if="mensajeAlerta" :class="['mensaje-estado', colorAlerta]">
          {{ mensajeAlerta }}
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>

.contenedor {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #0B0E14;
  font-family: 'Inter', system-ui, sans-serif;
  color: #E2E8F0;
  padding: 20px;
  box-sizing: border-box;
}

.tarjeta {
  width: 100%;
  max-width: 400px;
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.cabecera-tarjeta {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 25px;
  background-color: #11151D;
  border-bottom: 1px solid #1E2532;
}

.logo {
  font-family: 'Consolas', monospace;
  font-size: 1.2rem;
  font-weight: 900;
  letter-spacing: 2px;
}

.logo-bit { color: #E2E8F0; }
.logo-hub { color: #00E5FF; }

.contenido-tarjeta {
  padding: 30px 20px;
}

@media (min-width: 480px) {
  .contenido-tarjeta {
    padding: 40px 30px;
  }
}

.titulo-principal {
  font-size: 1.4rem;
  font-weight: 800;
  margin: 0 0 8px 0;
  color: #F8FAFC;
  text-align: center;
}

.subtitulo {
  font-size: 0.85rem;
  color: #64748B;
  margin: 0 0 30px 0;
  text-align: center;
  line-height: 1.4;
}

.credenciales {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.credenciales label {
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 1px;
  color: #94A3B8;
}

input {
  width: 100%;
  background-color: #0B0E14;
  border: 1px solid #1E2532;
  border-radius: 4px;
  padding: 12px 15px;
  color: #E2E8F0;
  font-size: 1rem;
  font-family: 'Inter', sans-serif;
  outline: none;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

input:focus {
  border-color: #00E5FF;
  background-color: #11151D;
}

.btn-conectar {
  width: 100%;
  background-color: #E2E8F0;
  color: #0B0E14;
  border: none;
  border-radius: 4px;
  padding: 14px;
  font-weight: 800;
  font-size: 0.9rem;
  letter-spacing: 1px;
  cursor: pointer;
  margin-top: 10px;
  transition: all 0.2s ease;
}

@media (hover: hover) {
  .btn-conectar:hover {
    background-color: #00E5FF;
    transform: translateY(-2px);
  }
}

.btn-conectar:active {
  transform: scale(0.98);
}

.enlace-volver {
  text-align: center;
  margin-top: 25px;
  font-size: 0.85rem;
  color: #64748B;
  cursor: pointer;
  transition: color 0.2s ease;
  padding: 10px;
}

.enlace-volver:hover {
  color: #E2E8F0;
}

.mensaje-estado {
  margin-top: 20px;
  padding: 12px;
  text-align: center;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 600;
}

.exito {
  color: #10B981; 
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid #10B981;
}

.error {
  color: #EF4444;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid #EF4444;
}
</style>