import request from '@/utils/request'

export function getCurrentCity() {
  return request({ url: '/Dismai/basedata/area/current', method: 'post' })
}

export function getHotCities(data) {
  return request({ url: '/Dismai/basedata/area/hot', method: 'post', data })
}

export function getAllCities(data) {
  return request({ url: '/Dismai/basedata/area/selectCityData', method: 'post', data })
}

export function getCityById(data) {
  return request({ url: '/Dismai/basedata/area/getById', method: 'post', data })
}
