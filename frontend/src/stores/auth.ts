import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  // 1. EL ESTADO (Las variables que guardamos)
  state: () => ({
    jugador: null as string | null, // Cambiado de 'user' a 'jugador'
    bytes: 0,                       // Añadimos el saldo de bytes
    isAuthenticated: false,
  }),
  
  // 2. LAS ACCIONES (Lo que podemos hacer con esas variables)
  actions: {
    login(alias: string, contrasena: string) {
      // De momento simulamos el login exitoso
      this.jugador = alias
      this.bytes = 1000 // Le damos 1000 bytes de saldo inicial al entrar
      this.isAuthenticated = true
      console.log(`Agente conectado: ${this.jugador} // Saldo: ${this.bytes} Bytes`)
    },
    
    logout() {
      // Al salir, borramos todo
      this.jugador = null
      this.bytes = 0
      this.isAuthenticated = false
    }
  }
})