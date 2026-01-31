import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/dashboard',
      component: () => import('../views/Layout.vue'),
      children: [
        {
          path: '',
          component: () => import('../views/hotel/List.vue')
        },
        {
          path: 'hotel-add',
          component: () => import('../views/hotel/Detail.vue')
        },
        {
          path: 'hotel-edit/:id',
          component: () => import('../views/hotel/Detail.vue')
        }
      ]
    }
  ]
})

export default router
