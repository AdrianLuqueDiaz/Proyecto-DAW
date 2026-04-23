import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const memoriaGuardada = localStorage.getItem('bithub_auth')
    const datosPrevios = memoriaGuardada ? JSON.parse(memoriaGuardada) : null

    return {
      jugador: datosPrevios?.jugador || null as string | null,
      bytes: datosPrevios?.bytes || 0,
      partidasJugadas: datosPrevios?.partidasJugadas || 0, // Aquí creamos la variable
      isAuthenticated: datosPrevios?.isAuthenticated || false,
      rol: datosPrevios?.rol || null as string | null 
    }
  },
  
  actions: {
    setUsuarioLogin(usuario: any) {
      this.jugador = usuario.alias
      this.bytes = usuario.saldoBytes || 0 
      this.partidasJugadas = usuario.partidasJugadas || 0 // La sincronizamos al hacer login
      this.rol = usuario.rol             
      this.isAuthenticated = true
      
      this.guardarEnLocal()
    },

    guardarEnLocal() {
      localStorage.setItem('bithub_auth', JSON.stringify({
        jugador: this.jugador,
        bytes: this.bytes,
        partidasJugadas: this.partidasJugadas,
        isAuthenticated: this.isAuthenticated,
        rol: this.rol
      }))
    },
    
    logout() {
      this.jugador = null
      this.bytes = 0
      this.partidasJugadas = 0
      this.isAuthenticated = false
      this.rol = null
      
      localStorage.removeItem('bithub_auth')
    }
  }
})