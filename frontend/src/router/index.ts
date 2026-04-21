import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/HomeView.vue'
import Memory from '../games/cartas-memory.vue'
import Preguntas from '../views/Preguntas.vue'
import Registro from '../views/Registro.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
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
      path: '/registro',
      name: 'registro',
      component: Registro
    },
   
    {
      path: '/memory',
      name: 'memory',
      component: Memory
    },
    {
      path: '/preguntas',
      name: 'preguntas',
      component: Preguntas
    }
  ]
})

export default router