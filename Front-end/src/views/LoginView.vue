<!--LoginView.vue-->
<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '../stores/userStore'
import axios from '../utils/request';
import router from '../router';
import { Select } from '@element-plus/icons-vue'

// 使用用戶Store
const userStore = useUserStore()
// 表單相關狀態
const username = ref('')
const password = ref('')
const loginSuccess = ref(false)
const errorMessage = ref('')

// 計算用戶角色顯示標籤
const userRoleLabel = computed(() => {
  return userStore.role === 'ADMIN' ? '管理員 👑' : '普通用戶 💼';
});

// 處理登錄請求
const handleLogin = async () => {
  try {
    const response = await axios.post('/api/auth/login', { // 透過axios發送帶著用戶名和密碼的登錄請求的api
      username: username.value,
      password: password.value
    });

    const { token, role, username: resUsername } = response.data; // 獲取響應數據中的token、角色和用戶名
    localStorage.setItem('token', token);
    userStore.token = token;
    userStore.role = role.toUpperCase(); // 確保轉換為大寫
    userStore.username = resUsername;
    userStore.isAuthenticated = true;

    // 強制重新初始化以同步數據
    await userStore.initialize();

    loginSuccess.value = true;
    setTimeout(() => {
      router.push('/home');
    }, 2000);

  } catch (error: any) {
    console.error('登錄失敗:', error);
    errorMessage.value = '登錄失敗: ' + (error.response?.data || '用戶名或密碼錯誤');
  }
};
</script>

<template>
  <div class="page-container">
    <!--登錄容器-->
    <div class="auth-container login">
      <h1>登錄</h1>
      <!--登錄成功提示-->
      <div v-if="loginSuccess" class="success-message">
        <p>歡迎 {{ userStore.username }}！</p>
        <p>您的權限級別：{{ userRoleLabel }}</p>
        <p>🎉 登錄成功！</p>
      </div>
      <!--登錄表單-->
      <form v-else @submit.prevent="handleLogin">
        <!--用戶名輸入框-->
        <el-input
          v-model="username"
          placeholder="用戶名"
          class="input-item"
        />
        <!--密碼輸入框-->
        <el-input
          v-model="password"
          type="password"
          placeholder="密碼"
          class="input-item"
        />
        <!--登錄成功動效框-->
        <div v-if="loginSuccess" class="success-box">
          <div class="success-content">
            <el-icon class="success-icon"><Select /></el-icon>
            <h3>🎉 歡迎 {{ userStore.username }}！</h3>
            <p>您的權限等級：<span class="role-tag">{{ userRoleLabel }}</span></p>
            <p>即將跳轉到首頁...</p>
          </div>
        </div>
        <!--登錄按鈕-->
        <el-button type="primary" native-type="submit" class="auth-button">登錄</el-button>
        <!--註冊跳轉區域-->
        <div class="auth-link">
          沒有賬號？<router-link to="/register">立即註冊</router-link>
        </div>
      </form>
      <!--錯誤信息提示-->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<style src="../styles/auth_style.css">
</style>