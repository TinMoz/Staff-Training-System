<!--LoginView.vue-->
<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '../stores/userStore'
import router from '../router';
import { Select } from '@element-plus/icons-vue'
import service from '../utils/request';

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
  // 先進行表單驗證
  if (!username.value) {
    errorMessage.value = '請填入你的用戶名';
    return;
  }
  
  if (!password.value) {
    errorMessage.value = '請填入你的密碼';
    return;
  }

  try {
    const response = await service.post('/api/auth/login', {
      username: username.value,
      password: password.value
    });

    const { token, role, username: resUsername } = response.data;
    localStorage.setItem('token', token);
    userStore.token = token;
    userStore.role = role.toUpperCase();
    userStore.username = resUsername;
    userStore.isAuthenticated = true;

    // 強制重新初始化以同步數據
    await userStore.initialize();
    
    errorMessage.value = '';
    loginSuccess.value = true;
    setTimeout(() => {
      router.push('/home');
    }, 1500);

  } catch (error: any) {
    console.error('登錄失敗:', error);
    // 改進錯誤訊息處理
    if (error.response?.data) {
      errorMessage.value = typeof error.response.data === 'string' 
        ? `登錄失敗: ${error.response.data}` 
        : '登錄失敗: 用戶名或密碼錯誤';
    } else {
      errorMessage.value = '登錄失敗: 網絡連接錯誤';
    }
  }
};
</script>

<template>
  <div class="page-container">
    <!--登錄容器-->
    <div class="auth-container login">
      <h1>登錄</h1>
      <!--登錄成功提示-->
      <div v-if="loginSuccess" class="success-box">
        <el-icon class="success-icon"><Select /></el-icon>
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