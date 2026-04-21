<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const nuevoUsuario = ref({
  alias: '',
  correo: '',
  contrasena: ''
})

const mensajeAlerta = ref('')
const colorAlerta = ref('') 

const registrarAgente = async () => {
  if (!nuevoUsuario.value.alias || !nuevoUsuario.value.correo || !nuevoUsuario.value.contrasena) {
    mensajeAlerta.value = '>> ERROR: Faltan credenciales'
    colorAlerta.value = 'error'
    return
  }

  try {
    const respuesta = await fetch('http://localhost:8080/api/usuarios/registro', {
      method: 'POST', 
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(nuevoUsuario.value) 
    })

    if (respuesta.ok) {
      mensajeAlerta.value = '>> DATA_ACCEPTED: AGENTE_REGISTRADO'
      colorAlerta.value = 'success'
      
      setTimeout(() => {
        router.push('/login')
      }, 2000)
      
    } else {
      mensajeAlerta.value = '>> ERROR: Servidor rechazó los datos'
      colorAlerta.value = 'error'
    }

  } catch (error) {
    console.error(error)
    mensajeAlerta.value = '>> ERROR: Imposible conectar al servidor'
    colorAlerta.value = 'error'
  }
}
</script>

<template>
  <div class="login-wrapper">
    <div class="cyber-card">
      <div class="card-header">
        <span class="status-dot"></span>
        <span class="system-text">NODE_REG_PROTOCOL // STACK_ARCADE</span>
      </div>
      
      <div class="card-content">
        <h1 class="neon-title">NEW_AGENT</h1>
        <p class="flavor-text">REGISTRO DE NUEVO DESARROLLADOR</p>

        <div class="input-field">
          <div class="label">DEVELOPER_ID (ALIAS)</div>
          <input v-model="nuevoUsuario.alias" placeholder="> GAMER_TAG" />
        </div>

        <div class="input-field">
          <div class="label">CONTACT_NODE (CORREO)</div>
          <input v-model="nuevoUsuario.correo" type="email" placeholder="> agente@bithub.com" />
        </div>

        <div class="input-field">
          <div class="label">SECURITY_TOKEN (CONTRASEÑA)</div>
          <input v-model="nuevoUsuario.contrasena" type="password" placeholder="> *********" />
        </div>

        <button @click="registrarAgente" class="login-btn">
          INITIALIZE AGENT
        </button>

        <div class="link-volver" @click="router.push('/login')">
          >> ABORTAR // VOLVER_AL_LOGIN
        </div>

        <div v-if="mensajeAlerta" :class="['status-msg', colorAlerta]">
          {{ mensajeAlerta }}
        </div>
      </div>

      <div class="card-footer">
        <span>ENCRYPTION: BCRYPT</span>
        <span>STATUS: AWAITING_DATA</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  display: flex; justify-content: center; align-items: center; height: 100vh;
  position: relative; z-index: 2; background: #020808; 
}

.cyber-card {
  width: 420px; background: rgba(5, 10, 15, 0.98); border: 1px solid #00ffcc;
  box-shadow: 0 0 40px rgba(0, 255, 204, 0.15); position: relative; font-family: 'Consolas', monospace;
}

.card-header {
  background: #00ffcc; color: #000; padding: 6px 15px; display: flex;
  align-items: center; gap: 10px; font-size: 0.7rem; font-weight: 800; letter-spacing: 1px;
}

.status-dot { width: 8px; height: 8px; background: #000; border-radius: 50%; animation: pulse 1.5s infinite; }

.card-content { padding: 45px 40px; }

.neon-title {
  font-size: 2.8rem; color: #fff; margin: 0; text-shadow: 0 0 10px #00ffcc; letter-spacing: -2px;
}

.flavor-text {
  font-size: 0.6rem; color: #00ffcc; margin-top: 5px; margin-bottom: 35px;
  letter-spacing: 2px; opacity: 0.8;
}

.input-field { margin-bottom: 25px; }

.label { font-size: 0.75rem; color: #00ffcc; margin-bottom: 8px; font-weight: bold; }

input {
  width: 100%; background: #080f0e; border: 1px solid #1a3333; padding: 12px; color: #fff;
  font-size: 0.9rem; outline: none; transition: all 0.3s; box-sizing: border-box;
}

input:focus { border-color: #00ffcc; box-shadow: 0 0 10px rgba(0, 255, 204, 0.2); }

.login-btn {
  width: 100%; padding: 16px; background: transparent; border: 1px solid #00ffcc;
  color: #00ffcc; font-weight: 900; cursor: pointer; margin-top: 10px;
  transition: 0.4s; letter-spacing: 2px; text-transform: uppercase;
}

.login-btn:hover { background: #00ffcc; color: #000; box-shadow: 0 0 30px #00ffcc; }

.card-footer {
  padding: 10px 20px; font-size: 0.6rem; color: #00ffcc; display: flex;
  justify-content: space-between; border-top: 1px solid #1a3333; opacity: 0.5;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.5; }
  100% { transform: scale(1); opacity: 1; }
}

/* ESTILOS AÑADIDOS PARA REGISTRO */
.link-volver {
  text-align: center; margin-top: 25px; color: #558888; font-size: 0.7rem;
  cursor: pointer; transition: 0.3s; letter-spacing: 1px;
}

.link-volver:hover { color: #00ffcc; text-shadow: 0 0 5px #00ffcc; }

.status-msg { margin-top: 20px; padding: 10px; text-align: center; border: 1px solid; font-size: 0.8rem; }
.success { color: #00ffcc; border-color: #00ffcc; background: rgba(0, 255, 204, 0.1); }
.error { color: #ff0055; border-color: #ff0055; background: rgba(255, 0, 85, 0.1); }
</style>