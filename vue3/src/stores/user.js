import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUserId, setUserId, removeUserId, getUserName, setUserName, removeUserName } from '@/utils/auth'
import { login as loginApi, logout as logoutApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userId: getUserId() || '',
    userName: getUserName() || ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(credentials) {
      const res = await loginApi(credentials)
      const { token, userId, userName } = res.data
      this.token = token
      this.userId = userId
      this.userName = userName || ''
      setToken(token)
      setUserId(userId)
      if (userName) setUserName(userName)
      return res
    },

    async logout() {
      try {
        await logoutApi({ userId: this.userId })
      } catch (e) {
        // ignore logout API errors
      }
      this.token = ''
      this.userId = ''
      this.userName = ''
      removeToken()
      removeUserId()
      removeUserName()
    },

    setTokenValue(token) {
      this.token = token
      setToken(token)
    },

    getUserIdValue() {
      return this.userId || getUserId()
    }
  }
})
