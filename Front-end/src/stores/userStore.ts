//userStore.ts
import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';

// 輸出 UserStore 到 Vue 組件
// 此Store主要用於管理用戶的狀態和行為
export const useUserStore = defineStore('user', {
  // 定義目前用戶狀態
  state: () => ({
    isAuthenticated: false,
    role: '',
    token: '',
    username: ''
  }),
  // 定義用戶的actions
  actions: {
    // 初始化用戶狀態
    async initialize() {
      const token = localStorage.getItem('token'); //從localStorage獲取token
      if (token) {
        try {
          const decoded = jwtDecode<{  // 使用Jwtdecode解析token中的role和username
            role: string, 
            username: string 
          }>(token);
          
          this.role = decoded.role.toUpperCase(); // 將role轉換為大寫
          this.username = decoded.username; // 獲取username
          this.token = token; // 獲取token
          this.isAuthenticated = true; // 設置用戶已認證
          
        } catch (error) {
          console.error('Token解析失敗:', error);
          this.logout();
        }
      }
    },
    logout() {  // 用戶登出
      localStorage.removeItem('token'); // 刪除localStorage中的token
      this.$reset();
    }
  }
});