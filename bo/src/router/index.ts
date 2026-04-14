import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '경영 대시보드' }
  },
  {
    path: '/sales/offers',
    name: 'Offers',
    component: () => import('../views/OfferManagement.vue'),
    meta: { title: '영업 관리 (Offers)' }
  },
  {
    path: '/wms/inbound',
    name: 'Inbound',
    component: () => import('../views/InboundManagement.vue'),
    meta: { title: '입고 관리 (Inbound)' }
  },
  {
    path: '/items',
    name: 'Items',
    component: () => import('../views/ItemManagement.vue'),
    meta: { title: '상품 관리' }
  },
  {
    path: '/partners',
    name: 'Partners',
    component: () => import('../views/PartnerManagement.vue'),
    meta: { title: '거래처 관리' }
  },
  {
    path: '/stock-status',
    name: 'StockStatus',
    component: () => import('../views/StockStatus.vue'),
    meta: { title: '재고 현황' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
