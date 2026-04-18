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
const showUploadModal = ref(false)
const uploadFile = ref<File | null>(null)
const uploadResult = ref<any>(null)
const inquiryNoInput = ref('')

// 오퍼 목록 조회
const fetchOffers = async () => {
  try {
    const response = await axios.get('/api/sales/offers')
    offers.value = response.data
  } catch (error) {
    console.error('오퍼 조회 실패:', error)
  }
}

// 파일 선택 핸들러
const onFileChange = (e: any) => {
  uploadFile.value = e.target.files[0]
}

// export_data 엑셀 업로드 실행
const handleUpload = async () => {
  if (!uploadFile.value) {
    alert('파일을 선택해주세요.')
    return
  }

  const formData = new FormData()
  formData.append('file', uploadFile.value)
  if (inquiryNoInput.value) {
    formData.append('inquiryNo', inquiryNoInput.value)
  }

  try {
    const response = await axios.post('/api/sales/offers/upload-export', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    uploadResult.value = response.data
    
    if (uploadResult.value.successCount > 0) {
      await fetchOffers()
      alert(`업로드 성공! (${uploadResult.value.successCount}건)`)
      showUploadModal.value = false
      uploadResult.value = null
      inquiryNoInput.value = ''
    }
  } catch (error) {
    console.error('업로드 실패:', error)
    alert('업로드 중 오류가 발생했습니다.')
  }
}

// 가공된 upload_data 엑셀 다운로드
const downloadUploadData = async (id: number) => {
  try {
    const response = await axios.get(`/api/sales/offers/${id}/export-upload-data`, {
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `upload_data_${id}.xlsx`)
    document.body.appendChild(link)
    link.click()
  } catch (error) {
    alert('엑셀 다운로드에 실패했습니다.')
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
  if (!confirm('P/O를 생성하시겠습니까?')) return
  try {
    await axios.post(`/api/sales/offers/${id}/generate-po`)
    alert('P/O 생성이 완료되었습니다.')
    fetchOffers()
  } catch (error) {
    alert('발주 생성에 실패했습니다.')
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
})
</script>

<template>
  <div class="offer-management compact-mode">
    <div class="header-actions">
      <h2>💼 영업 관리 (Inquiry & Offer)</h2>
      <button @click="showUploadModal = true" class="btn-primary">발주 사이트 엑셀 업로드</button>
    </div>

    <div class="table-container">
      <table class="offer-table">
        <thead>
          <tr>
            <th width="40">ID</th>
            <th>Inquiry No</th>
            <th>거래처 (자동매핑)</th>
            <th width="120">총액</th>
            <th width="80">상태</th>
            <th width="100">등록일</th>
            <th width="280">관리 액션</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="offers.length === 0">
            <td colspan="7" class="empty-row">데이터가 없습니다. 엑셀을 업로드해주세요! ✨</td>
          </tr>
          <tr v-for="offer in offers" :key="offer.id">
            <td>{{ offer.id }}</td>
            <td>
              <router-link :to="'/sales/offers/' + offer.id" class="inquiry-link">
                <code>{{ offer.inquiryNo || 'REQ-' + offer.id }}</code>
              </router-link>
            </td>
            <td class="text-left">{{ offer.partner?.partnerName || 'Unknown (Pending Mapping)' }}</td>
            <td class="text-right bold">{{ offer.currency }} {{ (offer.totalAmount || 0).toLocaleString() }}</td>
            <td>
              <span :class="['badge', getStatusBadge(offer.status)]">{{ offer.status }}</span>
            </td>
            <td>{{ offer.createdAt?.substring(0, 10) || '-' }}</td>
            <td>
              <div class="actions">
                <router-link :to="'/sales/offers/' + offer.id" class="btn-sm btn-info">상세보기/검토</router-link>
                <button v-if="offer.status === 'PI_CONVERTED'" @click="generatePO(offer.id)" class="btn-sm btn-warning">PO 생성</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 엑셀 업로드 모달 -->
    <div v-if="showUploadModal" class="modal-overlay">
      <div class="modal-content">
        <h3>📊 발주 사이트 원본 업로드</h3>
        <p class="guide">발주 사이트에서 내려받은 export_data.xlsx를 업로드하면 자동으로 가공합니다.</p>
        
        <div class="form-group">
          <label>인콰이어리 번호 (생략 가능)</label>
          <input type="text" v-model="inquiryNoInput" placeholder="예: REQ-2024-001">
        </div>

        <div class="form-group">
          <label>엑셀 파일 선택</label>
          <input type="file" @change="onFileChange" accept=".xlsx, .xls">
        </div>

        <div class="modal-actions">
          <button @click="showUploadModal = false; uploadResult = null" class="btn-secondary">닫기</button>
          <button @click="handleUpload" class="btn-primary" :disabled="!uploadFile">업로드 및 자동가공 시작</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 폰트 크기 축소 및 컴팩트 레이아웃 */
.compact-mode { font-size: 0.8rem; }
.compact-mode h2 { font-size: 1.2rem; margin: 0; }

.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.btn-primary { background: #3498db; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 0.8rem; }
.btn-secondary { background: #95a5a6; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 0.8rem; }
.table-container { background: white; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); overflow-x: auto; }
.offer-table { width: 100%; border-collapse: collapse; }
.offer-table th { background: #f8f9fa; font-weight: bold; border-bottom: 2px solid #dee2e6; }
.offer-table th, .offer-table td { padding: 6px 8px; border-bottom: 1px solid #eee; text-align: center; white-space: nowrap; }
.text-left { text-align: left; }
.text-right { text-align: right; }
.bold { font-weight: bold; }
code { background: #f4f4f4; padding: 1px 3px; border-radius: 3px; font-family: monospace; }

.badge { padding: 2px 6px; border-radius: 3px; color: white; font-size: 0.7rem; font-weight: bold; }
.bg-secondary { background: #6c757d; }
.bg-success { background: #28a745; }
.bg-primary { background: #007bff; }
.bg-danger { background: #dc3545; }
.bg-warning { background: #ffc107; color: #212529 !important; }
.bg-info { background: #17a2b8; }

.actions { display: flex; gap: 4px; justify-content: center; }
.btn-sm { padding: 3px 6px; border: none; border-radius: 3px; color: white; cursor: pointer; font-size: 0.7rem; }
.btn-success { background: #28a745; }
.btn-warning { background: #ffc107; color: #212529; }
.btn-info { background: #17a2b8; }
.btn-danger { background: #dc3545; }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; padding: 20px; border-radius: 6px; width: 400px; box-shadow: 0 4px 15px rgba(0,0,0,0.3); }
.modal-content h3 { margin-top: 0; font-size: 1.1rem; }
.guide { font-size: 0.75rem; color: #6c757d; margin-bottom: 15px; }
.form-group { margin-bottom: 10px; text-align: left; }
.form-group label { display: block; font-size: 0.8rem; font-weight: bold; margin-bottom: 4px; }
.form-group input { width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 3px; font-size: 0.8rem; }
.modal-actions { margin-top: 15px; display: flex; justify-content: flex-end; gap: 8px; }
</style>
