<!-- App.vue -->
<script setup lang="ts">
import { useUserStore } from './stores/userStore'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User } from '@element-plus/icons-vue'
//調用UserStore
const userStore = useUserStore()
//路由參數
const route = useRoute()  //獲取當前路由
const router = useRouter() //獲取當前路由的完整路徑
const routeKey = computed(() => `${route.fullPath}`)//使用路由的完整路徑作為鍵值，這樣當路由改變時，組件會重新渲染

//處理登出邏輯
//登出後返回登錄頁面
const handleLogout = () => {
  userStore.logout()
  router.push('/login') 
}
</script>

<template>
  <el-container class="app-container">
    <!--頂欄導航-->
     <el-header class="nav-container">
      <!--左側導航-->
      <div class="nav-left">
        <router-link to="/home">首頁</router-link>
        <router-link to="/courses">課程列表</router-link>
        <router-link v-if="userStore.role === 'ADMIN'" to="/admin">管理員頁面</router-link>
        <router-link v-if="userStore.role === 'USER'" to="/user-courses">你的課程</router-link>
        <router-link v-if="userStore.role === 'USER'" to="/user-timetable">時間表</router-link>
      </div>
      <!--右側導航-->
      <div class="nav-right">
        <template v-if="!userStore.isAuthenticated">
          <router-link to="/login">登錄</router-link>
        </template>
        <template v-else>
          <span class="user-info">
            <el-icon><User /></el-icon>
            <span class="username">{{ userStore.username }}</span>
          </span>
          <el-button class="logout-btn" @click="handleLogout">退出</el-button>
        </template>
      </div>
    </el-header>
    <!--主內容區域-->
    <el-main>
    <div class="content-container">
      <transition name="fade-in" mode="out-in">
        <router-view :key="routeKey" />
      </transition>
    </div>
    ​</el-main>
  </el-container>
</template>


<style src="./styles/main.css"></style>
