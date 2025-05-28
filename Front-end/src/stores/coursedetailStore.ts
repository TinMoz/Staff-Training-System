//coursedetailStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import service from '../utils/request';
import { useCourseStore } from './courseStore';
import { useUserStore } from './userStore';
import { ElMessage } from 'element-plus';

// 輸出 CourseDetailStore 到 Vue 組件
// 定義課程詳情的 store
export const useCourseDetailStore = defineStore('courseDetail', () => {
  // 引入其他 store
  const courseStore = useCourseStore();
  const userStore = useUserStore();
  
  // 狀態
  const activeChapter = ref(null);
  const currentCourseId = ref<string>('');
  const currentProgress = ref<number>(0);
  const hasUnsavedProgress = ref<boolean>(false);
  const loading = ref(true);
  const isEnrolled = ref(false);
  const enrollmentStatus = ref('');
  const canEnroll = ref(true);
  const enrollmentCheckMessage = ref('');
  const checkingEnrollment = ref(false);
  
  // 分頁狀態
  const currentPage = ref(1);
  const pageSize = ref(4);
  
  // 日期格式化
  const weekDays = ['週一', '週二', '週三', '週四', '週五', '週六', '週日'];
  
  // 自動保存的定時器
  let autoSaveTimer: ReturnType<typeof setInterval>;
  
  // 計算屬性：分頁
  const paginatedChapters = computed(() => {
    if (!courseStore.detail?.chapters) return [];
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return courseStore.detail.chapters.slice(start, end);
  });
  
  // 初始化課程詳情
  async function initialize(courseId: string) {
    try {
      loading.value = true;
      currentCourseId.value = courseId;
      await courseStore.fetchCourseDetail(courseId);
      await checkEnrollment();
      
      if (userStore.isAuthenticated && userStore.role === 'USER' && !isEnrolled.value) {
        await checkEnrollmentEligibility();
      }
    } catch (error) {
      console.error('加載課程詳情失敗:', error);
      ElMessage.error('無法加載課程詳情，請檢查權限或網絡');
    } finally {
      loading.value = false;
    }
  }
  
  // 格式化時間
  function formatTime(timeStr?: string) {
    if (!timeStr) return '--:--';
    return timeStr.slice(0, 5);
  }
  
  // 獲取章節時間信息
  function getChapterTime(chapter: any) {
    return {
      weekday: chapter.weekday !== undefined ? 
        weekDays[(chapter.weekday % 7) - 1 >= 0 ? (chapter.weekday % 7) - 1 : 6] : '未設置',
      start: formatTime(chapter.startTime),
      end: formatTime(chapter.endTime)
    };
  }
  
  // 處理頁碼變化
  function handlePageChange(newPage: number) {
    currentPage.value = newPage;
    activeChapter.value = null;
  }
  
  // 開始自動保存
  function startAutoSave() {
    autoSaveTimer = setInterval(() => {
      if (hasUnsavedProgress.value) {
        saveProgress();
        hasUnsavedProgress.value = false;
      }
    }, 30000); // 30秒自動保存一次
  }
  
  // 停止自動保存
  function stopAutoSave() {
    clearInterval(autoSaveTimer);
    console.log('Autosave stopped.');
  }
  
  // 保存進度
  async function saveProgress() {
    try {
      await service.post('/api/course/progress', {
        courseId: currentCourseId.value,
        progress: currentProgress.value
      });
      console.log('Progress saved successfully!');
    } catch (error) {
      console.error('Error saving progress:', error);
    }
  }
  
  // 格式化日期
  function formatDate(dateStr: string | undefined) {
    if (!dateStr) return '無日期信息';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '無效日期';
    return date.toLocaleDateString();
  }
  
  // 檢查是否已報名
  async function checkEnrollment() {
    try {
      const res = await service.get(`/api/enrollment/check/${currentCourseId.value}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      
      if (typeof res.data === 'object') {
        isEnrolled.value = res.data.enrolled;
        enrollmentStatus.value = res.data.status || '';
      } else {
        isEnrolled.value = res.data;
        enrollmentStatus.value = '';
      }
    } catch (error) {
      console.error('檢查報名狀態失敗:', error);
    }
  }
  
  // 檢查報名資格
  async function checkEnrollmentEligibility() {
    try {
      checkingEnrollment.value = true;      
      canEnroll.value = true;
      enrollmentCheckMessage.value = '';
      
      return true;
    } catch (error: any) {
      const errorMsg = error.response?.data?.error || '檢查失敗';
      canEnroll.value = false;
      
      if (errorMsg.includes('時間衝突')) {
        enrollmentCheckMessage.value = '⏰ 時間衝突！該課程與您已報名或正在申請的課程時間重疊';
      } else if (errorMsg.includes('最多只能')) {
        enrollmentCheckMessage.value = '⚠️ 課程數量超限！您最多只能同時學習6個未完成的課程';
      } else if (errorMsg.includes('已提交過該課程的申請')) {
        enrollmentCheckMessage.value = '⚠️ 您已經申請過該課程，請勿重複提交';
      } else {
        enrollmentCheckMessage.value = errorMsg;
      }
      
      return false;
    } finally {
      checkingEnrollment.value = false;
    }
  }
  
  // 申請課程
  async function applyCourse() {
    try {
      await checkEnrollmentEligibility();
      
      if (!canEnroll.value) {
        ElMessage.error({
          message: enrollmentCheckMessage.value,
          duration: 5000
        });
        return;
      }
      
      await service.post(`/api/enrollment/${currentCourseId.value}`, null, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      
      ElMessage.success('報名請求已提交');
      isEnrolled.value = true;
      await checkEnrollment();
    } catch (error: any) {
      const errorMsg = error.response?.data?.error || '提交失敗';
      ElMessage.error(errorMsg);
    }
  }
  
  // 重置狀態
  function reset() {
    activeChapter.value = null;
    currentCourseId.value = '';
    currentProgress.value = 0;
    hasUnsavedProgress.value = false;
    loading.value = true;
    isEnrolled.value = false;
    enrollmentStatus.value = '';
    canEnroll.value = true;
    enrollmentCheckMessage.value = '';
    checkingEnrollment.value = false;
    currentPage.value = 1;
  }
  
  // 返回狀態和方法
  return {
    // 狀態
    activeChapter,
    currentCourseId,
    currentProgress,
    hasUnsavedProgress,
    loading,
    isEnrolled,
    enrollmentStatus,
    canEnroll,
    enrollmentCheckMessage,
    checkingEnrollment,
    currentPage,
    pageSize,
    
    // 計算屬性
    paginatedChapters,
    
    // 方法
    initialize,
    formatTime,
    getChapterTime,
    handlePageChange,
    startAutoSave,
    stopAutoSave,
    saveProgress,
    formatDate,
    checkEnrollment,
    checkEnrollmentEligibility,
    applyCourse,
    reset
  };
});