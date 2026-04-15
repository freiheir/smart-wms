<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Item {
  id?: number
  partNumber?: string
  rawPartNumber: string
  itemName: string
  wholesalePrice: number
  multiplier: number
  weightKg: number
  cbm: number
  currency: string
  useYn: string
  safetyStock: number
  partnerName?: string
  createdAt?: string
}

const items = ref<Item[]>([])
const showModal = ref(false)
const isEdit = ref(false)
const currentItem = ref<Item>({
  rawPartNumber: '',
  itemName: '',
  wholesalePrice: 0,
  multiplier: 1.0,
  weightKg: 0,
  cbm: 0,
  currency: 'USD',
  useYn: 'Y',
  safetyStock: 0
})

const API_URL = '/api/items'

// 목록 조회
const fetchItems = async () => {
  try {
    const response = await axios.get(API_URL)
    items.value = response.data
  } catch (error: any) {
    console.error('목록 조회 실패:', error)
    alert('데이터를 불러오는데 실패했습니다.')
  }
}

// 등록/수정 모달 열기
const openModal = (item?: Item) => {
  if (item) {
    isEdit.value = true
    currentItem.value = { ...item }
  } else {
    isEdit.value = false
    currentItem.value = {
      rawPartNumber: '',
      itemName: '',
      wholesalePrice: 0,
      multiplier: 1.0,
      weightKg: 0,
      cbm: 0,
      currency: 'USD',
      useYn: 'Y',
      safetyStock: 0
    }
  }
  showModal.value = true
}

// 저장
const saveItem = async () => {
  try {
    if (isEdit.value && currentItem.value.id) {
      await axios.put(`${API_URL}/${currentItem.value.id}`, currentItem.value)
    } else {
      await axios.post(API_URL, currentItem.value)
    }
    showModal.value = false
    fetchItems()
  } catch (error) {
    console.error('저장 실패:', error)
    alert('저장에 실패했습니다. 품번 중복 여부를 확인하세요.')
  }
}

onMounted(fetchItems)
</script>

<template>
  <div class="item-management">
    <div class="header-actions">
      <h2>📦 품목 마스터 관리 (ERP)</h2>
      <button @click="openModal()" class="btn-primary">신규 품목 등록</button>
    </div>

    <div class="table-container">
      <table class="item-table">
        <thead>
          <tr>
            <th>정제 품번</th>
            <th>원시 품번</th>
            <th>품명</th>
            <th>통화</th>
            <th>도매가</th>
            <th>배수</th>
            <th>안전재고</th>
            <th>사용</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td><code>{{ item.partNumber }}</code></td>
            <td>{{ item.rawPartNumber }}</td>
            <td class="text-left">{{ item.itemName }}</td>
            <td>{{ item.currency }}</td>
            <td class="text-right">{{ item.wholesalePrice.toLocaleString() }}</td>
            <td>{{ item.multiplier }}</td>
            <td>{{ item.safetyStock }}</td>
            <td>
              <span :class="['badge', item.useYn === 'Y' ? 'bg-success' : 'bg-danger']">
                {{ item.useYn === 'Y' ? 'Y' : 'N' }}
              </span>
            </td>
            <td>
              <button @click="openModal(item)" class="btn-sm">수정</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 등록/수정 모달 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h3>{{ isEdit ? '품목 수정' : '신규 품목 등록' }}</h3>
        <div class="form-grid">
          <div class="form-group">
            <label>원시 품번 (Raw Part No)</label>
            <input v-model="currentItem.rawPartNumber" placeholder="예: SKU-001-E">
          </div>
          <div class="form-group">
            <label>품명 (Item Name)</label>
            <input v-model="currentItem.itemName">
          </div>
          <div class="form-group">
            <label>통화 (Currency)</label>
            <select v-model="currentItem.currency">
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
              <option value="KRW">KRW</option>
            </select>
          </div>
          <div class="form-group">
            <label>도매가 (Wholesale Price)</label>
            <input type="number" v-model="currentItem.wholesalePrice">
          </div>
          <div class="form-group">
            <label>판매가 배수 (Multiplier)</label>
            <input type="number" step="0.1" v-model="currentItem.multiplier">
          </div>
          <div class="form-group">
            <label>안전 재고 (Safety Stock)</label>
            <input type="number" v-model="currentItem.safetyStock">
          </div>
          <div class="form-group">
            <label>중량 (kg)</label>
            <input type="number" step="0.01" v-model="currentItem.weightKg">
          </div>
          <div class="form-group">
            <label>부피 (CBM)</label>
            <input type="number" step="0.0001" v-model="currentItem.cbm">
          </div>
        </div>
        <div class="modal-actions">
          <button @click="showModal = false" class="btn-secondary">취소</button>
          <button @click="saveItem" class="btn-primary">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.btn-primary { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-secondary { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-sm { padding: 4px 8px; font-size: 0.8rem; cursor: pointer; }
.table-container { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
.item-table { width: 100%; border-collapse: collapse; }
.item-table th, .item-table td { padding: 12px; border-bottom: 1px solid #eee; text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }
code { background: #f4f4f4; padding: 2px 4px; border-radius: 4px; color: #e74c3c; }
.badge { padding: 2px 6px; border-radius: 4px; color: white; font-size: 0.8rem; }
.bg-success { background: #2ecc71; }
.bg-danger { background: #e74c3c; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 30px; border-radius: 8px; width: 650px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 20px; }
.form-group { display: flex; flex-direction: column; }
label { font-size: 0.85rem; margin-bottom: 5px; font-weight: bold; }
input, select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
.modal-actions { margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px; }
</style>
