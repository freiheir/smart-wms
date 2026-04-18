<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const offerId = route.params.id

const offer = ref<any>(null)
const loading = ref(true)

const fetchOfferDetail = async () => {
  try {
    const response = await axios.get(`/api/sales/offers/${offerId}`)
    offer.value = response.data
  } catch (error) {
    console.error('상세 정보 조회 실패:', error)
    alert('데이터를 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const downloadExcel = async () => {
  try {
    const response = await axios.get(`/api/sales/offers/${offerId}/export-upload-data`, {
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `upload_data_${offerId}.xlsx`)
    document.body.appendChild(link)
    link.click()
  } catch (error) {
    alert('엑셀 다운로드 실패')
  }
}

const convertToPI = async () => {
  if (!confirm('P/I로 확정하시겠습니까?')) return
  try {
    await axios.post(`/api/sales/offers/${offerId}/convert-to-pi`)
    fetchOfferDetail()
    alert('P/I로 확정되었습니다.')
  } catch (error) {
    alert('확정 실패')
  }
}

const deleteOffer = async () => {
  if (!confirm('⚠️ 정말로 이 인콰이어리 내역을 삭제하시겠습니까?')) return
  try {
    await axios.delete(`/api/sales/offers/${offerId}`)
    alert('삭제되었습니다.')
    router.push('/sales/offers')
  } catch (error) {
    alert('삭제 실패')
  }
}

onMounted(fetchOfferDetail)

const summary = computed(() => {
  if (!offer.value || !offer.value.items) return { qty: 0, amount: 0, purchase: 0, margin: 0, marginRate: 0 }
  const items = offer.value.items
  const qty = items.reduce((sum: number, item: any) => sum + (item.quantity || 0), 0)
  const amount = items.reduce((sum: number, item: any) => sum + (item.amount || 0), 0)
  const purchase = items.reduce((sum: number, item: any) => sum + (item.purchaseAmount || 0), 0)
  const margin = amount - purchase
  const marginRate = amount > 0 ? (margin / amount) * 100 : 0
  return { qty, amount, purchase, margin, marginRate }
})

const formatNum = (val: any) => {
  if (val === null || val === undefined) return '0'
  return Number(val).toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 2 })
}
</script>

<template>
  <div class="offer-detail compact-mode">
    <div v-if="loading" class="loading">데이터를 불러오는 중... ⏳</div>
    
    <div v-else-if="offer" class="detail-container">
      <div class="header-section">
        <div class="title-area">
          <button @click="router.push('/sales/offers')" class="btn-back">⬅️ 목록으로</button>
          <h2>🔍 오퍼 상세 내역: {{ offer.inquiryNo || 'Draft' }}</h2>
          <span :class="['badge', offer.status]">{{ offer.status }}</span>
        </div>
        <div class="btn-group">
          <button @click="downloadExcel" class="btn-info">📥 가공 엑셀 다운로드</button>
          <button v-if="offer.status === 'INQUIRY'" @click="convertToPI" class="btn-success">✅ P/I 확정</button>
          <button @click="deleteOffer" class="btn-danger">🗑️ 삭제</button>
        </div>
      </div>

      <div class="summary-board">
        <div class="summary-item">
          <label>총 수량</label>
          <span class="value">{{ summary.qty.toLocaleString() }}</span>
        </div>
        <div class="summary-item">
          <label>총 매입액 (구매총액)</label>
          <span class="value">{{ offer.currency }} {{ formatNum(summary.purchase) }}</span>
        </div>
        <div class="summary-item">
          <label>총 매출액 (판매총액)</label>
          <span class="value">{{ offer.currency }} {{ formatNum(summary.amount) }}</span>
        </div>
        <div class="summary-item highlight">
          <label>예상 마진</label>
          <span class="value">{{ offer.currency }} {{ formatNum(summary.margin) }} ({{ summary.marginRate.toFixed(2) }}%)</span>
        </div>
      </div>

      <div class="table-wrapper">
        <table class="excel-style-table">
          <thead>
            <tr>
              <th>No.</th>
              <th>Buyer Part No</th>
              <th>CODE</th>
              <th>G</th>
              <th>H Class</th>
              <th>K Class</th>
              <th>Company</th>
              <th>Car Code</th>
              <th>Car Name</th>
              <th>Ordered Part No</th>
              <th>Supply No.</th>
              <th>CLASS</th>
              <th>Part Name Eng</th>
              <th>QTY REQ</th>
              <th class="bg-purchase">도매가</th>
              <th class="bg-purchase">구매가</th>
              <th class="bg-purchase">구매금액</th>
              <th class="bg-sales">판매가</th>
              <th class="bg-sales">판매금액</th>
              <th class="bg-margin">마진</th>
              <th class="bg-margin">마진율</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in offer.items" :key="item.id">
              <td class="text-center">{{ idx + 1 }}</td>
              <td class="text-left"><code>{{ item.buyerPartNo }}</code></td>
              <td>{{ item.itemCode || '-' }}</td>
              <td>{{ item.gClass || '-' }}</td>
              <td>{{ item.hClass || '-' }}</td>
              <td>{{ item.kClass || '-' }}</td>
              <td class="text-left">{{ item.company || '-' }}</td>
              <td>{{ item.carCode || '-' }}</td>
              <td class="text-left">{{ item.carName || '-' }}</td>
              <td class="text-left"><code>{{ item.orderedPartNo }}</code></td>
              <td>{{ item.supplyNo || '-' }}</td>
              <td>{{ item.itemClass || '-' }}</td>
              <td class="text-left">{{ item.partNameEng || '-' }}</td>
              <td class="text-right">{{ item.quantity?.toLocaleString() }}</td>
              <td class="text-right">{{ formatNum(item.originalPurchasePrice) }}</td>
              <td class="text-right">{{ formatNum(item.purchasePrice) }}</td>
              <td class="text-right">{{ formatNum(item.purchaseAmount) }}</td>
              <td class="text-right bold">{{ formatNum(item.unitPrice) }}</td>
              <td class="text-right bold">{{ formatNum(item.amount) }}</td>
              <td class="text-right">{{ formatNum(item.margin) }}</td>
              <td class="text-right" :class="item.marginRate < 0 ? 'text-danger' : 'text-success'">
                {{ ((item.marginRate || 0) * 100).toFixed(2) }}%
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.offer-detail { 
  padding: 10px; 
  background: #f4f7f6; 
  min-height: 100vh;
}
.compact-mode { font-size: 0.75rem; }
.header-section { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 10px; 
  background: #fff; 
  padding: 10px 15px; 
  border-radius: 4px; 
  box-shadow: 0 1px 2px rgba(0,0,0,0.05); 
}
.title-area { display: flex; align-items: center; gap: 12px; }
.title-area h2 { margin: 0; font-size: 1.1rem; color: #2c3e50; }
.btn-back { background: #fff; border: 1px solid #ced4da; padding: 4px 10px; border-radius: 4px; cursor: pointer; font-size: 0.75rem; }
.summary-board { 
  display: flex; 
  gap: 25px; 
  margin-bottom: 10px; 
  background: #34495e; 
  color: white; 
  padding: 12px 20px; 
  border-radius: 4px; 
}
.summary-item { display: flex; flex-direction: column; }
.summary-item label { font-size: 0.65rem; color: #bdc3c7; margin-bottom: 3px; }
.summary-item .value { font-size: 1.05rem; font-weight: bold; }
.summary-item.highlight .value { color: #2ecc71; }

.table-wrapper { 
  background: white; 
  border-radius: 4px; 
  box-shadow: 0 1px 3px rgba(0,0,0,0.1); 
  overflow-x: auto; /* 횡스크롤 핵심! */
  width: 100%;
}
.excel-style-table { 
  min-width: 1600px; /* 강제로 넓이를 확보해서 스크롤 유도 */
  border-collapse: collapse; 
  width: 100%;
}
.excel-style-table th { 
  background: #f8f9fa; 
  border: 1px solid #dee2e6; 
  padding: 8px 10px; 
  font-weight: bold; 
  text-align: center; 
  white-space: nowrap; 
}
.excel-style-table td { 
  border: 1px solid #eee; 
  padding: 5px 10px; 
  white-space: nowrap; 
}


/* 영역별 색상 구분 */
.bg-purchase { background: #eef2ff !important; }
.bg-sales { background: #fff7ed !important; }
.bg-margin { background: #f0fdf4 !important; }

.text-right { text-align: right; }
.bold { font-weight: bold; }
.text-danger { color: #e74c3c; font-weight: bold; }
.text-success { color: #27ae60; font-weight: bold; }
code { font-family: monospace; background: #fff5f8; padding: 1px 4px; border-radius: 3px; color: #d63384; font-size: 0.75rem; }
.badge { padding: 3px 10px; border-radius: 12px; font-size: 0.65rem; font-weight: bold; }
.INQUIRY { background: #6c757d; color: white; }
.PI_CONVERTED { background: #28a745; color: white; }
.btn-group { display: flex; gap: 10px; }
.btn-info { background: #17a2b8; color: white; border: none; padding: 6px 14px; border-radius: 4px; cursor: pointer; }
.btn-success { background: #28a745; color: white; border: none; padding: 6px 14px; border-radius: 4px; cursor: pointer; }
.btn-danger { background: #dc3545; color: white; border: none; padding: 6px 14px; border-radius: 4px; cursor: pointer; }
.loading { padding: 50px; text-align: center; font-size: 1.2rem; color: #666; }
</style>
