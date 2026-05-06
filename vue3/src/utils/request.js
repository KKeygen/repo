import axios from 'axios'
import { KJUR } from 'jsrsasign'
import { getToken, removeToken } from '@/utils/auth'
import eventBus from '@/utils/eventBus'

let isRedirectingToLogin = false

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

// RSA signing helper
function signBody(body) {
  const signFlag = import.meta.env.VITE_SIGN_FLAG
  if (signFlag !== '1') return body

  const code = import.meta.env.VITE_CODE
  const privateKey = import.meta.env.VITE_SIGN_SECRET_KEY
  const businessBody = JSON.stringify(body)
  const signContent = code + businessBody

  const sig = new KJUR.crypto.Signature({ alg: 'SHA256withRSA' })
  sig.init({ d: privateKey, isPrivate: true })
  sig.updateString(signContent)
  const sign = sig.sign()

  return {
    code,
    businessBody,
    sign
  }
}

// Request interceptor
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = token
    }

    if (config.method === 'post' && config.data) {
      config.data = signBody(config.data)
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data
    const code = res.code

    // Token expired — toast once + redirect
    if (code === 10055 || code === 516) {
      if (!isRedirectingToLogin) {
        isRedirectingToLogin = true
        removeToken()
        eventBus.emit('toast', { message: '登录已过期，请重新登录', type: 'warning' })
        setTimeout(() => {
          window.location.href = '/login'
          isRedirectingToLogin = false
        }, 800)
      }
      return Promise.reject(new Error('登录已过期'))
    }

    // Return the unwrapped data for views to handle
    return res
  },
  (error) => {
    const msg = error.response?.data?.msg || error.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export default service
