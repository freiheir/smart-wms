<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface InboundExpected {
  id: number
  purchaseOrder: { poNo: string }
  item: { partNumber: string, itemName: string }
  expectedQuantity: number
  receivedQuantity: number
  status: string // WAITING, PARTIAL, RECEIVED
  expectedDate: string
}

const inbounds = ref<InboundExpected[]>([])
const showModal = ref(false)
const selectedInbound = ref<InboundExpected | null>(null)
const receivedQtyInput = ref(0)

const fetchInbounds = async () => {
  try {
    const response = await axios.get('/api/wms/inbound/expected')
    inbounds.value = response.data
  } catch (error) {
    console.error('입고 예정 목록 조회 실패:', error)
  }
}

const openConfirmModal = (inbound: InboundExpected) => {
  selectedInbound.value = inbound
  receivedQtyInput.value = inbound.expectedQuantity - inbound.receivedQuantity
  showModal.value = true
}

const confirmInbound = async () => {
  if (!selectedInbound.value) return
  try {
    await axios.post(`/api/wms/inbound/confirm?expectedId=${selectedInbound.value.id}&receivedQty=${receivedQtyInput.value}`)
    alert('입고 처리가 완료되었습니다.')
    showModal.value = false
    fetchInbounds()
  } catch (error) {
    alert('입고 처리에 실패했습니다.')
  }
}

onMounted(fetchInbounds)
</script>

<template>
  <div class="inbound-management">
    <div class="header-actions">
      <h2>🚛 입고 관리 (Inbound)</h2>
    </div>

    <div class="table-container">
      <table class="inbound-table">
        <thead>
          <tr>
            <th>PO 번호</th>
            <th>품번</th>
            <th>품명</th>
            <th>예정 수량</th>
            <th>입고 완료</th>
            <th>상태</th>
            <th>예정일</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="inbound in inbounds" :key="inbound.id">
            <td><code>{{ inbound.purchaseOrder?.poNo }}</code></td>
            <td><code>{{ inbound.item?.partNumber }}</code></td>
            <td class="text-left">{{ inbound.item?.itemName }}</td>
            <td class="text-right">{{ inbound.expectedQuantity.toLocaleString() }}</td>
            <td class="text-right bold">{{ inbound.receivedQuantity.toLocaleString() }}</td>
            <td>
              <span :class="['badge', inbound.status === 'RECEIVED' ? 'bg-success' : 'bg-warning']">
                {{ inbound.status }}
              </span>
            </td>
            <td>{{ inbound.expectedDate?.substring(0, 10) }}</td>
            <td>
              <button v-if="inbound.status !== 'RECEIVED'" @click="openConfirmModal(inbound)" class="btn-sm btn-primary">입고 처리</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 입고 확정 모달 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h3>입고 수량 입력</h3>
        <div class="info-box" v-if="selectedInbound">
          <p><strong>PO:</strong> {{ selectedInbound.purchaseOrder.poNo }}</p>
          <p><strong>품목:</strong> {{ selectedInbound.item.itemName }} ({{ selectedInbound.item.partNumber }})</p>
          <p><strong>미입고 잔량:</strong> {{ selectedInbound.expectedQuantity - selectedInbound.receivedQuantity }}개</p>
        </div>
        <div class="form-group">
          <label>실제 입고 수량</label>
          <input type="number" v-model="receivedQtyInput">
        </div>
        <div class="modal-actions">
          <button @click="showModal = false" class="btn-secondary">취소</button>
          <button @click="confirmInbound" class="btn-primary">입고 확정</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.header-actions { margin-bottom: 20px; }
.table-container { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.inbound-table { width: 100%; border-collapse: collapse; }
.inbound-table th, .inbound-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }
.bold { font-weight: bold; }
code { background: #f4f4f4; padding: 2px 4px; border-radius: 4px; }

.badge { padding: 4px 8px; border-radius: 4px; color: white; font-size: 0.75rem; }
.bg-success { background: #2ecc71; }
.bg-warning { background: #f1c40f; }

.btn-sm { padding: 4px 8px; border: none; border-radius: 4px; color: white; cursor: pointer; font-size: 0.75rem; }
.btn-primary { background: #3498db; }
.btn-secondary { background: #95a5a6; }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 30px; border-radius: 8px; width: 400px; }
.info-box { background: #f8f9fa; padding: 15px; border-radius: 4px; margin-bottom: 20px; font-size: 0.9rem; }
.form-group { display: flex; flex-direction: column; margin-bottom: 20px; }
label { font-weight: bold; margin-bottom: 5px; }
input { padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 1.1rem; }
.modal-actions { display: flex; justify-content: flex-end; gap: 10px; }
</style>
