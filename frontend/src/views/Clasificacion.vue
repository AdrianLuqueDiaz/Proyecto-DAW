<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const API_URL = import.meta.env.VITE_API_BASE_URL


const auth = useAuthStore()
const router = useRouter()
const usuarios = ref<any[]>([])

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarClasificacion = async () => {
  try {
    const response = await fetch(`${API_URL}/usuarios/clasificacion`)
    if (response.ok) {
      usuarios.value = await response.json()
    }
  } catch (error) {
    console.error("Error al conectar con el sistema:", error)
  }
}

onMounted(cargarClasificacion)
</script>

<template>
  <div class="contenedor-principal">
    
    <header class="cabecera">
      <div class="logo">
        <span class="logo-bit">BIT</span>
        <span class="logo-hub">HUB</span>
      </div>

      <nav class="navegacion">
        <span class="enlace" @click="router.push('/home')">INICIO</span>
        <span class="enlace activo">CLASIFICACIÓN</span>
        <span 
          v-if="auth.rol === 'ADMIN'" 
          class="enlace" 
          @click="router.push('/admin')"
        >
          ADMINISTRACIÓN
        </span>
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

    <main class="contenido">
      
      <section class="header-contenido">
        <p class="subtitulo">SISTEMA ONLINE</p>
        <h1 class="titulo-principal">Ranking Global</h1>
      </section>

      <section class="lista-ranking">
        
        <div class="tabla-header">
          <span class="col-pos">POS</span>
          <span class="col-info">JUGADOR</span>
          <span class="col-partidas">PARTIDAS JUGADAS</span>
          <span class="col-puntos text-right">BYTES ACUMULADOS</span>
        </div>

        <article v-for="(u, index) in usuarios" :key="u.id" class="tarjeta-ranking">
          
          <div class="col-pos">
            <span :class="['num-pos', { 'top-3': index < 3 }]">
              {{ (index + 1).toString().padStart(2, '0') }}
            </span>
          </div>

          <div class="col-info">
            <span class="categoria" v-if="index === 0">TOP PLAYER</span>
            <h2 class="nombre-jugador-ranking">{{ u.alias }}</h2>
          </div>

          <div class="col-partidas">
            <span class="valor-partidas">{{ u.partidasJugadas || 0 }}</span>
          </div>

          <div class="col-puntos">
            <div class="estadistica">
              <span class="valor-ranking">{{ (u.saldoBytes || 0).toLocaleString() }} B</span>
            </div>
          </div>

        </article>

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
  color: #64748B;
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

.header-contenido {
  margin-bottom: 40px;
}

.subtitulo {
  font-family: 'Consolas', monospace;
  font-size: 0.8rem;
  color: #00E5FF;
  letter-spacing: 2px;
  margin-bottom: 10px;
}

.titulo-principal {
  font-size: 3rem;
  font-weight: 800;
  color: #F8FAFC;
  letter-spacing: -1px;
}

.lista-ranking {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tabla-header {
  display: none;
}

@media (min-width: 768px) {
  .tabla-header {
    display: flex;
    align-items: center;
    padding: 0 30px;
    margin-bottom: 10px;
    font-family: 'Consolas', monospace;
    font-size: 0.75rem;
    color: #bac8db;
    letter-spacing: 1px;
  }
}

.tarjeta-ranking {
  display: flex;
  align-items: center;
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 20px 30px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.tarjeta-ranking:hover {
  border-color: #334155;
}

.col-pos {
  width: 60px;
  flex-shrink: 0;
}

.col-info {
  flex: 1;
}

.col-partidas {
  width: 150px;
  text-align: center;
  flex-shrink: 0;
}

.col-puntos {
  width: 150px;
  text-align: right;
  flex-shrink: 0;
}

.num-pos {
  font-family: 'Consolas', monospace;
  font-weight: bold;
  color: #64748B;
}

.num-pos.top-3 {
  color: #00E5FF;
}

.categoria {
  font-size: 0.65rem;
  font-weight: 800;
  color: #FACC15;
  letter-spacing: 1px;
  display: block;
}

.nombre-jugador-ranking {
  font-size: 1.2rem;
  font-weight: 700;
  color: #F8FAFC;
  margin: 0;
}

.valor-partidas {
  font-family: 'Consolas', monospace;
  color: #94A3B8;
  font-weight: bold;
}

.valor-ranking {
  font-family: 'Consolas', monospace;
  font-size: 1.2rem;
  font-weight: bold;
  color: #FACC15;
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
    font-size: 2.2rem;
    text-align: center;
  }
  .header-contenido {
    text-align: center;
  }
  .tarjeta-ranking {
    padding: 20px;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .col-pos {
    width: auto;
    margin-bottom: 5px;
  }
  .col-partidas,
  .col-puntos {
    width: 100%;
    text-align: left;
    display: flex; 
    align-items: center;
    gap: 8px; 
    font-family: 'Consolas', monospace;
    font-size: 0.85rem;
  }
  .col-partidas::before {
    content: "PARTIDAS JUGADAS:";
    color: #64748B;
    font-weight: bold;
  }
  .col-puntos::before {
    content: "BYTES TOTALES:";
    color: #64748B;
    font-weight: bold;
  }
  .valor-ranking {
    font-size: 0.85rem;
  }
}
</style>