import request from '@/utils/request'

export function getCaptcha(data) {
  return request({ url: '/Dismai/user/user/captcha/get', method: 'post', data })
}

export function verifyCaptcha(data) {
  return request({ url: '/Dismai/user/user/captcha/verify', method: 'post', data })
}
