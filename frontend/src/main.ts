import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router' // <-- Esto enchufa tus rutas

const app = createApp(App)

app.use(createPinia()) // <-- Esto enchufa tu auth.ts
app.use(router)

app.mount('#app')