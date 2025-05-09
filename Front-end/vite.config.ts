import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "https://staff-training-system-2-744605943289.us-central1.run.app", // 後端URL
        changeOrigin: true,
      },
    },
  },
  plugins: [vue()],
})
