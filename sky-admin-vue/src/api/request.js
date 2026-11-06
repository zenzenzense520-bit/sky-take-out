import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'

// 创建 axios 实例，请求经 Vite 代理转发到 8080
const request = axios.create({
  baseURL: '/',
  timeout: 10000
})

// 请求拦截器：自动携带 JWT（请求头名称与后端 admin-token-name 一致）
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.token = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理后端 Result 结构
request.interceptors.response.use(
  (response) => {
    // 若为文件流响应（例如导出 Excel），直接返回二进制 Blob 数据
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response.data
    }
    const res = response.data
    if (res.code === 1) {
      return res
    }
    // 业务失败
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || 'Error'))
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      removeToken()
      router.push('/login')
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
