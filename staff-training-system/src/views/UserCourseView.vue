<!--UserCourseView.vue-->
<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, onMounted, computed } from 'vue';
import axios from '../utils/request';

// 確保初始化為空數組
const courses = ref<Array<any>>([]);
const currentPage = ref(1);
const pageSize = ref(8);

// 計算屬性：課程總數
const coursesCount = computed(() => courses.value.length);

// 分頁後的課程
const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return courses.value.slice(start, end);
});

const handlePageChange = (newPage: number) => {
  currentPage.value = newPage;
};

// 加載課程數據
const loadCourses = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('未登錄或登錄失效');
      return;
    }
    
    const response = await axios.get('/api/progress/courses', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    
    // 確保正確賦值
    courses.value = Array.isArray(response.data) ? response.data : [];
    
  } catch (error) {
    console.error('獲取課程進度失敗:', error);
    ElMessage.error('獲取課程數據失敗');
    // 確保在失敗時也有有效值
    courses.value = [];
  }
};

onMounted(() => {
  loadCourses();
});

const dropCourse = async (courseId: string) => {
  try {
    await ElMessageBox.confirm('確認退課嗎？課程進度將被清除', '警告')
    const token = localStorage.getItem('token');
    await axios.delete(`/api/progress/${courseId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    
    // 刷新課程列表
    await loadCourses();
    ElMessage.success('退課成功');
  } catch (error) {
    if (!String(error).includes("cancel")) {
      ElMessage.error('操作失敗');
    }
  }
};
</script>

<template>
  <div class="page-container">
    <div class="user-courses">
      <h1>你的課程</h1>
      
      <el-alert
        v-if="coursesCount >= 5"
        title="您已選擇的課程數量接近上限"
        type="warning"
        :description="`系統限制每位用戶最多可選6門課程，您當前已選擇 ${coursesCount} 門課程`"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      />
      
      <div v-if="coursesCount === 0" class="empty-tip">
        暫無學習記錄
      </div>
      <el-table :data="paginatedCourses" style="width: 100%" stripe v-else>
        <el-table-column prop="courseTitle" label="課程名稱" />
        <el-table-column prop="chapterTitle" label="當前章節" />
        <el-table-column label="進度">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" />
          </template>
        </el-table-column>
        <el-table-column label="最後學習時間">
          <template #default="{ row }">
            {{ new Date(row.lastAccessed).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="詳情">
          <template #default="{ row }">
            <router-link :to="`/courses/${row.courseId}`">
              課程詳情
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button 
              type="danger" 
              @click="dropCourse(row.courseId)"
            >
              退課
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分頁組件 -->
      <div class="pagination-container" v-if="coursesCount > pageSize">
        <el-pagination
          layout="prev, pager, next"
          :total="coursesCount"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
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

<style scoped>
.user-courses {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 20px;
}
</style>