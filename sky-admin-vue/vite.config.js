import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// Vite 配置：开发服务器代理后端接口，解决跨域
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 80,
    host: true,
    proxy: {
      // 管理端接口
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 用户端接口
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
