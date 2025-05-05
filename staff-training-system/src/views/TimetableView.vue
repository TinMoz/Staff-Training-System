<!-- TimetableView.vue -->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from '../utils/request'

interface TimetableItem {
  courseId: number
  courseTitle: string
  weekday: number
  startTime: string
  endTime: string
  chapterTitle: string
}

const timetableData = ref<TimetableItem[]>([])
const timeStart = 8 // 8:00 AM
const timeEnd = 23 
const timeSlotHeight = 20 // 每個時間格高度(px)
const timeInterval = 30   // 時間間隔(分鐘)




const timeSlots = computed(() => {
  const slots = []
  for (let hour = timeStart; hour < timeEnd; hour++) {
    // 添加兩個時間槽（0分和30分）
    slots.push({
      time: `${hour.toString().padStart(2, '0')}:00`,
      hour,
      minute: 0
    })
    slots.push({
      time: `${hour.toString().padStart(2, '0')}:30`,
      hour,
      minute: 30
    })
  }
  return slots
})


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
const parseTime = (time: string) => {
  const [hours, minutes] = time.split(':').map(Number)
  return { hours, minutes }
}
const courseStyle = (item: any) => {
  const startOffset = (item.startMinutes - timeStart * 60) / timeInterval * timeSlotHeight
  const height = (item.duration / timeInterval) * timeSlotHeight
  
  return {
    top: `${startOffset}px`,
    height: `${height}px`,
    lineHeight: `${height}px`
  }
}

const loadTimetable = async () => {
  try {
    const res = await axios.get('/api/progress/timetable', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
    timetableData.value = res.data
  } catch (error) {
    console.error('獲取課表失敗:', error)
  }
}

onMounted(() => {
  loadTimetable()
})
</script>

<template>
    <div class="page-container">
      <div class="timetable-view">
        <h1>課程時間表</h1>
        
        <div class="timetable-grid">
          <!-- 時間軸列 -->
          <div class="time-column">
            <div class="time-header"></div>
            <div 
                v-for="slot in timeSlots" 
                :key="slot.time"
                class="time-slot"
                :style="{ height: `${timeSlotHeight}px` }"
                >
                <!-- 顯示所有30分鐘間隔的時間 -->
                <span v-if="slot.minute === 0 || slot.minute === 30" class="time-label">
                    {{ slot.time }}
                </span>
            </div>
          </div>
  
          <!-- 星期列 -->
          <div 
            v-for="day in 7" 
            :key="day"
            class="day-column"
          >
            <div class="day-header">
              {{ ['週日','週一','週二','週三','週四','週五','週六','週日'][day] }}
            </div>
            <div class="day-content">
              <div 
                v-for="slot in timeSlots" 
                :key="slot.time"
                class="time-slot"
                :style="{ height: `${timeSlotHeight}px` }"
              ></div>
              
              <!-- 課程塊 -->
              <div
                v-for="item in formattedData.filter(d => d.weekday === day)"
                :key="item.courseId + '-' + item.chapterTitle"
                class="course-block"
                :style="courseStyle(item)"
              >
                <div class="course-content">
                  {{ item.courseTitle }}<br>
                  {{ item.chapterTitle }}<br>
                  {{ item.startTime }}-{{ item.endTime }}
                </div>
              </div>
            </div>
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
    </div>
  </template>

<style src="../styles/timetable_style.css"></style>