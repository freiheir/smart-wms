<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Item {
  id?: number
  itemCode: string
  itemName: string
  description: string
  price: number
  stockQuantity: number
  itemUnit: string
  barcode: string
  useYn: string
  category: string
  createdAt?: string
}

const items = ref<Item[]>([])
const showModal = ref(false)
const isEdit = ref(false)
const currentItem = ref<Item>({
  itemCode: '',
  itemName: '',
  description: '',
  price: 0,
  stockQuantity: 0,
  itemUnit: 'EA',
  barcode: '',
  useYn: 'Y',
  category: ''
})

const API_URL = '/api/items'

// 목록 조회
const fetchItems = async () => {
  try {
    console.log('상품 목록 조회 시도: ', API_URL)
    const response = await axios.get(API_URL)
    console.log('응답 데이터: ', response.data)
    items.value = response.data
  } catch (error: any) {
    console.error('상품 목록 조회 실패 상세:', error)
    if (error.response) {
      console.error('서버 응답 에러:', error.response.status, error.response.data)
    } else if (error.request) {
      console.error('요청 전송됨, 응답 없음 (네트워크 오류 등):', error.request)
    } else {
      console.error('요청 설정 오류:', error.message)
    }
    alert(`데이터를 불러오는데 실패했습니다. (오류: ${error.message})`)
  }
}

// 등록 모달 열기
const openAddModal = () => {
  isEdit.value = false
  currentItem.value = {
    itemCode: '',
    itemName: '',
    description: '',
    price: 0,
    stockQuantity: 0,
    itemUnit: 'EA',
    barcode: '',
    useYn: 'Y',
    category: ''
  }
  showModal.value = true
}

// 저장 (등록/수정)
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
    alert('저장에 실패했습니다. 코드가 중복되는지 확인해 주세요.')
  }
}

onMounted(fetchItems)
</script>

<template>
  <div class="item-management">
    <div class="header-actions">
      <h2>📦 상품 목록 관리</h2>
      <button @click="openAddModal" class="btn-primary">신규 상품 등록</button>
    </div>

    <!-- 상품 테이블 -->
    <div class="table-container">
      <table class="item-table">
        <thead>
          <tr>
            <th>코드</th>
            <th>상품명</th>
            <th>단위</th>
            <th>단가</th>
            <th>재고</th>
            <th>카테고리</th>
            <th>사용여부</th>
            <th>등록일</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.itemCode }}</td>
            <td class="text-left">{{ item.itemName }}</td>
            <td>{{ item.itemUnit }}</td>
            <td class="text-right">{{ item.price.toLocaleString() }}</td>
            <td class="text-right">{{ item.stockQuantity.toLocaleString() }}</td>
            <td>{{ item.category }}</td>
            <td>
              <span :class="['badge', item.useYn === 'Y' ? 'bg-success' : 'bg-danger']">
                {{ item.useYn === 'Y' ? '사용' : '중지' }}
              </span>
            </td>
            <td>{{ item.createdAt?.substring(0, 10) }}</td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="8" class="empty-msg">등록된 상품이 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 등록/수정 모달 (심플 구현) -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal-content">
        <h3>{{ isEdit ? '상품 수정' : '신규 상품 등록' }}</h3>
        <div class="form-grid">
          <div class="form-group">
            <label>상품 코드</label>
            <input v-model="currentItem.itemCode" placeholder="SKU-001" :disabled="isEdit">
          </div>
          <div class="form-group">
            <label>상품명</label>
            <input v-model="currentItem.itemName" placeholder="상품명을 입력하세요">
          </div>
          <div class="form-group">
            <label>단위</label>
            <select v-model="currentItem.itemUnit">
              <option value="EA">EA (개)</option>
              <option value="BOX">BOX (박스)</option>
              <option value="PLT">PLT (파렛트)</option>
            </select>
          </div>
          <div class="form-group">
            <label>단가</label>
            <input type="number" v-model="currentItem.price">
          </div>
          <div class="form-group">
            <label>초기 재고</label>
            <input type="number" v-model="currentItem.stockQuantity">
          </div>
          <div class="form-group">
            <label>카테고리</label>
            <input v-model="currentItem.category" placeholder="식품, 잡화 등">
          </div>
          <div class="form-group full-width">
            <label>설명</label>
            <textarea v-model="currentItem.description"></textarea>
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
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.btn-primary {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

/* 테이블 스타일 */
.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  overflow: hidden;
}

.item-table {
  width: 100%;
  border-collapse: collapse;
}

.item-table th, .item-table td {
  padding: 15px;
  text-align: center;
  border-bottom: 1px solid #ecf0f1;
}

.item-table th {
  background-color: #f8f9fa;
  font-weight: bold;
  color: #7f8c8d;
}

.text-left { text-align: left; }
.text-right { text-align: right; }

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
  color: white;
}
.bg-success { background-color: #2ecc71; }
.bg-danger { background-color: #e74c3c; }

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 600px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.full-width { grid-column: span 2; }

label { font-weight: bold; margin-bottom: 5px; font-size: 0.9rem; }
input, select, textarea {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.modal-actions {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
