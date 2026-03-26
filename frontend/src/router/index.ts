import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      // Si entran a la raíz, los mandamos al login
      path: '/',
      redirect: '/login'
    },
    {
      // Esta es la única ruta real que existe ahora mismo
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/home',
      name: 'home',
      component: Home
    },
    {
      // Ruta comodín: Si escriben cualquier cosa rara en la URL, van al login
      path: '/:pathMatch(.*)*',
      redirect: '/login'
    }
  ]
})

export default router