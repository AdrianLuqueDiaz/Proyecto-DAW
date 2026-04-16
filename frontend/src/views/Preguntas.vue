<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const preguntaNueva = ref({
  idPareja: 100, 
  enunciado: '',
  respuestaCorrecta: '',
  falsa1: '',
  falsa2: '',
  falsa3: ''
})

const mensajeAlerta = ref('')
const colorAlerta = ref('') 

const guardarPregunta = async () => {
  try {
    const respuesta = await fetch('http://localhost:8080/api/preguntas/add', {
      method: 'POST', 
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(preguntaNueva.value) 
    })

    if (respuesta.ok) {
      mensajeAlerta.value = '>> DATA_INJECTED_SUCCESSFULLY'
      colorAlerta.value = 'success'
      
      preguntaNueva.value.enunciado = ''
      preguntaNueva.value.respuestaCorrecta = ''
      preguntaNueva.value.falsa1 = ''
      preguntaNueva.value.falsa2 = ''
      preguntaNueva.value.falsa3 = ''
    } else {
      mensajeAlerta.value = '>> ERROR: Java no pudo guardar los datos.'
      colorAlerta.value = 'error'
    }

  } catch (error) {
    console.error(error)
    mensajeAlerta.value = '>> ERROR: Imposible conectar con el servidor.'
    colorAlerta.value = 'error'
  }
}
</script>

<template>
  <div class="hub-container">
    <div class="ambient-glow"></div>

    <aside class="sidebar">
      <div class="brand-section">
        <h1 @click="router.push('/hub')" class="neon-text clickable">BIT_HUB</h1>
        <div class="status-indicator">
          <span class="dot pulse"></span> DATABASE_ACTIVE
        </div>
      </div>
      
      <nav class="nav-menu">
        <div class="nav-item" @click="router.push('/home')">>> VOLVER_AL_HUB</div>
        <div class="nav-item" @click="router.push('/preguntas')">>> CREAR_PREGUNTAS(ADMIN)</div>
      </nav>
    </aside>

    <main class="viewport">
      <header class="top-nav glass-panel">
        <div class="cmd-prompt">
          <span class="prompt-symbol">>_</span>
          <span class="cmd-text">SYSTEM_ADMIN:QUESTION_BUILDER</span>
        </div>
      </header>

      <section class="form-grid neon-border">
        <div class="banner-overlay"></div>
        <h2 class="glitch-title">AÑADIR_DATA_ARCADE</h2>
        
        <form @submit.prevent="guardarPregunta" class="arcade-form">
          <div class="form-row">
            <div class="input-group">
              <label>LOGIC_CATEGORY</label>
              <select v-model="preguntaNueva.idPareja" class="cmd-input neon-border">
                <option :value="100">STRINGS (100)</option>
                <option :value="200">BOOLEANS (200)</option>
                <option :value="300">ARRAYS (300)</option>
                <option :value="400">NUMBERS (400)</option>
                <option :value="500">FUNCTIONS (500)</option>
                <option :value="600">OBJECTS (600)</option>
              </select>
            </div>
          </div>

          <div class="input-group">
            <label>ENUNCIADO_DEL_PROBLEMA</label>
            <textarea v-model="preguntaNueva.enunciado" class="cmd-input neon-border" placeholder="Escribir lógica..."></textarea>
          </div>

          <div class="input-group">
            <label>SOLUCIÓN_CORRECTA (TRUE_BIT)</label>
            <input v-model="preguntaNueva.respuestaCorrecta" type="text" class="cmd-input neon-border highlight-green" placeholder="Respuesta correcta" />
          </div>

          <div class="options-grid">
            <div class="input-group">
              <label>FALSA_01</label>
              <input v-model="preguntaNueva.falsa1" type="text" class="cmd-input neon-border highlight-red" placeholder="Error bit 1" />
            </div>
            <div class="input-group">
              <label>FALSA_02</label>
              <input v-model="preguntaNueva.falsa2" type="text" class="cmd-input neon-border highlight-red" placeholder="Error bit 2" />
            </div>
            <div class="input-group">
              <label>FALSA_03</label>
              <input v-model="preguntaNueva.falsa3" type="text" class="cmd-input neon-border highlight-red" placeholder="Error bit 3" />
            </div>
          </div>

          <button type="submit" class="btn-action">EJECUTAR_INSERT_QUERY()</button>
        </form>

        <div v-if="mensajeAlerta" :class="['status-msg', colorAlerta]">
          {{ mensajeAlerta }}
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
/* HEREDADOS DEL HUB */
.hub-container {
  display: flex; height: 100vh; background: #020808; color: #e0fbfb;
  font-family: 'Consolas', monospace; position: relative; overflow: hidden;
}

.ambient-glow {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(circle at 50% -20%, #0e2a2a 0%, transparent 70%);
  pointer-events: none;
}

.sidebar {
  width: 280px; background: rgba(0, 0, 0, 0.6); border-right: 2px solid #00ffcc;
  padding: 40px 20px; backdrop-filter: blur(10px); z-index: 5;
}

.neon-text { color: #fff; text-shadow: 0 0 10px #00ffcc, 0 0 20px #00ffcc; cursor: default; }
.clickable { cursor: pointer; }

.nav-item { padding: 15px 10px; color: #558888; cursor: pointer; transition: 0.3s; }
.nav-item:hover { color: #00ffcc; text-shadow: 0 0 5px #00ffcc; }
.nav-item.active { color: #00ffcc; border-left: 2px solid #00ffcc; background: rgba(0, 255, 204, 0.05); }

.viewport { flex: 1; padding: 30px 50px; overflow-y: auto; z-index: 5; }

.top-nav { padding: 15px 25px; background: rgba(0, 20, 20, 0.4); border-radius: 4px; margin-bottom: 30px; }
.cmd-text { color: #00ffcc; margin-left: 10px; }

/* ESTILO ESPECÍFICO DEL FORMULARIO */
.form-grid {
  padding: 40px; position: relative;
  background: linear-gradient(135deg, rgba(0, 255, 204, 0.05), transparent);
}

.glitch-title {
  font-size: 2rem; margin-bottom: 30px; color: #fff;
  text-shadow: 2px 0 #ff0055, -2px 0 #00ffcc;
}

.input-group { margin-bottom: 20px; display: flex; flex-direction: column; }
label { color: #00ffcc; font-size: 0.8rem; margin-bottom: 8px; letter-spacing: 1px; }

.cmd-input {
  background: rgba(0, 0, 0, 0.5); border: 1px solid #00ffcc !important;
  color: #00ffcc; padding: 12px; font-family: 'Consolas', monospace; outline: none;
}

.neon-border {
  box-shadow: 0 0 10px rgba(0, 255, 204, 0.2);
}

textarea.cmd-input { height: 80px; resize: none; }

.highlight-green { border-color: #00ffcc !important; box-shadow: 0 0 10px rgba(0, 255, 204, 0.4); }
.highlight-red { border-color: #ff0055 !important; }

.options-grid {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px;
}

.btn-action {
  background: transparent; border: 2px solid #00ffcc; color: #00ffcc;
  padding: 15px; font-weight: bold; cursor: pointer; transition: 0.3s;
  width: 100%; font-family: 'Consolas', monospace;
}

.btn-action:hover {
  background: #00ffcc; color: #000; box-shadow: 0 0 20px #00ffcc;
}

.status-msg { margin-top: 20px; padding: 10px; text-align: center; border: 1px solid; }
.success { color: #00ffcc; border-color: #00ffcc; background: rgba(0, 255, 204, 0.1); }
.error { color: #ff0055; border-color: #ff0055; background: rgba(255, 0, 85, 0.1); }

/* Scanlines Effect */
.scanlines {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.25) 50%), 
              linear-gradient(90deg, rgba(255, 0, 0, 0.06), rgba(0, 255, 0, 0.02), rgba(0, 0, 255, 0.06));
  background-size: 100% 4px, 3px 100%; pointer-events: none; z-index: 10;
}
</style>