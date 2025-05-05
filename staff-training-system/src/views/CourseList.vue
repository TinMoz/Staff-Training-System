<!--CourseList.vue-->
<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { useCourseStore } from '../stores/courseStore';
import { useUserStore } from '../stores/userStore';

const userStore = useUserStore();
const courseStore = useCourseStore();
const loading = ref(true);
const searchQuery = ref(''); // 新增：搜索詞
const searchType = ref('title'); // 新增：搜索類型，默認按課程名稱搜索
const currentPage = ref(1)
const pageSize = ref(8)

// 計算屬性：過濾課程
const filteredCourses = computed(() => {
  if (!searchQuery.value) return courseStore.courses; // 無搜索詞時返回全部
  
  const query = searchQuery.value.toLowerCase();
  return courseStore.courses.filter(course => {
    // 根據選擇的搜索類型進行過濾
    switch (searchType.value) {
      case 'title':
        return course.title.toLowerCase().includes(query);
      case 'courseCode':
        return course.courseCode.toLowerCase().includes(query);
      case 'teacher':
        return course.teacher?.toLowerCase().includes(query);
      default:
        return course.title.toLowerCase().includes(query);
    }
  });
});

// 計算屬性：分頁後的課程
const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCourses.value.slice(start, end)
})


// 處理頁面變化
const handlePageChange = (newPage: number) => {
  currentPage.value = newPage
}

onMounted(async () => {
  try {
    await courseStore.fetchCourses();
  } catch (error) {
    console.error('加载课程失败:', error);
    courseStore.courses = [];
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="page-container">
    <div>
      <div class="course-list">
        <h1>课程列表</h1>
        
        <!-- 增加搜索區域，包含搜索類型選擇和搜索框 -->
        <div class="search-container">
          <input
            v-model="searchQuery"
            :placeholder="'請輸入' + 
              (searchType === 'title' ? '課程名稱' : 
              searchType === 'courseCode' ? '課程編號' : '指導老師') + 
              '搜索'"
            class="search-input"
          />
          <select v-model="searchType" placeholder="搜索方式" class="search-type">
            <option label="按課程名稱" value="title"></option>
            <option label="按課程編號" value="courseCode"></option>
            <option label="按指導老師" value="teacher"></option>
          </select>
        </div>
        <!-- 課程列表 -->
        <el-table 
          :data="paginatedCourses" 
          v-loading="loading" 
          style="width: 100%"
          stripe
        >
          <!-- 課程名稱列 -->
          <el-table-column prop="title" label="課程名稱" />
          <el-table-column prop="courseCode" label="課程編號"/>
          <!-- 積分列 -->
          <el-table-column prop="credits" label="學分" />
          <!-- 指導老師列 -->
          <el-table-column prop="teacher" label="指導老師" />
          <!-- 操作列（僅管理員可見） 可刪除課程-->
          <el-table-column label="操作" v-if="userStore.role === 'ADMIN'">
            <template #default="scope">
              <el-button type="danger" @click="courseStore.deleteCourse(scope.row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
          <!-- 詳情列 -->
          <el-table-column label="詳情">
            <template #default="scope">
              <router-link :to="`/courses/${scope.row.id}`">查看詳情</router-link>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分頁组件 -->
        <div class="pagination-container" v-if="filteredCourses.length > pageSize">
          <el-pagination
            layout="prev, pager, next"
            :total="filteredCourses.length"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </div>
      
      <!-- 無課程提示 -->
      <div v-if="filteredCourses.length === 0" class="empty-tip">
        {{ searchQuery ? '沒有相關課程' : '暫無課程數據，請聯系管理員添加。' }}
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
.search-container {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
  align-items: center;
}

.search-type {
  height: 38px;
  width: 120px;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #b9bdc7;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  outline: none;
  color: #606266;
  box-sizing: border-box;
}

.search-type:hover {
  border-color: #9498a1;
}

.search-type:focus {
  border-color: #409eff;
}

.search-input {
  flex: 1;
  height: 38px;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #b9bdc7;
  border-radius: 4px;
  background-color: white;
  color: #606266;
  outline: none;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.search-input:hover {
  border-color: #9498a1;
}

.search-input:focus {
  border-color: #409eff;
}


</style>