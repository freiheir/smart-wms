<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface StockStatus {
  itemId: number
  partNumber: string
  itemName: string
  physicalStock: number
  availableStock: number
  safetyStock: number
}

const stocks = ref<StockStatus[]>([])
const valuation = ref(0)

const fetchData = async () => {
  try {
    const [statusRes, valuationRes] = await Promise.all([
      axios.get('/api/stocks/status'),
      axios.get('/api/stocks/valuation')
    ])
    stocks.value = statusRes.data
    valuation.value = valuationRes.data
  } catch (error) {
    console.error('재고 데이터 조회 실패:', error)
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="stock-status">
    <div class="summary-cards">
      <div class="card">
        <span class="label">총 재고 자산 가액</span>
        <span class="value">${{ valuation.toLocaleString(undefined, { minimumFractionDigits: 2 }) }}</span>
      </div>
      <div class="card warning">
        <span class="label">재고 부족 품목</span>
        <span class="value">{{ stocks.filter(s => s.availableStock <= s.safetyStock).length }}건</span>
      </div>
    </div>

    <div class="table-container">
      <table class="stock-table">
        <thead>
          <tr>
            <th>품번</th>
            <th>품명</th>
            <th>물리적 실재고</th>
            <th>가용 재고 (판매가능)</th>
            <th>안전 재고</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="stock in stocks" :key="stock.itemId">
            <td><code>{{ stock.partNumber }}</code></td>
            <td class="text-left">{{ stock.itemName }}</td>
            <td class="text-right">{{ stock.physicalStock.toLocaleString() }}</td>
            <td class="text-right bold">{{ stock.availableStock.toLocaleString() }}</td>
            <td class="text-right">{{ stock.safetyStock.toLocaleString() }}</td>
            <td>
              <span v-if="stock.availableStock <= 0" class="badge bg-danger">품절</span>
              <span v-else-if="stock.availableStock <= stock.safetyStock" class="badge bg-warning">보충필요</span>
              <span v-else class="badge bg-success">정상</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.summary-cards { display: flex; gap: 20px; margin-bottom: 25px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); flex: 1; display: flex; flex-direction: column; }
.card.warning { border-left: 5px solid #f1c40f; }
.card .label { font-size: 0.9rem; color: #7f8c8d; margin-bottom: 10px; }
.card .value { font-size: 1.8rem; font-weight: bold; color: #2c3e50; }

.table-container { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }
.stock-table { width: 100%; border-collapse: collapse; }
.stock-table th, .stock-table td { padding: 15px; border-bottom: 1px solid #eee; text-align: center; }
.stock-table th { background: #f8f9fa; color: #7f8c8d; font-weight: bold; }
.text-left { text-align: left; }
.text-right { text-align: right; }
.bold { font-weight: bold; color: #2980b9; }
code { background: #f4f4f4; padding: 2px 4px; border-radius: 4px; color: #e74c3c; }

.badge { padding: 4px 10px; border-radius: 20px; color: white; font-size: 0.75rem; font-weight: bold; }
.bg-success { background-color: #2ecc71; }
.bg-warning { background-color: #f1c40f; }
.bg-danger { background-color: #e74c3c; }
</style>
