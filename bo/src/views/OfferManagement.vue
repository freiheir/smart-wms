<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Offer {
  id: number
  inquiryNo: string
  partner: { partnerName: string }
  status: string // INQUIRY, OFFER, PI_CONVERTED, SHIPPED, CANCELLED
  totalAmount: number
  currency: string
  createdAt: string
}

const offers = ref<Offer[]>([])

const fetchOffers = async () => {
  try {
    const response = await axios.get('/api/sales/offers')
    offers.value = response.data
  } catch (error) {
    console.error('오퍼 조회 실패:', error)
  }
}

// P/I 확정
const convertToPI = async (id: number) => {
  if (!confirm('해당 오퍼를 P/I로 확정하시겠습니까?')) return
  try {
    await axios.post(`/api/sales/offers/${id}/convert-to-pi`)
    fetchOffers()
  } catch (error) {
    alert('확정에 실패했습니다.')
  }
}

// P/O 생성
const generatePO = async (id: number) => {
  if (!confirm('P/I를 기반으로 매입처별 발주서(P/O)를 생성하고 WMS에 입고 예정을 등록하시겠습니까?')) return
  try {
    await axios.post(`/api/sales/offers/${id}/generate-po`)
    alert('P/O 생성 및 WMS 연동이 완료되었습니다.')
    fetchOffers()
  } catch (error) {
    alert('발주 생성에 실패했습니다.')
  }
}

// 취소
const cancelOffer = async (id: number) => {
  if (!confirm('해당 오퍼를 취소하시겠습니까? (할당된 재고가 해제됩니다)')) return
  try {
    await axios.post(`/api/sales/offers/${id}/cancel`)
    fetchOffers()
  } catch (error) {
    alert('취소에 실패했습니다.')
  }
}

const getStatusBadge = (status: string) => {
  switch (status) {
    case 'INQUIRY': return 'bg-secondary'
    case 'PI_CONVERTED': return 'bg-success'
    case 'SHIPPED': return 'bg-primary'
    case 'CANCELLED': return 'bg-danger'
    default: return 'bg-info'
  }
}

onMounted(fetchOffers)
</script>

<template>
  <div class="offer-management">
    <div class="header-actions">
      <h2>💼 영업 관리 (Offers & PI)</h2>
    </div>

    <div class="table-container">
      <table class="offer-table">
        <thead>
          <tr>
            <th>No</th>
            <th>Inquiry No</th>
            <th>거래처</th>
            <th>총액</th>
            <th>상태</th>
            <th>등록일</th>
            <th>관리 액션</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="offer in offers" :key="offer.id">
            <td>{{ offer.id }}</td>
            <td><code>{{ offer.inquiryNo || '-' }}</code></td>
            <td class="text-left">{{ offer.partner?.partnerName }}</td>
            <td class="text-right bold">{{ offer.currency }} {{ offer.totalAmount.toLocaleString() }}</td>
            <td>
              <span :class="['badge', getStatusBadge(offer.status)]">{{ offer.status }}</span>
            </td>
            <td>{{ offer.createdAt.substring(0, 10) }}</td>
            <td>
              <div class="actions">
                <button v-if="offer.status === 'INQUIRY'" @click="convertToPI(offer.id)" class="btn-sm btn-success">PI 확정</button>
                <button v-if="offer.status === 'PI_CONVERTED'" @click="generatePO(offer.id)" class="btn-sm btn-warning">PO 생성</button>
                <button v-if="offer.status !== 'SHIPPED' && offer.status !== 'CANCELLED'" @click="cancelOffer(offer.id)" class="btn-sm btn-danger">취소</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.header-actions { margin-bottom: 20px; }
.table-container { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.offer-table { width: 100%; border-collapse: collapse; }
.offer-table th, .offer-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }
.bold { font-weight: bold; }
code { background: #f4f4f4; padding: 2px 4px; border-radius: 4px; }

.badge { padding: 4px 8px; border-radius: 4px; color: white; font-size: 0.75rem; }
.bg-secondary { background: #95a5a6; }
.bg-success { background: #2ecc71; }
.bg-primary { background: #3498db; }
.bg-danger { background: #e74c3c; }
.bg-warning { background: #f39c12; }

.actions { display: flex; gap: 5px; justify-content: center; }
.btn-sm { padding: 4px 8px; border: none; border-radius: 4px; color: white; cursor: pointer; font-size: 0.75rem; }
.btn-success { background: #27ae60; }
.btn-warning { background: #f39c12; }
.btn-danger { background: #c0392b; }
</style>
