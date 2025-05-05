import './styles/main.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 中文语言包

import router from './router/index'
import { useUserStore } from './stores/userStore'

const app = createApp(App)


app.use(createPinia())
app.use(ElementPlus, { locale: zhCn }) // 使用中文语言包
app.use(router)
app.mount('#app')

const userStore = useUserStore();
userStore.initialize();
