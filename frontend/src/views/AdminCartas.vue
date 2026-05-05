<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth' 

const router = useRouter()
const auth = useAuthStore()

const cerrarSesion = () => {
  auth.logout()
  router.push('/login')
}
</script>

<template>
    <h1>holi</h1>
</template>

<style scoped>
</style>