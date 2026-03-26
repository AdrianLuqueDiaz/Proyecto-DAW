import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // Estado
  const user = ref<string | null>(null)
  
  // Getter (saber si está logueado)
  const isLoggedIn = computed(() => user.value !== null)

  // Acciones
  function login(nombre: string) {
    user.value = nombre
    // Aquí en el futuro conectaremos con el Backend (Java)
  }

  function logout() {
    user.value = null
  }

  return { user, isLoggedIn, login, logout }
})