import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import Login from '../views/Login.vue'
import Home from '../views/HomeView.vue'
import Memory from '../games/cartas-memory.vue'
import Preguntas from '../admin/AdminPreguntas.vue'
import Registro from '../views/Registro.vue'
import Codejack from '../games/CodeJack.vue'
import Clasificacion from '../views/Clasificacion.vue'
import DeciBit from '../games/DeciBit.vue'
import Admin from '../admin/AdminMenu.vue'
import Cartas from '../admin/AdminCartas.vue'
import Estadisticas from '../admin/AdminEstadisticas.vue'
import LogicLink from '../games/LogicLink.vue'




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
      meta: { requiereAutenticacion: true}
    },
    {
      path: '/memory',
      name: 'memory',
      component: Memory,
      meta: { requiereAutenticacion: true}
    },
    {
      path: '/codejack',
      name: 'Codejack',
      component: Codejack,
      meta: { requiereAutenticacion: true }
    },
    {
      path: '/logiclink',
      name: 'LogicLink',
      component: LogicLink,
      meta: { requiereAutenticacion: true }
    },
    {
      path: '/admin',
      name: 'Admin',
      component: Admin,
      meta: { requiereAutenticacion: true, requiereAdmin: true }
    },
    {
      path: '/estadisticas',
      name: 'Estadisticas',
      component: Estadisticas,
      meta: { requiereAutenticacion: true, requiereAdmin: true }

    },
    {
      path: '/cartas',
      name: 'Cartas',
      component: Cartas,
      meta: { requiereAutenticacion: true, requiereAdmin: true }

    },
    {
      path: '/decibit',
      name: 'DeciBit',
      component: DeciBit,
      meta: { requiereAutenticacion: true }
    },

    {
      path: '/clasificacion',
      name: 'clasificacion',
      component: Clasificacion,
      meta: { requiereAutenticacion: true}
    },
    
    {
      path: '/preguntas',
      name: 'preguntas',
      component: Preguntas,
      meta: { requiereAutenticacion: true, requiereAdmin: true }
    }
  ]
})


router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  
  if (to.meta.requiereAutenticacion && !auth.rol) {
    return next('/login') 

  }
  if (to.meta.requiereAdmin && auth.rol !== 'ADMIN') {
    return next('/home') 
  }

  

 
 

  next()
})

export default router