import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '@/api/employee'
import { getToken, setToken, removeToken } from '@/utils/auth'

// 用户状态：令牌与登录信息
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    name: ''
  }),
  actions: {
    // 登录并持久化令牌
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token
      this.name = res.data.name
      setToken(res.data.token)
    },
    // 退出并清理本地状态
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.token = ''
        this.name = ''
        removeToken()
      }
    }
  }
})
