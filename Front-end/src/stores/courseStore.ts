//courseStore.ts
import {defineStore} from 'pinia';
import axios from 'axios';

interface Course {
    id: string;
    title: string;
    courseCode: string;
    description: string;
    credits: number;
    teacher: string; 
    chapters: { id: string; title: string; content: string;  duration: number;}[];
}


export const useCourseStore = defineStore('course', {
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
        async fetchCourses(force: boolean = false) {
            // 修正緩存判斷邏輯
            if (!force && Date.now() - this.lastFetch < 10_000) return
            
            try {
              const res = await axios.get('/api/courses')
              this.courses = res.data;
              this.lastFetch = Date.now() // 記錄成功請求時間
            } catch (err) {
              console.error('載入失敗:', err)
              throw err
            }
        },
        async deleteCourse(id: string) {
          try {
            await axios.delete(`/api/admin/courses/${id}`, { // 確保路徑正確
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
              }
            });
            this.courses = this.courses.filter((c) => c.id !== id);
          } catch (error) {
            throw new Error('刪除課程失敗');
          } 
        },
        
        async updateCourse(course: Course) {
          try {
            const chaptersWithOrder = course.chapters.map((ch, index) => ({
              ...ch,
              orderNum: index + 1
            }));
        
            const response = await axios.put(`/api/courses/${course.id}`, {
              id: course.id,
              title: course.title,
              courseCode: course.courseCode,
              description: course.description,
              credits: course.credits,
              teacher: course.teacher, // 明確指定 teacher 欄位
              chapters: chaptersWithOrder
            }, {
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
              }
            });
        
            const index = this.courses.findIndex(c => c.id === course.id);
            if (index !== -1) {
              this.courses[index] = response.data;
            }
          } catch (error) {
            throw new Error('更新失敗');
          }
        },
        async createCourse(course: Course) {
          const chaptersWithOrder = course.chapters.map((ch, index) => ({
            title: ch.title,
            content: ch.content,
            duration: ch.duration,
            orderNum: index + 1 
          }));
        
          const response = await axios.post('/api/courses', {
            title: course.title,
            courseCode: course.courseCode,
            description: course.description,
            credits: course.credits,
            teacher: course.teacher, // 明確指定 teacher 欄位
            chapters: chaptersWithOrder // 使用處理後的章節數據
          }, {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'
            }
          });
          
          this.courses.push(response.data);
        },
        
        async fetchCourseDetail(courseId: string) {
            try {
                console.log('請求路徑:', `/api/courses/${courseId}`);
                const res = await axios.get(`/api/courses/${courseId}`);
                console.log('響應數據:', res.data);
                this.detail = res.data;
            } catch (err) {
                console.error('請求詳情:', (err as any).config);
                throw err;
            }
        },
        
    }
});