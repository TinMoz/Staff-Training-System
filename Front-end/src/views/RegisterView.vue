<!--RegisterView.vue-->
<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import axios from '../utils/request';
import { useRouter } from 'vue-router';
import { CircleCheck } from '@element-plus/icons-vue';
import { debounce } from 'lodash'; // 需要安裝: npm install lodash

const router = useRouter();
const form = ref({
  username: '',
  password: '',
  email: '',
  role: 'USER' // 預設選普通用戶
});
const errorMessage = ref('');
const registerSuccess = ref(false);
const checkingUsername = ref(false); // 新增：標記是否正在檢查用戶名

// 添加字段級錯誤提示
const usernameError = ref('');
const passwordError = ref('');
const emailError = ref('');

const roleLabel = computed(() => {
  return form.value.role === 'ADMIN' ? '管理員 👑' : '普通用戶 💼';
});

// 檢查用戶名是否已存在（防抖處理，減少API請求）
const checkUsernameExists = debounce(async () => {
  // 如果用戶名不符合基本要求，則不發送請求
  if (!form.value.username || form.value.username.includes(' ') || form.value.username.length < 3) {
    return;
  }
  try {
    checkingUsername.value = true;
    const response = await axios.get(`/api/auth/check-username/${form.value.username}`); // 透過axios獲取用戶名是否存在的api
    if (response.data.exists) {
      usernameError.value = '用戶名已被註冊';
    }
  } catch (error) {
    console.error('檢查用戶名出錯:', error);
  } finally {
    checkingUsername.value = false;
  }
}, 500);

// 實時檢查用戶名
const checkUsername = () => {
  if (!form.value.username) {
    usernameError.value = '請輸入用戶名';
  } else if (form.value.username.includes(' ')) {
    usernameError.value = '用戶名不能包含空格';
  } else if (form.value.username.length < 3) {
    usernameError.value = '用戶名至少需要3個字符';
  } else {
    usernameError.value = '';
    // 只有基本驗證通過後，才檢查是否已存在
    checkUsernameExists();
  }
};

// 添加實時檢查密碼
const checkPassword = () => {
  if (!form.value.password) {
    passwordError.value = '請輸入密碼';
  } else if (form.value.password.includes(' ')) {
    passwordError.value = '密碼不能包含空格';
  } else if (form.value.password.length < 8) {
    passwordError.value = '密碼至少需要8個字符';
  } else {
    passwordError.value = '';
  }
};

// 添加實時檢查郵箱
const checkEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!form.value.email) {
    emailError.value = '請輸入郵箱';
  } else if (!emailRegex.test(form.value.email)) {
    emailError.value = '請輸入有效的郵箱地址';
  } else {
    emailError.value = '';
  }
};

// 使用 watch 監聽表單變化，自動重新驗證
watch(() => form.value.username, checkUsername);
watch(() => form.value.password, checkPassword);
watch(() => form.value.email, checkEmail);

// 表單驗證
const validateForm = () => {
  // 先清空之前的錯誤信息
  usernameError.value = '';
  passwordError.value = '';
  emailError.value = '';
  
  // 再次調用各個字段的檢查函數
  checkUsername();
  checkPassword();
  checkEmail();
  
  // 返回是否所有字段都驗證通過
  return !usernameError.value && !passwordError.value && !emailError.value;
};

// 先驗證再註冊
const validateAndRegister = async () => {
  if (!validateForm()) {
    return;
  }
  
  await handleRegister();
};

// 處理註冊邏輯
const handleRegister = async () => {
  try {
    await axios.post('/api/auth/register', form.value);
    
    // 顯示成功提示框
    registerSuccess.value = true;
    
    // 2秒後跳轉
    setTimeout(() => {
      router.push('/login');
    }, 2000);
    
  } catch (error: any) {
    errorMessage.value = error.response?.data || '註冊失敗';
  }
};
</script>

<template>
  <div class="page-container">
    <!--註冊容器-->
    <div class="auth-container register">
      <h1>創建新賬號</h1>
      
      <!--註冊成功提示區-->
      <div v-if="registerSuccess" class="success-message">
          <el-icon class="success-icon"><CircleCheck /></el-icon>
          <h3>🎉 註冊成功！</h3>
          <p>歡迎 {{ form.username }}</p>
          <p>用戶類型：<span class="role-tag">{{ roleLabel }}</span></p>
          <p>2秒後跳轉到登錄頁...</p>
      </div>
      
      <!--註冊表單-->
      <form v-else @submit.prevent="validateAndRegister" class="register-form">
        <!--用戶名輸入組-->
        <div class="form-group">
          <el-input
            v-model="form.username"
            placeholder="用戶名"
            class="input-item"
            prefix-icon="User"
            :loading="checkingUsername"
          />
          <div v-if="usernameError" class="field-error">{{ usernameError }}</div>
        </div>
        
        <!--密碼輸入組-->
        <div class="form-group">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密碼"
            class="input-item"
            prefix-icon="Lock"
            show-password
          />
          <div v-if="passwordError" class="field-error">{{ passwordError }}</div>
        </div>
        
        <!--郵箱輸入組-->
        <div class="form-group">
          <el-input
            v-model="form.email"
            placeholder="郵箱"
            class="input-item"
            prefix-icon="Message"
          />
          <div v-if="emailError" class="field-error">{{ emailError }}</div>
        </div>
        
        <!--表單操作區-->
        <div class="form-actions">
          <el-button 
            type="primary" 
            native-type="submit" 
            class="auth-button"
            :disabled="!!usernameError || !!passwordError || !!emailError"
          >註冊</el-button>
        </div>
        
        <!--登錄跳轉-->
        <div class="auth-link">
          已有賬號？ <router-link to="/login">去登錄</router-link>
        </div>
      </form>
      
      <!--錯誤提示區-->
      <div v-if="errorMessage" class="error-message">
        <el-alert
          :title="errorMessage"
          type="error"
          :closable="false"
          show-icon
        />
      </div>
    </div>
  </div>
</template>

<style src="../styles/auth_style.css"></style>