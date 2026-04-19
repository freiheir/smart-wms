<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-bold">가중치 관리</h2>
      <button @click="addNewRow" class="bg-blue-500 text-white px-4 py-2 rounded">+ 추가</button>
    </div>

    <!-- 목록 -->
    <div class="bg-white shadow rounded overflow-hidden">
      <table class="w-full">
        <thead class="bg-gray-50 border-b">
          <tr>
            <th class="p-3 text-left">품목코드</th>
            <th class="p-3 text-left">가중치</th>
            <th class="p-3 text-left">작업</th>
          </tr>
        </thead>
        <tbody v-if="weights.length > 0">
          <tr v-for="(w, index) in weights" :key="index" class="border-b">
            <td class="p-3">
              <input v-model="w.itemCode" :disabled="!!w.id" class="border p-1 rounded w-full" :class="{'bg-gray-100': w.id}" />
            </td>
            <td class="p-3">
              <input v-model.number="w.multiplier" type="number" step="0.01" class="border p-1 rounded w-24" />
            </td>
            <td class="p-3">
              <button @click="saveWeight(w)" class="text-blue-600 font-semibold hover:underline">저장</button>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr>
            <td colspan="3" class="p-10 text-center text-gray-500">등록된 가중치 정보가 없습니다.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const weights = ref<any[]>([])

const fetchWeights = async () => {
  const res = await axios.get('/api/settings/weights')
  weights.value = res.data
}

const addNewRow = () => {
  weights.value.unshift({ itemCode: '', multiplier: 1.0 })
}

const saveWeight = async (weight: any) => {
  if (!weight.itemCode) return alert('품목코드를 입력해주세요.')
  await axios.post('/api/settings/weights', weight)
  await fetchWeights()
}

onMounted(fetchWeights)
</script>
