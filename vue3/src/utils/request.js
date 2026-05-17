import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import eventBus from '@/utils/eventBus'

const AUTH_EXPIRED_CODES = [10055, 516]
const LOGIN_PATH = '/login'
const REQUEST_ID_HEADER = 'X-Request-Id'
const CLIENT_TIME_HEADER = 'X-Client-Timestamp'
const CLIENT_ROUTE_HEADER = 'X-Client-Route'

let isRedirectingToLogin = false

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 30000,
  headers: import.meta.env.VITE_SIGN_FLAG == 1
    ? { 'Content-Type': 'application/json;charset=utf-8', 'no_verify': false }
    : { 'Content-Type': 'application/json;charset=utf-8', 'no_verify': true }
})

function isPlainObject(value) {
  return Object.prototype.toString.call(value) === '[object Object]'
}

function createRequestId() {
  if (window.crypto?.randomUUID) {
    return window.crypto.randomUUID()
  }

  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

function base64ToArrayBuffer(base64) {
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i)
  }
  return bytes.buffer
}

function arrayBufferToBase64(buffer) {
  const bytes = new Uint8Array(buffer)
  let binary = ''
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}

async function signBody(body) {
  if (import.meta.env.VITE_SIGN_FLAG !== '1' || !isPlainObject(body)) {
    return body
  }

  const code = import.meta.env.VITE_CODE
  const privateKeyB64 = import.meta.env.VITE_SIGN_SECRET_KEY
  const businessBody = JSON.stringify(body)

  const signParams = { code, businessBody }
  const sortedKeys = Object.keys(signParams).sort()
  const signContent = sortedKeys.map(k => k + '=' + signParams[k]).join('&')

  try {
    const encoder = new TextEncoder()

    // Use browser's native Web Crypto API (same algorithm as Java backend)
    const keyData = base64ToArrayBuffer(privateKeyB64)
    const cryptoKey = await crypto.subtle.importKey(
      'pkcs8',
      keyData,
      { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' },
      false,
      ['sign']
    )

    const signature = await crypto.subtle.sign(
      'RSASSA-PKCS1-v1_5',
      cryptoKey,
      encoder.encode(signContent)
    )

    return {
      code,
      businessBody,
      sign: arrayBufferToBase64(signature)
    }
  } catch (e) {
    console.error('signBody failed:', e)
    return body
  }
}

function redirectToLogin() {
  if (isRedirectingToLogin) return

  isRedirectingToLogin = true
  removeToken()
  eventBus.emit('toast', { message: '登录已过期，请重新登录', type: 'warning' })

  const redirect = window.location.pathname + window.location.search
  const nextUrl = redirect && redirect !== LOGIN_PATH
    ? `${LOGIN_PATH}?redirect=${encodeURIComponent(redirect)}`
    : LOGIN_PATH

  window.setTimeout(() => {
    window.location.href = nextUrl
    isRedirectingToLogin = false
  }, 800)
}

service.interceptors.request.use(
  async (config) => {
    const token = getToken()
    if (token) {
      config.headers['token'] = token
    }

    config.headers[REQUEST_ID_HEADER] = config.headers[REQUEST_ID_HEADER] || createRequestId()
    config.headers[CLIENT_TIME_HEADER] = new Date().toISOString()
    config.headers[CLIENT_ROUTE_HEADER] = window.location.pathname

    if (config.method?.toLowerCase() === 'post' && config.data) {
      config.data = await signBody(config.data)
    }

    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => {
    const url = response.config.url
    if ('/Dismai/user/user/logout' === url) {
      return response.data
    }

    const res = response.data

    if (AUTH_EXPIRED_CODES.includes(res?.code)) {
      redirectToLogin()
      return Promise.reject(new Error('登录已过期'))
    }

    return res
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      redirectToLogin()
      return Promise.reject(new Error('登录已过期'))
    }

    const msg = error.response?.data?.msg || error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export default service
