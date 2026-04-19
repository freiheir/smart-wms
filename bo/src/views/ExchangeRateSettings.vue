<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-bold">환율 관리</h2>
      <button @click="addNewRow" class="bg-blue-500 text-white px-4 py-2 rounded">+ 추가</button>
    </div>

    <!-- 목록 -->
    <div class="bg-white shadow rounded overflow-hidden">
      <table class="w-full">
        <thead class="bg-gray-50 border-b">
          <tr>
            <th class="p-3 text-left">통화코드</th>
            <th class="p-3 text-left">환율 (KRW)</th>
            <th class="p-3 text-left">수정일</th>
            <th class="p-3 text-left">작업</th>
          </tr>
        </thead>
        <tbody v-if="rates.length > 0">
          <tr v-for="(r, index) in rates" :key="index" class="border-b">
            <td class="p-3">
              <input v-model="r.currencyCode" :disabled="!!r.id" class="border p-1 rounded w-full" :class="{'bg-gray-100': r.id}" placeholder="예: USD" />
            </td>
            <td class="p-3">
              <input v-model.number="r.rateToKrw" type="number" step="0.01" class="border p-1 rounded w-32" />
            </td>
            <td class="p-3 text-gray-500 text-sm">
              {{ r.updatedAt ? new Date(r.updatedAt).toLocaleString() : '-' }}
            </td>
            <td class="p-3">
              <button @click="saveRate(r)" class="text-blue-600 font-semibold hover:underline">저장</button>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr>
            <td colspan="4" class="p-10 text-center text-gray-500">등록된 환율 정보가 없습니다. (기본 1400원 적용됨)</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const rates = ref<any[]>([])

const fetchRates = async () => {
  const res = await axios.get('/api/settings/exchange-rates')
  rates.value = res.data
}

const addNewRow = () => {
  rates.value.unshift({ currencyCode: '', rateToKrw: 1400.0 })
}

const saveRate = async (rate: any) => {
  if (!rate.currencyCode) return alert('통화코드를 입력해주세요.')
  await axios.post('/api/settings/exchange-rates', rate)
  await fetchRates()
}

onMounted(fetchRates)
</script>
