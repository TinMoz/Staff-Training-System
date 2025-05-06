//courseStore.ts
import {defineStore} from 'pinia';
import axios from 'axios';
//定義課程的接口
interface Course {
    id: string;
    title: string;
    courseCode: string;
    description: string;
    credits: number;
    teacher: string; 
    chapters: { id: string; title: string; content: string;  duration: number;}[];
}

// 輸出 CourseStore 到 Vue 組件
// 定義課程的store
export const useCourseStore = defineStore('course', {
    // 定義目前Course狀態
    state: () => ({
        courses: [] as Course[],
        detail: null as {
            title: string;
            courseCode: string;
            credits: number;         
            description: string;    
            createdAt: String;      
            teacher: String;
            chapters: { id: string; title: string; content: string; duration: number; }[];
        } | null,
        lastFetch: 0, // 添加 lastFetch 屬性以追蹤上次獲取時間
    }),
    getters: {
        adminCourses: (state) => state.courses, // 更新此 getter 以返回課程
      },
    actions: {
        // 取得課程
        async fetchCourses(force: boolean = false) {
            // 修正緩存判斷邏輯
            if (!force && Date.now() - this.lastFetch < 10_000) return
            
            try {
              const res = await axios.get('/api/courses') // res 利用 axios 發送請求獲得後端發送的courses裡面的內容
              this.courses = res.data;
              this.lastFetch = Date.now() // 記錄成功請求時間
            } catch (err) {
              console.error('載入失敗:', err)
              throw err
            }
        },
        // 刪除課程
        async deleteCourse(id: string) {
          try {
            await axios.delete(`/api/admin/courses/${id}`, { // 利用 axios 發送請求刪除課程，用id指定具體課程
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}` // 發送API請求時帶上用戶token
              }
            });
            this.courses = this.courses.filter((c) => c.id !== id); // 更新課程列表，過濾掉已刪除的課程
          } catch (error) {
            throw new Error('刪除課程失敗');
          } 
        },
        // 更新課程
        async updateCourse(course: Course) {
          try {
            const chaptersWithOrder = course.chapters.map((ch, index) => ({
              ...ch,
              orderNum: index + 1
            }));
        
            const response = await axios.put(`/api/courses/${course.id}`, {// 利用 axios 發送請求更新課程，用id指定具體課程
              id: course.id,
              title: course.title,
              courseCode: course.courseCode,
              description: course.description,
              credits: course.credits,
              teacher: course.teacher,
              chapters: chaptersWithOrder
            }, {
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`// 發送API請求時帶上用戶token
              }
            });
        
            const index = this.courses.findIndex(c => c.id === course.id); // 在 courses 中找到要更新的課程索引
            if (index !== -1) {                                            // 如果找到，則更新課程數據
              this.courses[index] = response.data;            
            }
          } catch (error) {
            throw new Error('更新失敗');
          }
        },
        // 創建課程
        async createCourse(course: Course) {
          const chaptersWithOrder = course.chapters.map((ch, index) => ({ // 將章節數據映射到新的格式，並添加 orderNum 屬性
            title: ch.title,
            content: ch.content,
            duration: ch.duration,
            orderNum: index + 1 
          }));
        
          const response = await axios.post('/api/courses', { // 利用 axios 發送請求創建課程
            title: course.title,
            courseCode: course.courseCode,
            description: course.description,
            credits: course.credits,
            teacher: course.teacher,
            chapters: chaptersWithOrder // 使用處理後的章節數據
          }, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`, // 發送API請求時帶上用戶token
              'Content-Type': 'application/json'
            }
          });
          
          this.courses.push(response.data); // 將新創建的課程添加到 courses 陣列中
        },
        // 獲取課程詳情
        async fetchCourseDetail(courseId: string) {
            try {
                console.log('請求路徑:', `/api/courses/${courseId}`); 
                const res = await axios.get(`/api/courses/${courseId}`);// 利用 axios 發送請求獲得課程詳情
                console.log('響應數據:', res.data);
                this.detail = res.data; // 將響應數據賦值給 detail
            } catch (err) {
                console.error('請求詳情:', (err as any).config);
                throw err;
            }
        },
        
    }
});