<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Item {
  id: number;
  itemCode: string;
  itemName: string;
  description: string;
  price: number;
  stockQuantity: number;
}

const items = ref<Item[]>([])
const loading = ref(true)
const error = ref('')

const fetchItems = async () => {
  try {
    const response = await axios.get('/api/items')
    items.value = response.data
    loading.value = false
  } catch (err) {
    error.value = '데이터를 불러오는 중 오류가 발생했습니다.'
    loading.value = false
    console.error(err)
  }
}

onMounted(() => {
  fetchItems()
})
</script>

<template>
  <div class="bo-container">
    <h1>Smart WMS/ERP Back Office</h1>
    <div v-if="loading">로딩 중...</div>
    <div v-else-if="error">{{ error }}</div>
    <table v-else class="item-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>품목 코드</th>
          <th>품목 이름</th>
          <th>가격</th>
          <th>재고</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.itemCode }}</td>
          <td>{{ item.itemName }}</td>
          <td>{{ item.price.toLocaleString() }}원</td>
          <td>{{ item.stockQuantity }}개</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style>
.bo-container {
  padding: 20px;
  font-family: sans-serif;
}
.item-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.item-table th, .item-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}
.item-table th {
  background-color: #f4f4f4;
}
</style>
