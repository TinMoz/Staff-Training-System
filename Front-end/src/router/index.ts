//index.ts
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../stores/userStore'

//導入各個頁面組件
const App = () => import('../App.vue')
const LoginView = () => import('../views/LoginView.vue')
const CourseList = () => import('../views/CourseList.vue')
const CourseDetail = () => import('../views/CourseDetail.vue')
const AdminView = () => import('../views/AdminView.vue')
const HomeView = () => import('../views/HomeView.vue')
const RegisterView = () => import('../views/RegisterView.vue')
const UserCourseView = () => import('../views/UserCourseView.vue')
const UserTimetableView = () => import('../views/TimetableView.vue')
//路由各個頁面位置
const routes: Array<RouteRecordRaw> = [
  //登入頁面
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  //課程列表頁面
  {
    path: '/courses',
    name: 'CourseList',
    component: CourseList,
    meta: { requiresAuth: false }
  },
  //課程詳情頁面
  {
    path: '/courses/:id',
    name: 'CourseDetail',
    component: CourseDetail,
    meta: { requiresAuth: false },
    props: true
  },
  //管理員頁面
  {
    path: '/admin',
    name: 'Admin',
    component: AdminView,
    meta: { requiresAuth: false }
  },
  //首頁頁面
  {
    path: '/home',
    name: 'Home',
    component: HomeView,
    meta: { requiresAuth: false }
  },
  //引導首頁
  {
    path: '/',
    component: App,
    redirect: '/home',
    children: []
  },
  //註冊頁面
  {
    path: '/register',
    name: 'Register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  //用戶課程頁面
  {
    path: '/user-courses',
    name: 'UserCourses',
    component: UserCourseView,
    meta: { requiresAuth: true, requiresUser: true } 
  },
  //用戶時間表頁面
  {
    path: '/user-timetable',
    name: 'UserTimetable',
    component: UserTimetableView,
    meta: { requiresAuth: true, requiresUser: true }
  }
];

//創建路由實例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守衛
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore();
  await userStore.initialize();
  const publicPaths = ["/login", "/courses", "/courses/:id"];
  if (publicPaths.includes(to.path)) {
      next();
      return;
  }
    
  // 检查是否需要身份验证
  if (to.meta.requiresAuth) {
    if (!userStore.isAuthenticated) {
      next('/login');
    } else if (to.meta.requiresAdmin && userStore.role !== 'ADMIN') {
      next('/courses');
    } else {
      next();
    }
  } else {
    next(); // 公開頁面放行
  }
});
export default router