<!--AdminView.vue-->
<script setup lang="ts">
import { useCourseStore } from '../stores/courseStore';
import { useAdminStore } from '../stores/adminStore';
import { ElForm } from 'element-plus';
import { computed, ref, onMounted } from 'vue';
import { Plus } from '@element-plus/icons-vue';

// 導入相關store
const courseStore = useCourseStore();
const adminStore = useAdminStore();

// 引用DOM元素
const courseForm = ref<InstanceType<typeof ElForm>>();
const showAddDialog = ref(false);

// 從courseStore獲取課程列表
const courses = computed(() => courseStore.courses);

// 分頁後的課程數據 隨著課程數量變化
const paginatedCourses = computed(() => {
  const start = (adminStore.currentPage - 1) * adminStore.pageSize;
  const end = start + adminStore.pageSize;
  return courses.value.slice(start, end);
});

// 初始化
onMounted(async () => {
  await adminStore.initialize();
});

// 處理添加課程
const handleAddCourse = () => {
  adminStore.resetCurrentCourse();
  showAddDialog.value = true;
};

// 提交表單
const submitForm = async () => {
  try {
    await courseForm.value?.validate();
    const success = await adminStore.saveCourse();
    if (success) {
      showAddDialog.value = false;
    }
  } catch (error) {
    // 表單驗證失敗
  }
};

// 處理編輯課程
const editCourse = (course: any) => {
  adminStore.editCourse(course);
  showAddDialog.value = true;
};

// 處理請求
const approveRequest = (id: number) => adminStore.processRequest(id, 'approved');
const rejectRequest = (id: number) => adminStore.processRequest(id, 'rejected');
</script>

<template>
  <div class="page-container">
    <div class="admin-view">
      <h1>管理員頁面</h1>
      <el-button type="primary" @click="handleAddCourse">
        添加課程
      </el-button>
      <el-table
        :data="paginatedCourses"
        style="width: 100%"
        v-loading="adminStore.loading"
        stripe
      >       
        <el-table-column prop="title" label="課程名稱" />
        <el-table-column prop="chapters.length" label="章節數目" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="詳情">
          <template #default="scope">
            <router-link :to="`/courses/${scope.row.id}`">查看詳情</router-link>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button @click="editCourse(scope.row)">編輯</el-button>
            <el-button type="danger" @click="adminStore.deleteCourse(scope.row.id)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 課程分頁組件 -->
      <div class="pagination-container" v-if="courses.length > adminStore.pageSize">
        <el-pagination
          layout="prev, pager, next"
          :total="courses.length"
          :page-size="adminStore.pageSize"
          :current-page="adminStore.currentPage"
          @current-change="adminStore.handlePageChange"
        />
      </div>

      <el-dialog 
        v-model="showAddDialog" 
        title="課程編輯" 
        class="course-dialog"
        :append-to-body="true" 
        :show-close="false"
        width="800px"
      >
        <el-form 
          :model="adminStore.currentCourse" 
          :rules="adminStore.formRules" 
          ref="courseForm"
          label-width="120px"
        >
          <el-form-item label="課程名稱" prop="title">
            <el-input v-model="adminStore.currentCourse.title" placeholder="例如：企業人力資源管理" />
          </el-form-item>
          
          <el-form-item label="課程代碼" prop="courseCode">
            <el-input v-model="adminStore.currentCourse.courseCode" placeholder="例如：HR-101" />
          </el-form-item>

          <el-form-item label="課程描述" prop="description">
            <el-input 
              v-model="adminStore.currentCourse.description" 
              type="textarea" 
              :rows="3"
              placeholder="詳細描述課程內容、目標和收益，例如：本課程涵蓋現代企業人力資源管理的核心理念與實踐..."
            />
          </el-form-item>

          <el-form-item label="課程學分" prop="credits">
            <el-input
              v-model.number="adminStore.currentCourse.credits" 
              :min="1" 
              :max="10"
              style="width: 50px;"
              placeholder="1-10"
            />
          </el-form-item>

          <el-form-item label="指導老師" prop="teacher">
            <el-input
              v-model="adminStore.currentCourse.teacher"
              placeholder="例如：張偉老師"
              style="width: 200px;"
            />
          </el-form-item>

          <el-form-item label="章節列表" prop="chapters">
            <transition-group name="chapter" tag="div">
              <div 
                v-for="(chapter, index) in adminStore.currentCourse.chapters" 
                :key="index"
                class="chapter-item"
              >
                <el-input
                  v-model="chapter.title"
                  placeholder="例如：第一章 - 入門概述"
                  style="width: 200px; margin-right: 10px;"
                />
                <el-input
                  v-model="chapter.content"
                  placeholder="章節詳細內容，例如：本章節將介紹核心概念和基礎知識..."
                  style="flex: 1;"
                  type="textarea"
                />

                <el-input
                  v-model.number="chapter.duration"
                  placeholder="120-300"
                  type="number"
                  :min="120"
                  :max="300"
                  :step="5"
                  style="width: 180px;"
                >
                  <template #append>分鐘</template>
                </el-input>

                <el-button 
                  type="danger" 
                  circle 
                  @click="adminStore.removeChapter(index)"
                  style="margin-left: 10px;"
                  class="remove-btn"
                >
                  刪除
                </el-button>
              </div>
            </transition-group>
            <el-button 
              type="primary" 
              @click="adminStore.addChapter"
              plain
              class="add-chapter-btn"
            >
              <el-icon :size="16" style="margin-right: 8px"><Plus /></el-icon>
              添加章節
            </el-button>
          </el-form-item>
        </el-form>

        <template #footer>
          <el-button class="cancel-btn" @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitForm" class="save-btn">
            保存
          </el-button>
        </template>
      </el-dialog>

      <h2>待處理報名請求</h2>
      <el-table :data="adminStore.paginatedRequests" stripe>
        <el-table-column prop="username" label="用戶" />
        <el-table-column prop="courseTitle" label="課程" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button @click="approveRequest(row.id)">通過</el-button>
            <el-button type="danger" @click="rejectRequest(row.id)">拒絕</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 請求分頁組件 -->
      <div class="pagination-container" v-if="adminStore.pendingRequests.length > adminStore.requestPageSize">
        <el-pagination
          layout="prev, pager, next"
          :total="adminStore.pendingRequests.length"
          :page-size="adminStore.requestPageSize"
          :current-page="adminStore.currentRequestPage"
          @current-change="adminStore.handleRequestPageChange"
        />
      </div>
    </div>

    <el-button 
      type="primary" 
      @click="$router.push('/home')"
      style="margin-top: 20px;"
    >
      返回首頁
    </el-button>
  </div>
</template>

<style src="../styles/admin_style.css"></style>

