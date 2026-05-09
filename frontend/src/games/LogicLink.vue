<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { registrarPartida } from '@/services/statsService'

const router = useRouter()
const auth = useAuthStore()
const API_URL = import.meta.env.VITE_API_BASE_URL

const retos = ref<any[]>([])
const retoActualId = ref(0)
const opciones = ref<string[]>([])
const estado = ref('jugando') // jugando, correcto, error, terminado
const piezaSoltada = ref<string | null>(null)

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}

const cargarRetos = async () => {
  try {
    const res = await fetch(`${API_URL}/logiclink`)
    if (res.ok) {
      let datos = await res.json()

      retos.value = datos.sort(() => Math.random() - 0.5)
      prepararReto()
    }
  } catch (error) {
    console.error("Error cargando los enlaces lógicos:", error)
  }
}

const prepararReto = () => {
  if (retos.value.length === 0) return
  
  estado.value = 'jugando'
  piezaSoltada.value = null
  
  const retoActual = retos.value[retoActualId.value]
  const arrayOpciones = [
    retoActual.respuestaCorrecta, 
    retoActual.opcionFalsa1, 
    retoActual.opcionFalsa2
  ]
  
  opciones.value = arrayOpciones.sort(() => Math.random() - 0.5)
}

const actualizarBytes = async (cantidad: number) => {
  if (!auth.jugador) return;
  try {
    const res = await fetch(`${API_URL}/usuarios/${auth.jugador}/bytes?cantidad=${cantidad}`, {
      method: 'PUT'
    });
    if (res.ok) {
      const usuario = await res.json();
      auth.bytes = usuario.saldoBytes;
      auth.guardarEnLocal();
    }
  } catch (error) {
    console.error("Error al actualizar bytes:", error);
  }
}



</script>

<template>
    <h1>holi</h1>
</template>

<style scoped>

</style>