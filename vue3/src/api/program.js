import request from '@/utils/request'

export function getCategoryTypes(data) {
  return request({ url: '/Dismai/program/program/category/selectByType', method: 'post', data })
}

export function getHomeList(data) {
  return request({ url: '/Dismai/program/program/home/list', method: 'post', data })
}

export function getProgramDetail(data) {
  return request({ url: '/Dismai/program/program/detail', method: 'post', data })
}

export function getCategoryByParent(data) {
  return request({ url: '/Dismai/program/program/category/selectByParentProgramCategoryId', method: 'post', data })
}

export function getProgramPage(data) {
  return request({ url: '/Dismai/program/program/page', method: 'post', data })
}

export function searchPrograms(data) {
  return request({ url: '/Dismai/program/program/search', method: 'post', data })
}

export function getRecommendList(data) {
  return request({ url: '/Dismai/program/program/recommend/list', method: 'post', data })
}

export function getSeatInfo(data) {
  return request({ url: '/Dismai/program/seat/relate/info', method: 'post', data })
}
