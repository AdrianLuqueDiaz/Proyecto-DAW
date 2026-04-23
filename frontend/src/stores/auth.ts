import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => {
    // 1. Miramos si hay una sesión guardada en el navegador
    const memoriaGuardada = localStorage.getItem('bithub_auth')
    const datosPrevios = memoriaGuardada ? JSON.parse(memoriaGuardada) : null

    // 2. Cargamos los datos guardados o arrancamos en blanco
    return {
      jugador: datosPrevios?.jugador || null as string | null,
      bytes: datosPrevios?.bytes || 0,
      partidasJugadas: datosPrevios?.partidasJugadas || 0, // <-- AÑADIDO
      isAuthenticated: datosPrevios?.isAuthenticated || false,
      rol: datosPrevios?.rol || null as string | null 
    }
  },
  
  actions: {
    setUsuarioLogin(usuario: any) {
      this.jugador = usuario.alias
      this.bytes = usuario.saldoBytes || 0 
      this.partidasJugadas = usuario.partidasJugadas || 0 // <-- AÑADIDO
      this.rol = usuario.rol             
      this.isAuthenticated = true
      
      console.log(`Usuario conectado: ${this.jugador} // Rol: ${this.rol} // Saldo: ${this.bytes} Bytes`)
      
      // Llamamos a la función auxiliar para guardar
      this.guardarEnLocal()
    },

    // 3. Función auxiliar para actualizar el localStorage fácilmente desde otros archivos
    guardarEnLocal() {
      localStorage.setItem('bithub_auth', JSON.stringify({
        jugador: this.jugador,
        bytes: this.bytes,
        partidasJugadas: this.partidasJugadas, // <-- AÑADIDO
        isAuthenticated: this.isAuthenticated,
        rol: this.rol
      }))
    },
    
    logout() {
      this.jugador = null
      this.bytes = 0
      this.partidasJugadas = 0 // <-- AÑADIDO
      this.isAuthenticated = false
      this.rol = null
      
      // 4. Destruimos la sesión del navegador al salir
      localStorage.removeItem('bithub_auth')
    }
  }
})