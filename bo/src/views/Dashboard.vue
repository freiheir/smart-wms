<template>
  <div class="dashboard-container">
    <h1>경영 대시보드</h1>
    
    <div class="metrics-grid">
      <div class="metric-card">
        <h3>오퍼 성공률</h3>
        <div class="value">{{ (report.offerSuccessRate || 0).toFixed(1) }}%</div>
      </div>
      
      <div class="metric-card alert" v-if="report.stockAlerts && report.stockAlerts.length > 0">
        <h3>부족 재고 알림</h3>
        <div class="value">{{ report.stockAlerts.length }}건</div>
      </div>
    </div>

    <div class="report-section" v-if="report.financialStats">
      <h2>기간별 재무 통계</h2>
      <div class="stats-table">
        <div class="stat-item">
          <span>총 매출:</span>
          <strong>${{ formatNumber(report.financialStats.totalSales) }}</strong>
        </div>
        <div class="stat-item">
          <span>총 매입:</span>
          <strong>${{ formatNumber(report.financialStats.totalPurchase) }}</strong>
        </div>
        <div class="stat-item">
          <span>예상 마진:</span>
          <strong :class="{ positive: report.financialStats.expectedMargin > 0 }">
            ${{ formatNumber(report.financialStats.expectedMargin) }} ({{ report.financialStats.marginRate }}%)
          </strong>
        </div>
      </div>
    </div>

    <div class="list-section">
      <h2>미출고 현황 (P/I 확정분)</h2>
      <table>
        <thead>
          <tr>
            <th>Inquiry No</th>
            <th>거래처</th>
            <th>총액</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="offer in report.unshippedOffers" :key="offer.offerId">
            <td>{{ offer.inquiryNo }}</td>
            <td>{{ offer.partnerName }}</td>
            <td>${{ formatNumber(offer.totalAmount) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

const report = ref<any>({});

const fetchReport = async () => {
  try {
    const today = new Date().toISOString().split('T')[0];
    const startDate = '2026-01-01'; // 기본 조회 기간
    const response = await axios.get(`/api/reports/dashboard?startDate=${startDate}&endDate=${today}`);
    report.value = response.data;
  } catch (error) {
    console.error('Failed to fetch dashboard report:', error);
  }
};

const formatNumber = (val: number) => {
  if (!val) return '0.00';
  return val.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

onMounted(() => {
  fetchReport();
});
</script>

<style scoped>
.dashboard-container { padding: 20px; }
.metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }
.metric-card { background: #f8f9fa; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; }
.metric-card.alert { background: #fff3f3; border: 1px solid #ffc1c1; }
.metric-card .value { font-size: 2rem; font-weight: bold; color: #2c3e50; }
.report-section { margin-bottom: 30px; background: #fff; padding: 20px; border-radius: 8px; }
.stats-table { display: flex; gap: 40px; }
.stat-item { display: flex; flex-direction: column; }
.positive { color: #27ae60; }
table { width: 100%; border-collapse: collapse; margin-top: 10px; }
th, td { border-bottom: 1px solid #eee; padding: 12px; text-align: left; }
th { background: #f4f7f6; }
</style>
