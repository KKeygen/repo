import { defineStore } from 'pinia'
import {
  getToken,
  setToken,
  removeToken,
  getUserId,
  setUserId,
  removeUserId,
  getUserName,
  setUserName,
  removeUserName,
  getUserMobile,
  setUserMobile,
  removeUserMobile
} from '@/utils/auth'
import { login as loginApi, logout as logoutApi } from '@/api/user'
import { ADMIN_USER_IDS as DEFAULT_ADMIN_USER_IDS } from '@/constants/site'

const envAdminIds = (import.meta.env.VITE_ADMIN_USER_IDS || '').split(',').map(s => s.trim()).filter(Boolean)
const adminIds = Array.from(new Set([...envAdminIds, ...DEFAULT_ADMIN_USER_IDS]))

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userId: getUserId() || '',
    userName: getUserName() || '',
    mobile: getUserMobile() || ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => adminIds.length > 0 && adminIds.includes(String(state.mobile || state.userId))
  },

  actions: {
    async login(credentials) {
      const res = await loginApi(credentials)
      const { token, userId, userName } = res.data
      this.token = token
      this.userId = userId
      this.userName = userName || ''
      this.mobile = credentials.mobile || ''
      setToken(token)
      setUserId(userId)
      if (userName) setUserName(userName)
      if (credentials.mobile) setUserMobile(credentials.mobile)
      return res
    },

    async logout() {
      try {
        if (this.token) {
          await logoutApi({ code: '0001', token: this.token })
        }
      } catch (e) {
        // ignore logout API errors
      }
      this.token = ''
      this.userId = ''
      this.userName = ''
      this.mobile = ''
      removeToken()
      removeUserId()
      removeUserName()
      removeUserMobile()
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
