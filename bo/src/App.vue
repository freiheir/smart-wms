<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="layout">
    <!-- 사이드바 메뉴 -->
    <aside class="sidebar">
      <div class="logo">
        <h2>Smart ERP BO</h2>
      </div>
      <nav class="menu">
        <router-link to="/dashboard" class="menu-item" active-class="active">
          <span class="icon">📊</span> 대시보드
        </router-link>
        <router-link to="/sales/offers" class="menu-item" active-class="active">
          <span class="icon">💼</span> 영업 관리
        </router-link>
        <router-link to="/wms/inbound" class="menu-item" active-class="active">
          <span class="icon">📥</span> 입고 관리
        </router-link>
        <router-link to="/items" class="menu-item" active-class="active">
          <span class="icon">📦</span> 상품 관리
        </router-link>
        <router-link to="/partners" class="menu-item" active-class="active">
          <span class="icon">🤝</span> 거래처 관리
        </router-link>
        <router-link to="/stock-status" class="menu-item" active-class="active">
          <span class="icon">🚚</span> 재고 현황
        </router-link>
      </nav>
    </aside>

    <!-- 메인 콘텐츠 영역 -->
    <main class="content">
      <header class="header">
        <h1>{{ route.meta.title || '환영합니다' }}</h1>
      </header>
      <section class="main-view">
        <router-view></router-view>
      </section>
    </main>
  </div>
</template>

<style>
/* 전역 초기화 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  background-color: #f5f7fa;
  color: #2c3e50;
}

.layout {
  display: flex;
  height: 100vh;
  width: 100vw;
}

/* 사이드바 스타일 */
.sidebar {
  width: 260px;
  background-color: #2c3e50;
  color: #ecf0f1;
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 30px 20px;
  border-bottom: 1px solid #34495e;
  text-align: center;
}

.menu {
  padding: 20px 0;
  flex: 1;
}

.menu-item {
  display: block;
  padding: 15px 25px;
  color: #bdc3c7;
  text-decoration: none;
  transition: all 0.3s;
  font-size: 1.1rem;
}

.menu-item:hover {
  background-color: #34495e;
  color: #ffffff;
}

.menu-item.active {
  background-color: #3498db;
  color: #ffffff;
  border-left: 5px solid #ffffff;
}

.icon {
  margin-right: 10px;
}

/* 메인 콘텐츠 스타일 */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.header {
  background-color: #ffffff;
  padding: 20px 40px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.main-view {
  padding: 40px;
  flex: 1;
}
</style>
