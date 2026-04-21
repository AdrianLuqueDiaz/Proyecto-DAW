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
      alert(">> ERROR: Credenciales incorrectas o usuario no encontrado.")
    }

  } catch (error) {
    console.error(error)
    alert(">> ERROR: Imposible conectar con el servidor central.")
  }
}
</script>

<template>
  <div class="login-wrapper">
    <div class="cyber-card">
      <div class="card-header">
        <span class="status-dot"></span>
        <span class="system-text">NODE_AUTH_PROTOCOL // STACK_ARCADE</span>
      </div>
      
      <div class="card-content">
        <h1 class="neon-title">BIT-ARCADE</h1>
        <p class="flavor-text">SISTEMA DE ENTRENAMIENTO LÓGICO Y ALGORÍTMICO</p>

        <div class="input-field">
          <div class="label">DEVELOPER_ID</div>
          <input v-model="alias" placeholder="> GAMER_TAG" />
        </div>

        <div class="input-field">
          <div class="label">SECURITY_TOKEN</div>
          <input v-model="contrasena" type="password" placeholder="> *********" @keyup.enter="entrar" />
        </div>

        <button @click="entrar" class="login-btn">
          ESTABLISH CONNECTION
        </button>

        <div class="link-registro" @click="router.push('/registro')">
          >> INITIALIZE_NEW_AGENT (REGISTRARSE)
        </div>
      </div>

      <div class="card-footer">
        <span>ENCRYPTION: AES-256</span>
        <span>STATUS: READY TO CODE</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
  z-index: 2;
  background: #020808; 
}

.cyber-card {
  width: 420px;
  background: rgba(5, 10, 15, 0.98);
  border: 1px solid #00ffcc;
  box-shadow: 0 0 40px rgba(0, 255, 204, 0.15);
  position: relative;
  font-family: 'Consolas', monospace;
}

.card-header {
  background: #00ffcc;
  color: #000;
  padding: 6px 15px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.7rem;
  font-weight: 800;
  letter-spacing: 1px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #000;
  border-radius: 50%;
  animation: pulse 1.5s infinite;
}

.card-content {
  padding: 45px 40px;
}

.neon-title {
  font-size: 2.8rem;
  color: #fff;
  margin: 0;
  text-shadow: 0 0 10px #00ffcc;
  letter-spacing: -2px;
}

.flavor-text {
  font-size: 0.6rem;
  color: #00ffcc;
  margin-top: 5px;
  margin-bottom: 35px;
  letter-spacing: 2px;
  opacity: 0.8;
}

.input-field {
  margin-bottom: 25px;
}

.label {
  font-size: 0.75rem;
  color: #00ffcc;
  margin-bottom: 8px;
  font-weight: bold;
}

input {
  width: 100%;
  background: #080f0e;
  border: 1px solid #1a3333;
  padding: 12px;
  color: #fff;
  font-size: 0.9rem;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
}

input:focus {
  border-color: #00ffcc;
  box-shadow: 0 0 10px rgba(0, 255, 204, 0.2);
}

.login-btn {
  width: 100%;
  padding: 16px;
  background: transparent;
  border: 1px solid #00ffcc;
  color: #00ffcc;
  font-weight: 900;
  cursor: pointer;
  margin-top: 10px;
  transition: 0.4s;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.login-btn:hover {
  background: #00ffcc;
  color: #000;
  box-shadow: 0 0 30px #00ffcc;
}

.link-registro {
  text-align: center;
  margin-top: 25px;
  color: #558888;
  font-size: 0.7rem;
  cursor: pointer;
  transition: 0.3s;
  letter-spacing: 1px;
}

.link-registro:hover {
  color: #00ffcc;
  text-shadow: 0 0 5px #00ffcc;
}

.card-footer {
  padding: 10px 20px;
  font-size: 0.6rem;
  color: #00ffcc;
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #1a3333;
  opacity: 0.5;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.5; }
  100% { transform: scale(1); opacity: 1; }
}
</style>