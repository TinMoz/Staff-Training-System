<!--HomeView.vue-->
<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '../stores/userStore'
import { useHomeStore } from '../stores/homeStore'
import { Collection, Bell, Notebook, Finished, AlarmClock, Clock, List, Setting } from '@element-plus/icons-vue'

// 使用 Store
const userStore = useUserStore()
const homeStore = useHomeStore()

// 組件掛載時初始化數據
onMounted(() => {
  homeStore.initialize()
})
</script>

<template>
  <div class="page-container" :class="{ 'user-home': userStore.isAuthenticated && userStore.role === 'USER' }">
    <!-- 添加一個頂部安全區域 div -->
    <div class="top-safe-area" aria-hidden="true"></div>
    
    <div class="home-view">
      <!-- 未登錄狀態 -->
      <div v-if="!userStore.isAuthenticated" class="login-prompt">
        <h1>歡迎來到課程系統</h1>
        <p>請先登錄以查看您的學習數據</p>
        <router-link to="/login" class="login-btn no-hover">
          <el-button type="primary" size="large">立即登錄</el-button>
        </router-link>
      </div>

      <!-- 已登錄狀態 -->
      <template v-else>
        <h1>歡迎回來，{{ userStore.username }}！🎉</h1>
        
        <!-- 統計卡片 -->
        <div class="stats-grid">
          <el-card class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon"><Collection /></el-icon>
              <div>
                <div class="stat-label">總課程數</div>
                <div class="stat-value">{{ homeStore.actualTotalCourses }}</div>
              </div>
            </div>
          </el-card>
          
          <!-- 管理員專屬卡片 -->
          <el-card v-if="userStore.role === 'ADMIN'" class="stat-card">
            <div class="stat-content">
              <el-icon class="stat-icon"><Bell /></el-icon>
              <div>
                <div class="stat-label">待處理請求</div>
                <div class="stat-value">{{ homeStore.pendingRequests }}</div>
              </div>
            </div>
          </el-card>

          <!-- 普通用戶卡片 -->
          <template v-if="userStore.role !== 'ADMIN'">
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon"><Notebook /></el-icon>
                <div>
                  <div class="stat-label">已選課程</div>
                  <div class="stat-value">{{ homeStore.stats.enrolledCourses }}</div>
                </div>
              </div>
            </el-card>
            
            <el-card class="stat-card">
              <div class="stat-content">
                <el-icon class="stat-icon"><Finished /></el-icon>
                <div>
                  <div class="stat-label">完成章節</div>
                  <div class="stat-value">{{ homeStore.stats.completedChapters }}</div>
                </div>
              </div>
            </el-card>
          </template>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <el-button 
            type="primary" 
            @click="$router.push('/courses')"
          >
            <el-icon class="icon-left"><List /></el-icon>
            瀏覽課程
          </el-button>
          <el-button 
            v-if="userStore.role !== 'ADMIN'"
            type="success"
            @click="$router.push('/user-courses')"
          >
            <el-icon class="icon-left"><Notebook /></el-icon>
            我的課程
          </el-button>
          <el-button 
            v-if="userStore.role !== 'ADMIN'"
            type="warning"
            @click="$router.push('/user-timetable')"
          >
            <el-icon class="icon-left"><Clock /></el-icon>
            時間表
          </el-button>
          <el-button 
            v-if="userStore.role === 'ADMIN'"
            type="danger"
            @click="$router.push('/admin')"
          >
            <el-icon class="icon-left"><Setting /></el-icon>
            管理後台
          </el-button>
        </div>

        <!-- 合併後的課程時間表（僅普通用戶） -->
        <el-card v-if="userStore.role !== 'ADMIN'" class="section-card">
          <template #header>
            <div class="card-header">
              <el-icon><AlarmClock /></el-icon>
              <span>我的課程和時間表</span>
            </div>
          </template>
          
          <div v-if="homeStore.mergedCourseData.length > 0">
            <!-- 使用分頁後的數據 -->
            <div v-for="item in homeStore.paginatedCourseData" :key="item.id" class="timetable-item">
              <div class="time-info">
                <div class="weekday">
                  {{ item.completed ? '已完成' : homeStore.getWeekdayName(item.weekday) }}
                </div>
                <div class="time-range" v-if="!item.completed">
                  {{ item.startTime || '未安排' }} - {{ item.endTime || '未安排' }}
                </div>
                <div class="time-range completed" v-else>
                  <el-tag type="success" size="small">100%</el-tag>
                </div>
              </div>
              <div class="course-details">
                <div class="course-title">{{ item.courseTitle }}</div>
                <div class="chapter">{{ item.chapterTitle }}</div>
                <div v-if="item.progress !== undefined" class="progress-container">
                  <el-progress 
                    :percentage="item.progress" 
                    :stroke-width="8"
                    :color="item.progress === 100 ? '#67c23a' : '#409eff'"
                    style="margin: 8px 0;"
                  />
                </div>
              </div>
              <div class="course-actions">
                <el-button 
                  type="text" 
                  @click="$router.push(`/courses/${item.courseId}`)"
                >
                  {{ item.progress === 100 ? '查看詳情' : '繼續學習' }} →
                </el-button>
              </div>
            </div>
            
            <!-- 添加分頁控件 -->
            <div class="pagination-container" v-if="homeStore.mergedCourseData.length > homeStore.pageSize">
              <el-pagination
                background
                layout="prev, pager, next"
                :total="homeStore.mergedCourseData.length"
                :page-size="homeStore.pageSize"
                :current-page="homeStore.currentPage"
                @current-change="homeStore.handlePageChange"
              />
            </div>
          </div>
          <div v-else class="empty-tip">
            暫無學習課程和安排
          </div>
          
          <div class="view-all-link" v-if="homeStore.mergedCourseData.length > 0">
            <el-button type="primary" plain @click="$router.push('/user-timetable')">
              查看完整時間表
            </el-button>
          </div>
        </el-card>
      </template>
    </div>
  </div>
</template>

<style src="../styles/home_style.css"></style>
<style scoped>
/* 頂部安全區域樣式 */
.top-safe-area {
  height: 10px;
  width: 100%;
  visibility: hidden;
}

.icon-left {
  margin-right: 6px;
  vertical-align: middle;
}

/* 確保按鈕內容居中 */
.quick-actions .el-button {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 修改合併後的時間表項樣式 */
.timetable-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #eee;
  align-items: center;
  transition: background-color 0.2s;
}

.timetable-item:hover {
  background-color: #f9f9f9;
}

.timetable-item:last-child {
  border-bottom: none;
}

.time-info {
  text-align: center;
  min-width: 100px;
  margin-right: 16px;
  border-right: 1px solid #eee;
  padding-right: 16px;
}

.weekday {
  font-weight: 700;
  color: #409eff;
  font-size: 16px;
  margin-bottom: 8px;
}

.time-info .completed {
  display: flex;
  justify-content: center;
}

.course-details {
  flex: 1;
}

.course-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 4px;
}

.progress-container {
  margin-top: 8px;
}

.course-actions {
  min-width: 100px;
  text-align: right;
}

.view-all-link {
  text-align: center;
  margin-top: 20px;
}

/* 添加分頁樣式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.pagination-container .el-pagination {
  padding: 10px 0;
}
</style>