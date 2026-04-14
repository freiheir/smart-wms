<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Partner {
  id?: number
  partnerCode: string
  partnerName: string
  partnerType: string // BUY, SELL, BOTH
  currency: string
  paymentTerms: string
  useYn: string
}

const partners = ref<Partner[]>([])
const showModal = ref(false)
const currentPartner = ref<Partial<Partner>>({
  partnerCode: '',
  partnerName: '',
  partnerType: 'BUY',
  currency: 'USD',
  paymentTerms: '',
  useYn: 'Y'
})

const fetchPartners = async () => {
  try {
    const response = await axios.get('/api/partners')
    partners.value = response.data
  } catch (error) {
    console.error('거래처 조회 실패:', error)
  }
}

const savePartner = async () => {
  try {
    await axios.post('/api/partners', currentPartner.value)
    showModal.value = false
    fetchPartners()
  } catch (error) {
    alert('저장에 실패했습니다.')
  }
}

onMounted(fetchPartners)
</script>

<template>
  <div class="partner-management">
    <div class="header-actions">
      <h2>🤝 거래처 관리</h2>
      <button @click="showModal = true" class="btn-primary">신규 거래처 등록</button>
    </div>

    <div class="table-container">
      <table class="partner-table">
        <thead>
          <tr>
            <th>코드</th>
            <th>거래처명</th>
            <th>구분</th>
            <th>주요 통화</th>
            <th>결제 조건</th>
            <th>사용</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in partners" :key="partnerCode">
            <td><code>{{ p.partnerCode }}</code></td>
            <td class="text-left">{{ p.partnerName }}</td>
            <td>
              <span v-if="p.partnerType === 'BUY'" class="badge bg-info">매입처</span>
              <span v-else-if="p.partnerType === 'SELL'" class="badge bg-warning">매출처</span>
              <span v-else class="badge bg-purple">전체</span>
            </td>
            <td>{{ p.currency }}</td>
            <td>{{ p.paymentTerms }}</td>
            <td>{{ p.useYn }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 등록 모달 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h3>신규 거래처 등록</h3>
        <div class="form-grid">
          <div class="form-group">
            <label>거래처 코드</label>
            <input v-model="currentPartner.partnerCode" placeholder="VND-001">
          </div>
          <div class="form-group">
            <label>거래처명</label>
            <input v-model="currentPartner.partnerName">
          </div>
          <div class="form-group">
            <label>거래처 구분</label>
            <select v-model="currentPartner.partnerType">
              <option value="BUY">매입처 (Vendor)</option>
              <option value="SELL">매출처 (Buyer)</option>
              <option value="BOTH">전체 (Both)</option>
            </select>
          </div>
          <div class="form-group">
            <label>결제 통화</label>
            <select v-model="currentPartner.currency">
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
              <option value="KRW">KRW</option>
            </select>
          </div>
          <div class="form-group full-width">
            <label>결제 조건 (Payment Terms)</label>
            <input v-model="currentPartner.paymentTerms" placeholder="Net 30, T/T 등">
          </div>
        </div>
        <div class="modal-actions">
          <button @click="showModal = false" class="btn-secondary">취소</button>
          <button @click="savePartner" class="btn-primary">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.btn-primary { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-secondary { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.table-container { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.partner-table { width: 100%; border-collapse: collapse; }
.partner-table th, .partner-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: center; }
.text-left { text-align: left; }
.badge { padding: 2px 8px; border-radius: 4px; color: white; font-size: 0.8rem; }
.bg-info { background: #3498db; }
.bg-warning { background: #f1c40f; }
.bg-purple { background: #9b59b6; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 30px; border-radius: 8px; width: 500px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 20px; }
.full-width { grid-column: span 2; }
.form-group { display: flex; flex-direction: column; }
label { font-size: 0.85rem; margin-bottom: 5px; font-weight: bold; }
input, select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.modal-actions { margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px; }
</style>
