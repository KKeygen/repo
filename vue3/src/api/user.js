import request from '@/utils/request'

export function login(data) {
  return request({ url: '/Dismai/user/user/login', method: 'post', data })
}

export function logout(data) {
  return request({ url: '/Dismai/user/user/logout', method: 'post', data })
}

export function register(data) {
  return request({ url: '/Dismai/user/user/register', method: 'post', data })
}

export function checkCaptchaNeed(data) {
  return request({ url: '/Dismai/user/user/captcha/check/need', method: 'post', data })
}

export function getUserInfo(data) {
  return request({ url: '/Dismai/user/user/get/id', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/Dismai/user/user/update', method: 'post', data })
}

export function updatePassword(data) {
  return request({ url: '/Dismai/user/user/update/password', method: 'post', data })
}

export function updateEmail(data) {
  return request({ url: '/Dismai/user/user/update/email', method: 'post', data })
}

export function updateMobile(data) {
  return request({ url: '/Dismai/user/user/update/mobile', method: 'post', data })
}

export function authenticate(data) {
  return request({ url: '/Dismai/user/user/authentication', method: 'post', data })
}
