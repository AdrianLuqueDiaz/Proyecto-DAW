import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  // 1. EL ESTADO (Las variables que guardamos)
  state: () => ({
    jugador: null as string | null,
    bytes: 0,
    isAuthenticated: false,
    rol: null as string | null 
  }),
  
  // 2. LAS ACCIONES (Lo que podemos hacer con esas variables)
  actions: {
    login(alias: string, contrasena: string) {
      // De momento simulamos el login exitoso
      this.jugador = alias
      this.bytes = 1000
      this.isAuthenticated = true
      
      // <--- 2. LA TRAMPA DE LA SIMULACIÓN
      // Si el que entra eres tú (ProGamer), te damos poderes de ADMIN. 
      // Si entra cualquier otro nombre, le damos poderes de USUARIO normal.
      if (alias === 'ProGamer') {
        this.rol = 'ADMIN'
      } else {
        this.rol = 'USUARIO'
      }
      
      console.log(`Agente conectado: ${this.jugador} // Rol: ${this.rol} // Saldo: ${this.bytes} Bytes`)
    },
    
    logout() {
      // Al salir, borramos todo
      this.jugador = null
      this.bytes = 0
      this.isAuthenticated = false
      this.rol = null // <--- 3. Borramos el rol por seguridad
    }
  }
})