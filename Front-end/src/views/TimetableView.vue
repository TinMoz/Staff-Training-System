<!--TimetableView.vue-->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/request'

//定義時間表結構
interface TimetableItem {
  courseId: number
  courseTitle: string
  weekday: number
  startTime: string
  endTime: string
  chapterTitle: string
}

//時間表基本配置
const timetableData = ref<TimetableItem[]>([])
const timeStart = 8 // 8:00 AM 開始時間
const timeEnd = 23 // 23:00 PM 結束時間
const timeSlotHeight = 20 // 每個時間格高度(px)
const timeInterval = 30   // 時間間隔(分鐘)

//生成時間槽數組
const timeSlots = computed(() => {
  const slots = []
  for (let hour = timeStart; hour < timeEnd; hour++) {
    //添加整點時間槽
    slots.push({
      time: `${hour.toString().padStart(2, '0')}:00`,
      hour,
      minute: 0
    })
    //添加半點時間槽
    slots.push({
      time: `${hour.toString().padStart(2, '0')}:30`,
      hour,
      minute: 30
    })
  }
  return slots
})

//處理時間表數據格式
const formattedData = computed(() => {
  return timetableData.value.map(item => {
    const start = parseTime(item.startTime)
    const end = parseTime(item.endTime)
    
    return {
      ...item,
      startMinutes: start.hours * 60 + start.minutes,
      endMinutes: end.hours * 60 + end.minutes,
      duration: (end.hours * 60 + end.minutes) - (start.hours * 60 + start.minutes)
    }
  })
})

//解析時間字符串為小時和分鐘
const parseTime = (time: string) => {
  const timeParts = time.split(':');
  const hours = parseInt(timeParts[0]);
  const minutes = parseInt(timeParts[1]);
  return { hours, minutes }
}

const formatTime = (time: string): string => {
  // 如果時間包含秒數，只保留小時和分鐘
  if (time && time.includes(':')) {
    const parts = time.split(':');
    return `${parts[0]}:${parts[1]}`;
  }
  return time;
}

//計算課程塊樣式
const courseStyle = (item: any) => {
  const startOffset = (item.startMinutes - timeStart * 60) / timeInterval * timeSlotHeight
  const height = (item.duration / timeInterval) * timeSlotHeight
  
  return {
    top: `${startOffset}px`,
    height: `${height}px`,
    lineHeight: `${height}px`
  }
}

//從API加載時間表
const loadTimetable = async () => {
  try {
    const res = await axios.get('/api/progress/timetable', { // 透過axios獲取課表的api
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
    timetableData.value = res.data
  } catch (error) {
    console.error('獲取課表失敗:', error)
  }
}

//組件掛載時加載數據
onMounted(() => {
  loadTimetable()
})
</script>

<template>
    <div class="page-container">
      <!--時間表視圖容器-->
      <div class="timetable-view">
        <h1>課程時間表</h1>
        
        <!--時間表網格-->
        <div class="timetable-grid">
          <!--時間軸列-->
          <div class="time-column">
            <div class="time-header"></div>
            <div 
                v-for="slot in timeSlots" 
                :key="slot.time"
                class="time-slot"
                :style="{ height: `${timeSlotHeight}px` }"
                >
                <!--顯示時間標籤-->
                <span v-if="slot.minute === 0 || slot.minute === 30" class="time-label">
                    {{ slot.time }}
                </span>
            </div>
          </div>
  
          <!--星期列-->
          <div 
            v-for="day in 7" 
            :key="day"
            class="day-column"
          >
            <!--星期標題-->
            <div class="day-header">
              {{ ['週日','週一','週二','週三','週四','週五','週六','週日'][day] }}
            </div>
            <div class="day-content">
              <!--時間槽背景-->
              <div 
                v-for="slot in timeSlots" 
                :key="slot.time"
                class="time-slot"
                :style="{ height: `${timeSlotHeight}px` }"
              ></div>
              
              <!--課程塊-->
              <div
                v-for="item in formattedData.filter(d => d.weekday === day)"
                :key="item.courseId + '-' + item.chapterTitle"
                class="course-block"
                :style="courseStyle(item)"
              >
                <!--課程內容-->
                <div class="course-content">
                  {{ item.courseTitle }}<br>
                  {{ item.chapterTitle }}<br>
                  {{ formatTime(item.startTime) }}-{{ formatTime(item.endTime) }}
                </div>
              </div>
            </div>
          </div>
        </div>
  
        <!--返回首頁按鈕-->
        <el-button 
          type="primary" 
          @click="$router.push('/home')"
          style="margin-top: 20px;"
        >
          返回首頁
        </el-button>
      </div>
    </div>
  </template>

<style src="../styles/timetable_style.css"></style>