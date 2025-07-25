import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  server: {
    proxy: {
      "/api": {
        target: "https://p01--stp--vnwzhrpwlmrg.code.run", // 後端URL
        changeOrigin: true,
      },
    },
  },
  plugins: [vue()],
})
