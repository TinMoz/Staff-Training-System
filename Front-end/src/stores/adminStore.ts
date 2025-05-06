//adminStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import axios from '../utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useCourseStore } from './courseStore';

// 定義課程和請求的接口
interface Course {
  id: string;
  title: string;
  courseCode: string;
  description: string;
  credits: number;
  teacher?: string;
  chapters: { id: string; title: string; content: string; duration: number; }[];
}

interface PendingRequest {
  id: number;
  user: { username: string };
  course: { title: string };
  username: string;
  courseTitle: string;
}
// 輸出AdminStore到Vue組件
// 這個store主要用於管理課程和請求的狀態和行為
export const useAdminStore = defineStore('admin', () => {
  const courseStore = useCourseStore();
  const loading = ref(true);
  const currentCourse = ref<Course>({
    id: '',
    title: '',
    courseCode: '',
    description: '',
    credits: 1,
    teacher: '',
    chapters: []
  });
  const pendingRequests = ref<PendingRequest[]>([]);
  
  // 分頁相關
  const currentPage = ref(1);
  const pageSize = ref(5);
  const currentRequestPage = ref(1);
  const requestPageSize = ref(4);
  
  // 計算屬性：分頁
  const paginatedRequests = computed(() => {
    const start = (currentRequestPage.value - 1) * requestPageSize.value;
    const end = start + requestPageSize.value;
    return pendingRequests.value.slice(start, end);
  });
  
  // 編輯菜單定義
  const formRules = {
    title: [{ required: true, message: '請輸入課程名稱', trigger: 'blur' }],
    courseCode: [{ required: true, message: '請輸入課程代碼', trigger: 'blur' }],
    description: [{ required: true, message: '請輸入課程描述', trigger: 'blur' }],
    credits: [{ required: true, message: '請選擇課程學分', trigger: 'change' }],
    teacher: [{ required: true, message: '請輸入指導老師', trigger: 'blur' }],
    chapters: [
    { 
        validator: (_rule: any, value: any, callback: any) => {
            if (!value || value.length === 0) {
                callback(new Error('至少需要添加一個章節'));
                return;
            }
            const invalid = value.some((ch: any) => 
                !ch.duration || 
                ch.duration < 120 || 
                isNaN(ch.duration)
            );
            invalid ? callback(new Error('章節時長不能小於120分鐘且必須填寫')) : callback();
        },
        trigger: 'blur'
    }
    ]
  };
  
  // 初始化方法
  async function initialize() {
    try {
      await courseStore.fetchCourses();
      await fetchPendingRequests();
    } catch (error) {
      ElMessage.error('載入失敗');
    } finally {
      loading.value = false;
    }
  }
  
  // 清空當前課程
  function resetCurrentCourse() {
    currentCourse.value = {
      id: '',
      title: '',
      courseCode: '',
      description: '',
      credits: 1,
      teacher: '',
      chapters: []
    };
  }
  
  // 添加章節
  function addChapter() {
    currentCourse.value.chapters.push({
      id: Date.now().toString(),
      title: '',
      content: '',
      duration: 120
    });
  }
  
  // 刪除章節
  function removeChapter(index: number) {
    currentCourse.value.chapters.splice(index, 1);
  }
  
  // 保存課程
  async function saveCourse() {
    try {
      const chaptersWithOrder = currentCourse.value.chapters.map((ch, index) => ({
        ...ch,
        orderNum: index + 1,
        duration: Number(ch.duration) || 120,
        title: String(ch.title).trim(),
        content: String(ch.content).trim()
      }));
      
      if (currentCourse.value.id) {
        await courseStore.updateCourse({
          ...currentCourse.value,
          teacher: currentCourse.value.teacher || '',
          chapters: chaptersWithOrder
        });
      } else {
        await courseStore.createCourse({
          ...currentCourse.value,
          teacher: currentCourse.value.teacher || '',
          chapters: chaptersWithOrder
        });
      }
      
      ElMessage.success('操作成功');
      return true;
    } catch (error) {
      ElMessage.error('操作失敗');
      return false;
    }
  }
  
  // 編輯課程
  function editCourse(course: Course) {
    currentCourse.value = {
      ...course,
      chapters: course.chapters.map(ch => ({
        ...ch,
        duration: ch.duration ?? 120
      }))
    };
  }
  
  // 刪除課程
  async function deleteCourse(id: string) {
    try {
      await ElMessageBox.confirm('確認刪除？', '警告');
      await courseStore.deleteCourse(id);
      ElMessage.success('刪除成功');
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('刪除失敗');
      }
    }
  }
  
  // 獲取待處理請求
  async function fetchPendingRequests() {
    try {
      const res = await axios.get('/api/enrollment/pending');
      pendingRequests.value = res.data;
    } catch (error) {
      console.error('獲取待處理請求失敗:', error);
    }
  }
  
  // 處理請求
  async function processRequest(id: number, status: string) {
    try {
      await axios.put(`/api/enrollment/${id}/${status}`);
      pendingRequests.value = pendingRequests.value.filter(r => r.id !== id);
      ElMessage.success('操作成功');
    } catch (error) {
      ElMessage.error('操作失敗');
    }
  }
  
  // 處理頁碼變化
  function handlePageChange(newPage: number) {
    currentPage.value = newPage;
  }
  
  // 處理請求頁碼變化
  function handleRequestPageChange(newPage: number) {
    currentRequestPage.value = newPage;
  }
  
  // 返回store的狀態和方法
  return {
    // 狀態
    loading,
    currentCourse,
    pendingRequests,
    currentPage,
    pageSize,
    currentRequestPage,
    requestPageSize,
    formRules,
    
    // 計算屬性
    paginatedRequests,
    
    // 方法
    initialize,
    resetCurrentCourse,
    addChapter,
    removeChapter,
    saveCourse,
    editCourse,
    deleteCourse,
    fetchPendingRequests,
    processRequest,
    handlePageChange,
    handleRequestPageChange
  };
});