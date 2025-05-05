//request.ts
import axios from 'axios';
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios';

//建立AnxiosInstance
const service: AxiosInstance = axios.create({
    baseURL: "http://localhost:8080", // 确保此变量已配置
    timeout: 10000,
});

// 2. 請求攔截（JWT）
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      // 從 localStorage 獲取 token（根據實際存儲位置調整）
      const token = localStorage.getItem("token");
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
  
// 3. 导出实例
export default service;