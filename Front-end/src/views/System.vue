<script setup lang="ts">
import { ref, onMounted } from 'vue';
import service from '../utils/request';

const databaseInfo = ref({
  databaseName: '載入中...',
  databaseUrl: '載入中...',
  databaseUsername: '載入中...'
});

const loading = ref(true);
const error = ref('');

const fetchDatabaseInfo = async () => {
  try {
    loading.value = true;
    const response = await service.get('/api/system/database-info');
    databaseInfo.value = response.data;
  } catch (err) {
    console.error('獲取數據庫信息失敗:', err);
    error.value = '無法載入數據庫信息';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchDatabaseInfo();
});
</script>

<template>
  <div class="system-info-card">
    <h3>系統信息</h3>
    <div v-if="loading">載入中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="info-content">
      <div class="info-item">
        <span class="info-label">數據庫名稱:</span>
        <span class="info-value">{{ databaseInfo.databaseName }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">用戶名:</span>
        <span class="info-value">{{ databaseInfo.databaseUsername }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">連接URL:</span>
        <span class="info-value url">{{ databaseInfo.databaseUrl }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.system-info-card {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin: 15px 0;
}

h3 {
  margin-top: 0;
  color: #333;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: flex-start;
}

.info-label {
  font-weight: bold;
  width: 120px;
}

.info-value {
  flex-grow: 1;
}

.info-value.url {
  word-break: break-all;
  font-family: monospace;
  font-size: 0.9em;
}

.error {
  color: #dc3545;
}
</style>