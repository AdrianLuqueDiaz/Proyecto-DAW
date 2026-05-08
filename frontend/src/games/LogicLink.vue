<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { registrarPartida } from '@/services/statsService';

const API_URL = import.meta.env.VITE_API_BASE_URL

const auth = useAuthStore()
const router = useRouter()


</script>

<template>
    <h1>holi</h1>
</template>

<style scoped>

</style>