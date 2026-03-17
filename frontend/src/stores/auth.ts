import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const jugador = ref<string | null>(null)
  const bytes = ref(0)
  const estaLogueado = ref(false)

  function login(alias: string, saldo: number) {
    jugador.value = alias
    bytes.value = saldo
    estaLogueado.value = true
  }

  function logout() {
    jugador.value = null
    bytes.value = 0
    estaLogueado.value = false
  }

  return { jugador, bytes, estaLogueado, login, logout }
})