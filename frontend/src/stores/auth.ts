import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    jugador: null as string | null,
    bytes: 0,
    isAuthenticated: false,
    rol: null as string | null 
  }),
  
  actions: {
    setUsuarioLogin(usuario: any) {
      this.jugador = usuario.alias
      this.bytes = usuario.saldoBytes || 0 
      this.rol = usuario.rol             
      this.isAuthenticated = true
      
      console.log(`Agente conectado: ${this.jugador} // Rol: ${this.rol} // Saldo: ${this.bytes} Bytes`)
    },
    
    logout() {
      this.jugador = null
      this.bytes = 0
      this.isAuthenticated = false
      this.rol = null
    }
  }
})