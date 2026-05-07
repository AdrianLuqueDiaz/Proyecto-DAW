<script setup lang="ts">
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="contenedor-principal">
    
    <header class="cabecera">
      
      <div class="logo">
        <span class="logo-bit">BIT</span>
        <span class="logo-hub">HUB</span>
      </div>

      <nav class="navegacion">
        <span class="enlace activo">INICIO</span>
        <span class="enlace" @click="router.push('/clasificacion')">CLASIFICACIÓN</span>
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
        <h1 class="titulo-principal">Módulos de Juego</h1>
      </section>

      <section class="lista-juegos">
        
        <article class="tarjeta-juego">
          
          

          <div class="columna-info">
            <span class="categoria">LOGIC GATE</span>
            <h2 class="nombre-juego">Logic Slots</h2>
            <p class="descripcion">Valida expresiones booleanas en tiempo real.</p>
          </div>

          <div class="columna-estadisticas">
            <div class="estadistica">
              <span class="coste">COSTE</span>
              <span class="valor">50 B</span>
            </div>
            
          </div>

          <div class="columna-iniciar">
            <button class="boton-jugar" @click="router.push('/memory')">INICIAR</button>
          </div>

        </article>

        <article class="tarjeta-juego">
          
          

          <div class="columna-info">
            <span class="categoria">ANALYSIS</span>
            <h2 class="nombre-juego">Code-Jack 21</h2>
            <p class="descripcion">Suma 21 puntos sin desbordar el stack de memoria.</p>
          </div>

          <div class="columna-estadisticas">
            <div class="estadistica">
              <span class="coste">COSTE</span>
              <span class="valor">50 B</span>
            </div>
            
          </div>

          <div class="columna-iniciar">
            <button class="boton-jugar" @click="router.push('/Codejack')">INICIAR</button>
          </div>

        </article>

        <article class="tarjeta-juego">
          
          

          <div class="columna-info">
            <span class="categoria">THINKING</span>
            <h2 class="nombre-juego">DeciBit</h2>
            <p class="descripcion">Alcanza la cantidad de bits en un tiempo limite.</p>
          </div>

          <div class="columna-estadisticas">
            <div class="estadistica">
              <span class="coste">COSTE</span>
              <span class="valor">10 B</span>
            </div>
            
          </div>

          <div class="columna-iniciar">
            <button class="boton-jugar" @click="router.push('/decibit')">INICIAR</button>
          </div>

        </article>

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
  width: 100%;
  overflow-x: hidden;
}

.cabecera {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  min-height: 80px;
  background-color: #11151D;
  border-bottom: 2px solid #1E2532;
  flex-wrap: wrap;
}

@media (min-width: 768px) {
  .cabecera {
    padding: 0 40px;
    height: 80px;
    flex-wrap: nowrap;
  }
}

.logo {
  font-family: 'Consolas', monospace;
  font-size: 1.3rem;
  font-weight: 900;
  letter-spacing: 2px;
  padding: 15px 0;
}

@media (min-width: 768px) {
  .logo { font-size: 1.5rem; }
}

.logo-bit { color: #E2E8F0; }
.logo-hub { color: #00E5FF; }

.navegacion {
  display: flex;
  order: 3;
  width: 100%;
  justify-content: center;
  border-top: 1px solid #1E2532;
}

@media (min-width: 768px) {
  .navegacion {
    order: 2;
    width: auto;
    height: 100%;
    border-top: none;
  }
}

.enlace {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 1px;
  color: #64748B;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

@media (min-width: 768px) {
  .enlace {
    padding: 0 20px;
    font-size: 0.85rem;
  }
}

.enlace:hover { color: #E2E8F0; }
.enlace.activo {
  color: #00E5FF;
  border-bottom: 2px solid #00E5FF;
}

.info-usuario {
  display: flex;
  align-items: center;
  gap: 15px;
  order: 2;
  padding: 10px 0;
}

@media (min-width: 768px) {
  .info-usuario {
    order: 3;
    gap: 25px;
  }
}

.bloque-bytes {
  display: flex;
  align-items: baseline;
  gap: 5px;
  background: #1E2532;
  padding: 4px 8px;
  border-radius: 4px;
}

.etiqueta-bytes {
  font-size: 0.6rem;
  font-weight: 700;
  color: #64748B;
  letter-spacing: 1px;
}

.valor-bytes {
  font-family: 'Consolas', monospace;
  font-weight: bold;
  color: #FACC15; 
  font-size: 0.9rem;
}

.nombre-jugador {
  display: none;
}

@media (min-width: 480px) {
  .nombre-jugador {
    display: inline;
    font-family: 'Consolas', monospace;
    font-size: 0.8rem;
    color: #E2E8F0;
  }
}

.boton-salir {
  background: transparent;
  border: 1px solid #334155;
  color: #94A3B8;
  padding: 6px 12px;
  font-size: 0.7rem;
  font-weight: bold;
  border-radius: 4px; 
  cursor: pointer;
}

.boton-salir:hover {
  background: #EF4444; 
  border-color: #EF4444;
  color: #fff;
}

.contenido {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
}

@media (min-width: 768px) {
  .contenido { padding: 60px 20px; }
}

.header-contenido {
  margin-bottom: 30px;
  text-align: center;
}

@media (min-width: 768px) {
  .header-contenido { text-align: left; }
}

.subtitulo {
  font-family: 'Consolas', monospace;
  font-size: 0.75rem;
  color: #00E5FF;
  letter-spacing: 2px;
  margin: 0 0 10px 0;
}

.titulo-principal {
  font-size: 2rem;
  font-weight: 800;
  margin: 0;
  color: #F8FAFC;
}

@media (min-width: 768px) {
  .titulo-principal { font-size: 3rem; }
}

.lista-juegos {
  display: flex;
  flex-direction: column;
  gap: 15px; 
}

.tarjeta-juego {
  display: flex;
  flex-direction: column;
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 6px;
  padding: 20px;
  gap: 20px;
}

@media (min-width: 768px) {
  .tarjeta-juego {
    flex-direction: row;
    align-items: center;
    padding: 25px 30px;
  }
}

.columna-info { flex: 1; }

.categoria {
  font-size: 0.65rem;
  font-weight: 800;
  letter-spacing: 2px;
  color: #00E5FF;
  margin-bottom: 5px;
  display: block;
}

.nombre-juego {
  font-size: 1.3rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #F8FAFC;
}

.descripcion {
  font-size: 0.85rem;
  color: #94A3B8;
  margin: 0;
}

.columna-estadisticas {
  display: flex;
  gap: 30px;
  padding: 15px 0;
  border-top: 1px solid #1E2532;
  border-bottom: 1px solid #1E2532;
}

@media (min-width: 768px) {
  .columna-estadisticas {
    border: none;
    padding: 0 40px;
    margin: 0;
    
    
  }
}

.estadistica {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.coste {
  font-size: 0.6rem;
  font-weight: 700;
  color: #64748B;
}

.valor {
  font-family: 'Consolas', monospace;
  font-size: 1rem;
  font-weight: bold;
  color: #E2E8F0;
}

.columna-iniciar {
  width: 100%;
}

@media (min-width: 768px) {
  .columna-iniciar {
    width: 120px;
    text-align: right;
  }
}

.boton-jugar {
  width: 100%;
  background: #E2E8F0;
  color: #0B0E14;
  border: none;
  border-radius: 4px;
  padding: 12px;
  font-weight: 800;
  font-size: 0.85rem;
  cursor: pointer;
}

@media (min-width: 768px) {
  .boton-jugar {
    width: auto;
    padding: 12px 24px;
  }
}

.boton-jugar:active { transform: scale(0.98); }
@media (hover: hover) {
  .boton-jugar:hover {
    background: #00E5FF;
    transform: translateY(-2px);
  }
}
</style>