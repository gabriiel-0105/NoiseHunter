import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/pages/index.vue'
import Usuarios from '@/pages/usuarios.vue'
import Dispositivos from '@/pages/dispositivos.vue'
import Eventos from '@/pages/eventos.vue'
import Leituras from '@/pages/leituras.vue'
const router = createRouter({
 history: createWebHistory(import.meta.env.BASE_URL),
 routes: [
  { path: '/', component: Dashboard },
  { path: '/usuarios', component: Usuarios },
  { path: '/dispositivos', component: Dispositivos },
  { path: '/eventos', component: Eventos },
  { path: '/leituras', component: Leituras },
  { path: '/:pathMatch(.*)*', redirect: '/' },
 ],
})
export default router
