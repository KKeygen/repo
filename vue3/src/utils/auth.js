import Cookies from 'js-cookie'

const TOKEN_KEY = 'Admin-Token'
const USER_ID_KEY = 'userId'
const USER_NAME_KEY = 'userName'

export function getToken() {
  return Cookies.get(TOKEN_KEY)
}

export function setToken(token) {
  return Cookies.set(TOKEN_KEY, token, { sameSite: 'Strict' })
}

export function removeToken() {
  return Cookies.remove(TOKEN_KEY)
}

export function getUserId() {
  return Cookies.get(USER_ID_KEY)
}

export function setUserId(id) {
  return Cookies.set(USER_ID_KEY, id)
}

export function removeUserId() {
  return Cookies.remove(USER_ID_KEY)
}

export function getUserName() {
  return Cookies.get(USER_NAME_KEY)
}

export function setUserName(name) {
  return Cookies.set(USER_NAME_KEY, name)
}

export function removeUserName() {
  return Cookies.remove(USER_NAME_KEY)
}
