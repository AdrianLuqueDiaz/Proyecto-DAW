<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const alias = ref('')
const contrasena = ref('')

const entrar = async () => {
  if (!alias.value || !contrasena.value) {
    alert("ERROR: Credenciales incompletas")
    return
  }

  try {
    const respuesta = await fetch('http://localhost:8080/api/usuarios/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        alias: alias.value,
        contrasena: contrasena.value
      })
    })

    if (respuesta.ok) {
      const usuario = await respuesta.json()

      auth.setUsuarioLogin(usuario)
      router.push('/home') 
    } else {
      alert("ERROR: Credenciales incorrectas o usuario no encontrado.")
    }

  } catch (error) {
    console.error(error)
    alert("ERROR: Imposible conectar con el servidor.")
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
        <h1 class="titulo-principal">Iniciar Sesión</h1>
        <p class="subtitulo">Accede a tu cuenta para continuar</p>

        <div class="credenciales">
          <label>ALIAS</label>
          <input v-model="alias" placeholder="Introduce tu alias" />
        </div>

        <div class="credenciales">
          <label>CONTRASEÑA</label>
          <input v-model="contrasena" type="password" placeholder="••••••••" @keyup.enter="entrar"/>
        </div>

        <button @click="entrar" class="btn-conectar">
          CONECTAR
        </button>

        <div class="enlace-registro" @click="router.push('/registro')">
          ¿No tienes cuenta? Crear un usuario nuevo
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

.enlace-registro {
  text-align: center;
  margin-top: 25px;
  font-size: 0.85rem;
  color: #64748B;
  cursor: pointer;
  transition: color 0.2s ease;
  padding: 10px;
}

.enlace-registro:hover {
  color: #E2E8F0;
}
</style>