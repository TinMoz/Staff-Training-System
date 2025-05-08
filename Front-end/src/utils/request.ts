//request.ts
import axios from 'axios';
import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios';

//此為axios的請求攔截器，主要用於攔截請求並添加JWT token到請求頭中
//建立AnxiosInstance
const service: AxiosInstance = axios.create({
    baseURL: "https://staff-training-system-744605943289.us-central1.run.app", //後端API的基礎URL，建立後端鏈接
    timeout: 10000, //請求超時時間
});

// 請求攔截器
service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => { 
      const token = localStorage.getItem("token"); //從localStorage獲取token
      if (token && config.headers) {    //如果token存在，則將其添加到請求頭中
        config.headers.Authorization = `Bearer ${token}`;  //添加Authorization頭
      }
      return config;
    },
    (error) => {
      console.error('API 錯誤詳情:', {
        url: error.config?.url,
        method: error.config?.method,
        status: error.response?.status,
        data: error.response?.data
      });
      return Promise.reject(error); //如果請求失敗，則返回錯誤
    }
  );
  
// 輸出攔截器
export default service;