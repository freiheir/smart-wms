import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import basicSsl from '@vitejs/plugin-basic-ssl'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    basicSsl()
  ],
  server: {
    port: 5181,
    host: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:5182',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
