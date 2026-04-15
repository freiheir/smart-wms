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
const partners = ref<any[]>([])
const showUploadModal = ref(false)
const selectedPartnerId = ref<number | null>(null)
const uploadFile = ref<File | null>(null)
const uploadResult = ref<any>(null)

// 거래처 목록 조회 (매출처 위주)
const fetchPartners = async () => {
  try {
    const response = await axios.get('/api/partners')
    partners.value = response.data
    // 거래처가 하나도 없으면 테스트를 위해 경고!
    if (partners.value.length === 0) {
      console.warn('등록된 거래처가 없습니다. 거래처 관리에서 먼저 등록해주세요!')
    }
  } catch (error) {
    console.error('거래처 조회 실패:', error)
  }
}

// 오퍼 목록 조회
const fetchOffers = async () => {
  try {
    const response = await axios.get('/api/sales/offers')
    offers.value = response.data
    console.log('오퍼 목록 로드 완료:', offers.value.length, '건')
  } catch (error) {
    console.error('오퍼 조회 실패:', error)
    alert('목록을 불러오는데 실패했습니다.')
  }
}

// 파일 선택 핸들러
const onFileChange = (e: any) => {
  uploadFile.value = e.target.files[0]
}

// 엑셀 업로드 실행
const handleUpload = async () => {
  if (!selectedPartnerId.value || !uploadFile.value) {
    alert('거래처와 파일을 모두 선택해주세요.')
    return
  }

  const formData = new FormData()
  formData.append('file', uploadFile.value)
  formData.append('partnerId', selectedPartnerId.value.toString())

  try {
    const response = await axios.post('/api/sales/offers/upload-excel', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    uploadResult.value = response.data
    
    // 성공한 건이 하나라도 있으면 목록 새로고침
    if (uploadResult.value.successCount > 0) {
      await fetchOffers()
    }

    if (uploadResult.value.failureCount === 0) {
      alert('업로드 성공!')
      showUploadModal.value = false
      uploadResult.value = null
    }
  } catch (error) {
    console.error('업로드 실패:', error)
    alert('업로드 중 오류가 발생했습니다. 로그를 확인하세요.')
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

onMounted(() => {
  fetchOffers()
  fetchPartners()
})
</script>

<template>
  <div class="offer-management">
    <div class="header-actions">
      <h2>💼 영업 관리 (Offers & PI)</h2>
      <button @click="showUploadModal = true" class="btn-primary">인콰이어리 엑셀 업로드</button>
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
          <tr v-if="offers.length === 0">
            <td colspan="7" class="empty-row">등록된 오퍼가 없습니다. 엑셀을 업로드해주세요! ✨</td>
          </tr>
          <tr v-for="offer in offers" :key="offer.id">
            <td>{{ offer.id }}</td>
            <td><code>{{ offer.inquiryNo || '-' }}</code></td>
            <td class="text-left">{{ offer.partner?.partnerName || 'Unknown' }}</td>
            <td class="text-right bold">{{ offer.currency }} {{ (offer.totalAmount || 0).toLocaleString() }}</td>
            <td>
              <span :class="['badge', getStatusBadge(offer.status)]">{{ offer.status }}</span>
            </td>
            <td>{{ offer.createdAt?.substring(0, 10) || '-' }}</td>
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

    <!-- 엑셀 업로드 모달 -->
    <div v-if="showUploadModal" class="modal-overlay">
      <div class="modal-content">
        <h3>📊 인콰이어리 엑셀 업로드</h3>
        <p class="guide">엑셀 첫 번째 시트의 A열(품번), B열(수량) 데이터를 읽어옵니다.</p>
        
        <div class="form-group">
          <label>거래처(Buyer) 선택</label>
          <select v-model="selectedPartnerId">
            <option :value="null">거래처를 선택하세요</option>
            <option v-for="p in partners" :key="p.id" :value="p.id">{{ p.partnerName }} ({{ p.currency }})</option>
          </select>
        </div>

        <div class="form-group">
          <label>엑셀 파일 선택</label>
          <input type="file" @change="onFileChange" accept=".xlsx, .xls">
        </div>

        <div v-if="uploadResult" class="result-box">
          <p>✅ 성공: {{ uploadResult.successCount }}건</p>
          <p v-if="uploadResult.failureCount > 0" class="error-text">❌ 실패: {{ uploadResult.failureCount }}건</p>
          <ul class="error-list">
            <li v-for="err in uploadResult.errors" :key="err.rowNum">
              [행 {{ err.rowNum }}] {{ err.rawPartNumber }}: {{ err.errorMessage }}
            </li>
          </ul>
        </div>

        <div class="modal-actions">
          <button @click="showUploadModal = false; uploadResult = null" class="btn-secondary">닫기</button>
          <button @click="handleUpload" class="btn-primary" :disabled="!selectedPartnerId || !uploadFile">업로드 시작</button>
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
.offer-table { width: 100%; border-collapse: collapse; }
.offer-table th, .offer-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }
.bold { font-weight: bold; }
code { background: #f4f4f4; padding: 2px 4px; border-radius: 4px; }
.empty-row { padding: 40px !important; color: #95a5a6; font-style: italic; }

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

/* 모달 스타일 */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; padding: 30px; border-radius: 8px; width: 500px; box-shadow: 0 4px 20px rgba(0,0,0,0.2); }
.guide { font-size: 0.85rem; color: #7f8c8d; margin-bottom: 20px; }
.form-group { margin-bottom: 15px; display: flex; flex-direction: column; text-align: left; }
.form-group label { font-size: 0.9rem; font-weight: bold; margin-bottom: 5px; }
.form-group select, .form-group input { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.modal-actions { margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px; }

.result-box { margin-top: 20px; padding: 15px; background: #f9f9f9; border-radius: 4px; border-left: 4px solid #3498db; text-align: left; }
.error-text { color: #e74c3c; font-weight: bold; margin-top: 10px; }
.error-list { font-size: 0.8rem; color: #c0392b; list-style: none; padding: 0; margin-top: 5px; max-height: 150px; overflow-y: auto; }
</style>
