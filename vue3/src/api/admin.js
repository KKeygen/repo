import request from '@/utils/request'

// 节目管理
export function addProgram(data) {
  return request({ url: '/Dismai/program/program/add', method: 'post', data })
}
export function editProgram(data) {
  return request({ url: '/Dismai/program/program/edit', method: 'post', data })
}
export function invalidProgram(data) {
  return request({ url: '/Dismai/program/program/invalid', method: 'post', data })
}

// 座位管理
export function addSeat(data) {
  return request({ url: '/Dismai/program/seat/add', method: 'post', data })
}
export function batchAddSeats(data) {
  return request({ url: '/Dismai/program/seat/batch/add', method: 'post', data })
}

// 票档管理
export function addTicketCategory(data) {
  return request({ url: '/Dismai/program/ticket/category/add', method: 'post', data })
}
export function getTicketCategoryDetail(data) {
  return request({ url: '/Dismai/program/ticket/category/detail', method: 'post', data })
}
export function getTicketCategoriesByProgram(data) {
  return request({ url: '/Dismai/program/ticket/category/select/list/by/program', method: 'post', data })
}

// 分类管理
export function getAllCategories() {
  return request({ url: '/Dismai/program/program/category/select/all', method: 'post' })
}
export function batchSaveCategories(data) {
  return request({ url: '/Dismai/program/program/category/save/batch', method: 'post', data })
}

// 场次管理
export function addShowTime(data) {
  return request({ url: '/Dismai/program/program/show/time/add', method: 'post', data })
}

// 库存重置
export function resetProgram(data) {
  return request({ url: '/Dismai/program/program/reset/execute', method: 'post', data })
}
