import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/items'
  },
  {
    path: '/items',
    name: 'Items',
    component: () => import('../views/ItemManagement.vue'),
    meta: { title: '상품 관리' }
  },
  {
    path: '/stock-status',
    name: 'StockStatus',
    component: () => import('../views/StockStatus.vue'),
    meta: { title: '입출고 현황' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
