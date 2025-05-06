<!--CourseDetail.vue-->
<script setup lang="ts">
import { useCourseStore } from '../stores/courseStore';
import { useUserStore } from '../stores/userStore';
import { useCourseDetailStore } from '../stores/coursedetailStore';
import { onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { Warning } from '@element-plus/icons-vue';

// 使用 Store
const courseStore = useCourseStore();
const userStore = useUserStore();
const courseDetailStore = useCourseDetailStore();
const route = useRoute();

//獲得路由參數 初始化 自動保存
onMounted(async () => {
  const courseId = route.params.id as string;
  await courseDetailStore.initialize(courseId);
  courseDetailStore.startAutoSave();
});

// 結束自動保存 重置
onUnmounted(() => {
  courseDetailStore.stopAutoSave();
  courseDetailStore.reset(); 
});
</script>

<template>
  <div class="page-container course-detail-page">
    <!--課程詳情頁面容器-->
    <div class="course-detail" v-loading="courseDetailStore.loading">
      <!--課程頭部信息區域-->
      <div class="course-header">
        <!--課程標題區域-->
        <div class="course-title-section">
          <h1>{{ courseStore.detail?.title }}</h1>
          <div class="course-code">{{ courseStore.detail?.courseCode }}</div>
        </div>
        <!--操作按鈕區域-->
        <div class="actions">
          <!--報名操作區域-->
          <div class="enrollment-action">
            <!--報名按鈕，僅對未報名的普通用戶顯示-->
            <el-button 
              v-if="!courseDetailStore.isEnrolled && userStore.role === 'USER' && userStore.isAuthenticated"
              type="primary" 
              @click="courseDetailStore.applyCourse"
              class="enroll-button"
              :loading="courseDetailStore.checkingEnrollment"
              :disabled="!courseDetailStore.canEnroll"
            >
              報名課程
              <!--提示圖標，顯示報名限制說明-->
              <el-tooltip
                content="系統會自動檢查時間衝突和課程數量限制"
                placement="top"
                effect="light"
              >
                <el-icon class="info-icon"><Warning /></el-icon>
              </el-tooltip>
            </el-button>
            
            <!--報名警告提示，當無法報名時顯示-->
            <div v-if="!courseDetailStore.canEnroll" class="enrollment-warning">
              <el-alert
                :title="courseDetailStore.enrollmentCheckMessage"
                type="warning"
                show-icon
                :closable="false"
                style="margin-top: 10px;"
              />
            </div>
          </div>
          
          <!--返回課程列表按鈕-->
          <router-link :to="`/courses`" class="back-link no-hover">
            <el-button>回到課程列表</el-button>
          </router-link>
        </div>
      </div>

      <!--分隔線-->
      <el-divider />
      
      <!--課程元數據信息區域-->
      <div class="metadata">
        <!--學分信息-->
        <div class="metadata-item">
          <div class="metadata-label">學分</div>
          <div class="metadata-value">{{ courseStore.detail?.credits }}</div>
        </div>
        
        <!--章節數量信息-->
        <div class="metadata-item">
          <div class="metadata-label">章節數量</div>
          <div class="metadata-value">{{ courseStore.detail?.chapters?.length ?? 0 }}</div>
        </div>

        <!--創建時間信息-->
        <div class="metadata-item">
          <div class="metadata-label">創建時間</div>
          <div class="metadata-value">{{ courseDetailStore.formatDate(String(courseStore.detail?.createdAt ?? '')) }}</div>
        </div>

        <!--指導老師信息-->
        <div class="metadata-item">
          <div class="metadata-label">指導老師</div>
          <div class="metadata-value">{{  courseStore.detail?.teacher || '未指定' }}</div>
        </div>
      </div>
      
      <!--課程描述區域-->
      <div class="course-description">
        <h3>課程描述</h3>
        <p>{{ courseStore.detail?.description }}</p>
      </div>
      
      <!--課程內容區域-->
      <div class="course-content">
        <h3>課程內容</h3>
        <!--可折疊的章節列表-->
        <el-collapse v-model="courseDetailStore.activeChapter" class="chapter-list">
          <!--每個章節項目-->
          <el-collapse-item
            v-for="(chapter, index) in courseDetailStore.paginatedChapters"
            :key="chapter.id"
            :name="chapter.id"
            class="chapter-item"
          >
            <!--章節標題區域-->
            <template #title>
              <div class="chapter-title-wrapper">
                <!--章節序號-->
                <span class="chapter-index">{{ (courseDetailStore.currentPage - 1) * courseDetailStore.pageSize + index + 1 }}.</span>
                <div class="chapter-meta">
                  <!--章節名稱-->
                  <span class="chapter-name">{{ chapter.title }}</span>
                  <!--時間標籤-->
                  <div class="time-tags">
                    <!--星期幾標籤-->
                    <el-tag type="info" size="small">
                      {{ courseDetailStore.getChapterTime(chapter).weekday }}
                    </el-tag>
                    <!--時間範圍標籤-->
                    <el-tag type="success" size="small">
                      {{ courseDetailStore.getChapterTime(chapter).start }} - {{ courseDetailStore.getChapterTime(chapter).end }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </template>

            <!--章節內容顯示區域-->
            <div class="chapter-content" v-html="chapter.content"></div>
            <!--章節腳註區域-->
            <div class="chapter-footer">
              <!--學習時長標籤-->
              <el-tag v-if="chapter.duration" type="warning" effect="light">
                預計學習時長: {{ chapter.duration }}分鐘
              </el-tag>
            </div>
          </el-collapse-item>
        </el-collapse>
        
        <!--章節分頁控件-->
        <div class="chapter-pagination" v-if="(courseStore.detail?.chapters ?? []).length > courseDetailStore.pageSize">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="courseStore.detail?.chapters?.length || 0"
            :page-size="courseDetailStore.pageSize"
            :current-page="courseDetailStore.currentPage"
            @current-change="courseDetailStore.handlePageChange"
          />
        </div>
        
        <!--無章節內容時的提示-->
        <div v-if="!courseStore.detail?.chapters?.length" class="no-chapters">
          暫無章節內容
        </div>
      </div>
      
      <!--課程頁腳區域-->
      <div class="course-footer">
        <!--返回首頁按鈕-->
        <el-button 
          type="primary" 
          @click="$router.push('/home')"
        >
          返回首頁
        </el-button>
      </div>
    </div>
  </div>
</template>

<style src="../styles/coursedetail_style.css"></style>

<style scoped>
.enrollment-action {
  display: flex;
  flex-direction: column;
}

.enrollment-warning {
  width: 100%;
  margin-top: 10px;
}

.enrollment-warning .el-alert {
  margin: 0;
}
</style>
