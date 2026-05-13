<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth' 

const API_URL = import.meta.env.VITE_API_BASE_URL

const router = useRouter()
const auth = useAuthStore()

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
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
      
      <section class="contenido-header texto-centrado">
        <p class="subtitulo">HERRAMIENTAS DE ADMINISTRADOR</p>
        <h1 class="titulo-principal">Selecciona un Módulo</h1>
      </section>

      <div class="cuadricula-modulos">
        <div class="tarjeta-modulo" @click="router.push('/preguntas')">
          <h3 class="titulo-modulo">Logic Slots</h3>
          <p class="desc-modulo">Añade y elimina las preguntas que aparecen al emparejar cartas en el juego de memoria.</p>
          <span class="boton-modulo">GESTIONAR PREGUNTAS &rarr;</span>
        </div>

        <div class="tarjeta-modulo" @click="router.push('/cartas')">
          <h3 class="titulo-modulo">Code-Jack 21</h3>
          <p class="desc-modulo">Gestiona la baraja de datos. Añade nuevas cartas con valores en Gigabytes o Megabytes.</p>
          <span class="boton-modulo">GESTIONAR CARTAS &rarr;</span>
        </div>
      </div>

      <div class="contenedor-inferior">
        <div class="tarjeta-modulo tarjeta-ancha" @click="router.push('/estadisticas')">
          <h3 class="titulo-modulo">Estadísticas</h3>
          <p class="desc-modulo">Visualiza las estadísticas de cada juego.</p>
          <span class="boton-modulo">MOSTRAR ESTADÍSTICAS &rarr;</span>
        </div>
      </div>

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

.texto-centrado {
  text-align: center;
  margin-bottom: 50px;
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

.cuadricula-modulos {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.tarjeta-modulo {
  background-color: #11151D;
  border: 1px solid #1E2532;
  border-radius: 8px;
  padding: 40px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.tarjeta-modulo:hover {
  border-color: #00E5FF;
  transform: translateY(-5px);
  box-shadow: 0 10px 30px -10px rgba(0, 229, 255, 0.1);
}

.titulo-modulo {
  font-size: 1.5rem;
  color: #F8FAFC;
  margin: 0 0 15px 0;
}

.desc-modulo {
  color: #94A3B8;
  font-size: 0.95rem;
  line-height: 1.6;
  margin: 0 0 30px 0;
  flex-grow: 1;
}

.boton-modulo {
  color: #00E5FF;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 1px;
}

.contenedor-inferior {
  margin-top: 30px;
  width: 100%;
}

.tarjeta-ancha {
  width: 100%;
  box-sizing: border-box;
}

@media (max-width: 900px) {
  .titulo-principal {
    font-size: 2.5rem;
  }
  .tarjeta-modulo {
    padding: 30px;
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
  .cuadricula-modulos {
    grid-template-columns: 1fr;
  }
  .tarjeta-modulo {
    padding: 25px;
  }
  .contenido {
    padding: 40px 20px;
  }
}
</style>