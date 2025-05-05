//userStore.ts
import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';
export const useUserStore = defineStore('user', {
  state: () => ({
    isAuthenticated: false,
    role: '',
    token: '',
    username: ''
  }),
  actions: {
    async initialize() {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const decoded = jwtDecode<{ 
            role: string, 
            username: string 
          }>(token);
          
          this.role = decoded.role.toUpperCase();
          this.username = decoded.username;
          this.token = token;
          this.isAuthenticated = true;
          
        } catch (error) {
          console.error('Token解析失敗:', error);
          this.logout();
        }
      }
    },
    logout() {
      localStorage.removeItem('token');
      this.$reset();
    }
  }
});