import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import Login from '../views/Login.vue'
import Home from '../views/HomeView.vue'
import Memory from '../games/cartas-memory.vue'
import Preguntas from '../views/Preguntas.vue'
import Registro from '../views/Registro.vue'
import Codejack from '../games/CodeJack.vue'

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
      path: '/registro',
      name: 'registro',
      component: Registro
    },
    {
      path: '/home',
      name: 'home',
      component: Home,
      meta: { requiereAutenticacion: true } // <-- Candado para usuarios logueados
    },
    {
      path: '/memory',
      name: 'memory',
      component: Memory,
      meta: { requiereAutenticacion: true } // <-- Candado
    },
    {
      path: '/Codejack',
      name: 'Codejack',
      component: Codejack,
      meta: { requiereAutenticacion: true } // <-- Candado
    },
    {
      path: '/preguntas',
      name: 'preguntas',
      component: Preguntas,
      meta: { requiereAutenticacion: true, requiereAdmin: true } // <-- Candado doble (Solo ADMIN)
    }
  ]
})


router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  
  if (to.meta.requiereAdmin && auth.rol !== 'ADMIN') {
    return next('/home') 
  }

  else if (to.meta.requiereAutenticacion && !auth.rol) {
    return next('/login') 
  }

 


  next()
})

export default router