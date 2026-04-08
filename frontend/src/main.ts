import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router' // Asegúrate de que esta ruta es correcta

const app = createApp(App)

// Aquí es donde "enchufamos" las tecnologías antes de arrancar
app.use(createPinia())
app.use(router)

// Finalmente, arrancamos la aplicación
app.mount('#app')