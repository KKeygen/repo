import axios from 'axios'
import { KJUR } from 'jsrsasign'
import { getToken, removeToken } from '@/utils/auth'
import eventBus from '@/utils/eventBus'

const AUTH_EXPIRED_CODES = [10055, 516]
const LOGIN_PATH = '/login'
const REQUEST_ID_HEADER = 'X-Request-Id'
const CLIENT_TIME_HEADER = 'X-Client-Timestamp'
const CLIENT_ROUTE_HEADER = 'X-Client-Route'
const NO_VERIFY_HEADER = 'no_verify'
const SIGN_ENABLED = import.meta.env.VITE_SIGN_FLAG === '1'
const NUMERIC_CODE_PATTERN = /^-?\d+$/

let isRedirectingToLogin = false

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
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

function normalizeResponse(res) {
  if (!res || typeof res !== 'object') {
    return res
  }

  if (typeof res.code === 'string' && NUMERIC_CODE_PATTERN.test(res.code)) {
    res.code = Number(res.code)
  }
  if (res.msg === undefined && res.message !== undefined) {
    res.msg = res.message
  }

  return res
}

function signBody(body) {
  if (!SIGN_ENABLED || !isPlainObject(body)) {
    return body
  }

  const code = import.meta.env.VITE_CODE
  const privateKey = import.meta.env.VITE_SIGN_SECRET_KEY
  const businessBody = JSON.stringify(body)
  const signContent = code + businessBody

  const sig = new KJUR.crypto.Signature({ alg: 'SHA256withRSA' })
  sig.init({ d: privateKey, isPrivate: true })
  sig.updateString(signContent)

  return {
    code,
    businessBody,
    sign: sig.sign()
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
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }

    config.headers[REQUEST_ID_HEADER] = config.headers[REQUEST_ID_HEADER] || createRequestId()
    config.headers[CLIENT_TIME_HEADER] = new Date().toISOString()
    config.headers[CLIENT_ROUTE_HEADER] = window.location.pathname
    if (SIGN_ENABLED) {
      delete config.headers[NO_VERIFY_HEADER]
    } else {
      config.headers[NO_VERIFY_HEADER] = 'true'
    }

    if (config.method?.toLowerCase() === 'post' && config.data) {
      config.data = signBody(config.data)
    }

    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => {
    const res = normalizeResponse(response.data)

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
