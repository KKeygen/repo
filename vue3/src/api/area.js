import request from '@/utils/request'

export function getCurrentCity() {
  return request({ url: '/Dismai/basedata/area/current', method: 'post' })
}

export function getHotCities() {
  return request({ url: '/Dismai/basedata/area/hot', method: 'post' })
}

export function getAllCities() {
  return request({ url: '/Dismai/basedata/area/selectCityData', method: 'post' })
}

export function getCityById(data) {
  return request({ url: '/Dismai/basedata/area/getById', method: 'post', data })
}
