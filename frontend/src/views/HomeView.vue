<script setup lang="ts">
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const logout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<template>

    
  <div class="hub-container">
    <div class="ambient-glow"></div>
    <div class="scanlines"></div>
    
    <aside class="sidebar">
      <div class="brand-section">
        <h1 class="neon-text">BIT_HUB</h1>
        <div class="status-indicator">
          <span class="dot pulse"></span> ONLINE
        </div>
      </div>
      
      <nav class="nav-menu">
        <div class="nav-item active">>> POPULARES_STACK</div>
      </nav>

    </aside>

    <main class="viewport">
      <header class="top-nav glass-panel">
        <div class="cmd-prompt">
          <span class="prompt-symbol">>_</span>
          <input type="text" placeholder="BUSCAR_MODULO_LOGICO..." class="cmd-input" />
        </div>
        
        <div class="user-stats">
          <div class="bytes-counter neon-border">
            <span class="gold-text">{{ auth.bytes }} BYTES: </span>
          </div>
          <div class="user-info">
            <span class="username">{{ auth.jugador }}</span>
            <button @click="logout" class="btn-quit">QUIT_SESSION</button>
          </div>
        </div>
      </header>

      <section class="hero-banner neon-border">
        <div class="banner-overlay"></div>
        <div class="banner-content">
          <div class="event-tag">EVENTO_ACTIVO</div>
          <h2 class="glitch-title">OPTIMIZACIÓN_O(n)</h2>
          <p>Mejora el rendimiento del nodo central para recolectar créditos extra.</p>
          <button class="btn-action">EJECUTAR_NODO()</button>
        </div>
      </section>

      <section class="game-grid">
        <div class="game-card neon-border">
          <div class="card-header">
            <span class="type">LOGIC_GATE</span>
            <span class="reward">x500_POTENCIAL</span>
          </div>
          <h3>LOGIC_SLOTS</h3>
          <p>Valida expresiones booleanas en tiempo real.</p>
          <div class="card-footer">
            <span class="cost">COST: 10B</span>
            <button @click="router.push('/memory')" class="btn-play">START_NODE</button>
        </div>
        </div>

        <div class="game-card neon-border">
          <div class="card-header">
            <span class="type">ANALYSIS</span>
            <span class="reward">x2.5_POTENCIAL</span>
          </div>
          <h3>CODE-JACK_21</h3>
          <p>Suma 21 puntos sin desbordar el stack de memoria.</p>
          <div class="card-footer">
            <span class="cost">COST: 50B</span>
            <button class="btn-play">START_NODE</button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
/* (Mantenemos todo tu estilo CSS igual que lo tenías) */
.hub-container {
  display: flex;
  height: 100vh;
  background: #020808;
  color: #e0fbfb;
  font-family: 'Consolas', monospace;
  position: relative;
  overflow: hidden;
}

.ambient-glow {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: radial-gradient(circle at 50% -20%, #0e2a2a 0%, transparent 70%);
  pointer-events: none;
}

.sidebar {
  width: 280px;
  background: rgba(0, 0, 0, 0.6);
  border-right: 2px solid #00ffcc;
  padding: 40px 20px;
  backdrop-filter: blur(10px);
  z-index: 5;
}

.neon-text {
  color: #fff;
  font-size: 2rem;
  margin: 0;
  text-shadow: 0 0 10px #00ffcc, 0 0 20px #00ffcc;
}

.status-indicator {
  font-size: 0.7rem;
  color: #00ffcc;
  margin-top: 10px;
  letter-spacing: 2px;
}

.dot {
  display: inline-block;
  width: 8px; height: 8px;
  background: #00ffcc;
  border-radius: 50%;
  margin-right: 5px;
}

.pulse { animation: pulse-animation 2s infinite; }

@keyframes pulse-animation {
  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(0, 255, 204, 0.7); }
  70% { transform: scale(1.1); box-shadow: 0 0 0 10px rgba(0, 255, 204, 0); }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(0, 255, 204, 0); }
}

.nav-menu { margin-top: 60px; }
.nav-item {
  padding: 15px 10px;
  color: #558888;
  cursor: pointer;
  transition: 0.3s;
}
.nav-item:hover { color: #00ffcc; text-shadow: 0 0 5px #00ffcc; }
.nav-item.active {
  color: #00ffcc;
  border-left: 2px solid #00ffcc;
  background: rgba(0, 255, 204, 0.05);
}

.viewport {
  flex: 1;
  padding: 30px 50px;
  overflow-y: auto;
  z-index: 5;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 25px;
  background: rgba(0, 20, 20, 0.4);
  border-radius: 4px;
  margin-bottom: 30px;
}

.cmd-input {
  background: transparent;
  border: none;
  color: #00ffcc;
  outline: none;
  margin-left: 10px;
  width: 250px;
}

.neon-border {
  border: 1px solid #00ffcc;
  box-shadow: 0 0 15px rgba(0, 255, 204, 0.2), inset 0 0 5px rgba(0, 255, 204, 0.1);
}

.bytes-counter {
  padding: 8px 20px;
  background: rgba(0, 255, 204, 0.1);
}

.gold-text { color: #ffd700; font-weight: bold; text-shadow: 0 0 10px rgba(255, 215, 0, 0.5); }

.hero-banner {
  padding: 50px;
  margin-bottom: 40px;
  position: relative;
  background: linear-gradient(90deg, rgba(0, 255, 204, 0.1), transparent);
}

.event-tag {
  display: inline-block;
  background: #ff0055;
  color: white;
  font-size: 0.7rem;
  padding: 3px 10px;
  margin-bottom: 20px;
}

.glitch-title {
  font-size: 2.5rem;
  margin: 0;
  color: #fff;
  text-shadow: 2px 0 #ff0055, -2px 0 #00ffcc;
}

.btn-action {
  background: transparent;
  border: 2px solid #00ffcc;
  color: #00ffcc;
  padding: 12px 30px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 20px;
  transition: 0.3s;
}
.btn-action:hover {
  background: #00ffcc;
  color: #000;
  box-shadow: 0 0 20px #00ffcc;
}

.game-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.game-card {
  padding: 30px;
  background: rgba(0, 10, 10, 0.8);
  transition: 0.4s;
}

.game-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0 30px rgba(0, 255, 204, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.7rem;
  color: #00ffcc;
  margin-bottom: 20px;
}

.btn-play {
  background: transparent;
  border: 1px solid #00ffcc;
  color: #00ffcc;
  padding: 8px 15px;
  cursor: pointer;
}

.btn-play:hover {
  background: rgba(0, 255, 204, 1);
  color: #000;
}

.btn-quit {
  background: transparent;
  border: 1px solid #ff4444;
  color: #ff4444;
  padding: 5px 15px;
  font-size: 0.7rem;
  margin-left: 15px;
  cursor: pointer;
}
.btn-quit:hover { background: #ff4444; color: white; }
</style>