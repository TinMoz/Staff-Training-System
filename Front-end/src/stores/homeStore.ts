//homeStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useUserStore } from './userStore';
import { useCourseStore } from './courseStore';
import service from '../utils/request';
// 輸出 HomeStore 到 Vue 組件
// 此Store主要用於定義首頁的狀態和行為
export const useHomeStore = defineStore('home', () => {
  // 引入其他store
  const userStore = useUserStore();
  const courseStore = useCourseStore();
  
  // 狀態
  const recentCourses = ref<Array<any>>([]);
  const timetable = ref<Array<any>>([]);
  const pendingRequests = ref<number>(0);
  const stats = ref({
    totalCourses: 0,
    enrolledCourses: 0,
    completedChapters: 0
  });
  
  // 分頁狀態
  const currentPage = ref(1);
  const pageSize = ref(4);
  
  // 計算屬性：獲取實際課程總數
  const actualTotalCourses = computed(() => {
    return courseStore.courses.length;
  });
  
  // 合併課程數據
  const mergedCourseData = computed(() => {
    // 首先創建一個Map，以courseId為鍵
    const courseMap = new Map();
    
    // 添加進度信息
    recentCourses.value.forEach(course => { // 遍歷最近課程
      courseMap.set(course.courseId, { 
        ...course, 
        id: `course-${course.courseId}`, 
        weekday: undefined,
        startTime: undefined,
        endTime: undefined,
        completed: course.progress === 100 // 添加完成狀態標記
      });
    });
    
    // 添加時間表信息
    timetable.value.forEach(item => { // 遍歷時間表
      const courseId = item.courseId; 
      if (courseMap.has(courseId)) {
        // 如果課程已經存在，則合併信息
        const existingCourse = courseMap.get(courseId);
        courseMap.set(courseId, {
          ...existingCourse,
          weekday: item.weekday,
          startTime: item.startTime,
          endTime: item.endTime
        });
      } else {
        // 如果課程不存在，則添加新條目
        courseMap.set(courseId, {
          ...item,
          id: `timetable-${courseId}-${item.chapterTitle}`,
          progress: undefined
        });
      }
    });
    
    // 轉換回數組並排序
    return Array.from(courseMap.values()).sort((a, b) => {
      // 首先按星期排序 - 使用嚴格比較
      const dayA = a.weekday === undefined ? 8 : a.weekday;
      const dayB = b.weekday === undefined ? 8 : b.weekday;
      if (dayA !== dayB) return dayA - dayB;
      
      // 如果星期相同，按時間排序
      if (a.startTime && b.startTime) {
        return a.startTime.localeCompare(b.startTime);
      }
      
      // 如果沒有時間信息，按課程名稱排序
      return a.courseTitle.localeCompare(b.courseTitle);
    });
  });
  
  // 計算屬性:分頁
  const paginatedCourseData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return mergedCourseData.value.slice(start, end);
  });
  
  // 獲取星期名稱
  function getWeekdayName(day: number | undefined) {
    if (day === undefined) return '未指定';
    const weekdays = ['週一', '週二', '週三', '週四', '週五', '週六', '週日'];
    return weekdays[(day % 7) - 1 >= 0 ? (day % 7) - 1 : 6];
  }
  
  // 獲取待處理請求數
  async function fetchPendingRequests() {
    try {
      const res = await service.get('/api/enrollment/pending', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      pendingRequests.value = res.data.length;
    } catch (error) {
      console.error('獲取請求數失敗:', error);
      pendingRequests.value = 0; // 確保出錯時顯示0
    }
  }
  
  // 獲取最近課程
  async function fetchRecentCourses() {
    if (userStore.role !== 'ADMIN') { // 普通用戶才獲取
      try {
        const res = await service.get('/api/progress/courses', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        recentCourses.value = res.data;
      } catch (error) {
        console.error('獲取最近課程失敗:', error);
      }
    }
  }
  
  // 獲取時間表
  async function fetchTimetable() {
    try {
      const res = await service.get('/api/progress/timetable?limit=5', { // 增加顯示數量
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      timetable.value = res.data;
    } catch (error) {
      console.error('獲取時間表失敗:', error);
    }
  }
  
  // 獲取統計信息
  async function fetchStats() {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;
      
      // 管理員和普通用戶不同接口
      const url = userStore.role === 'ADMIN' 
        ? '/api/progress/admin-stats' // 管理員的統計信息
        : '/api/progress/stats'; // 普通用戶的統計信息
  
      const res = await service.get(url, {
        headers: { Authorization: `Bearer ${token}` }
      });
      // 根據用戶角色權限顯示不同內容
      stats.value = userStore.role === 'ADMIN' 
        ? {
            totalCourses: actualTotalCourses.value,
            enrolledCourses: 0, 
            completedChapters: 0 // 管理員不顯示這些數據
          }
        : {
            totalCourses: actualTotalCourses.value, 
            enrolledCourses: res.data.enrolled || 0,
            completedChapters: res.data.completedChapters || 0 // 普通用戶顯示這些數據
          };
    } catch (error) {
      console.error('獲取統計信息失敗:', error);
    }
  }
  
  // 初始化數據
  async function initialize() {
    if (userStore.isAuthenticated) {
      if (courseStore.courses.length === 0) {
        await courseStore.fetchCourses();
      }
      
      await Promise.all([ // 並行獲取數據
        fetchRecentCourses(),
        fetchTimetable(),
        fetchStats(),
        userStore.role === 'ADMIN' && fetchPendingRequests()
      ]);
    }
  }
  
  // 處理頁碼變化
  function handlePageChange(newPage: number) {
    currentPage.value = newPage;
  }
  
  return {
    // 狀態
    recentCourses,
    timetable,
    pendingRequests,
    stats,
    currentPage,
    pageSize,
    
    // 計算屬性
    actualTotalCourses,
    mergedCourseData,
    paginatedCourseData,
    
    // 方法
    getWeekdayName,
    fetchPendingRequests,
    fetchRecentCourses,
    fetchTimetable,
    fetchStats,
    initialize,
    handlePageChange
  };
});