<!--LoginView.vue-->
<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '../stores/userStore'
import axios from '../utils/request';
import router from '../router';
import { Select } from '@element-plus/icons-vue'

const userStore = useUserStore()
const username = ref('')
const password = ref('')
const loginSuccess = ref(false)
const errorMessage = ref('')

const userRoleLabel = computed(() => {
  return userStore.role === 'ADMIN' ? '管理員 👑' : '普通用戶 💼';
});

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    });

    const { token, role, username: resUsername } = response.data;
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
    <div class="auth-container login">
      <h1>登錄</h1>
      <div v-if="loginSuccess" class="success-message">
        <p>歡迎 {{ userStore.username }}！</p>
        <p>您的權限級別：{{ userRoleLabel }}</p>
        <p>🎉 登錄成功！</p>
      </div>
      <form v-else @submit.prevent="handleLogin">
        <el-input
          v-model="username"
          placeholder="用戶名"
          class="input-item"
        />
        <el-input
          v-model="password"
          type="password"
          placeholder="密碼"
          class="input-item"
        />
        <div v-if="loginSuccess" class="success-box">
        <div class="success-content">
          <el-icon class="success-icon"><Select /></el-icon>
          <h3>🎉 歡迎 {{ userStore.username }}！</h3>
          <p>您的權限等級：<span class="role-tag">{{ userRoleLabel }}</span></p>
          <p>即將跳轉到首頁...</p>
        </div>
      </div>
        <el-button type="primary" native-type="submit" class="auth-button">登錄</el-button>
        <div class="auth-link">
        沒有賬號？<router-link to="/register">立即註冊</router-link>
        </div>
      </form>
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<style src="../styles/auth_style.css">
</style>