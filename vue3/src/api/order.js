import request from '@/utils/request'

export function createOrderV1(data) {
  return request({ url: '/Dismai/program/program/order/create/v1', method: 'post', data })
}

export function createOrderV2(data) {
  return request({ url: '/Dismai/program/program/order/create/v2', method: 'post', data })
}

export function createOrderV3(data) {
  return request({ url: '/Dismai/program/program/order/create/v3', method: 'post', data })
}

export function createOrderV4(data) {
  return request({ url: '/Dismai/program/program/order/create/v4', method: 'post', data })
}

export function getOrderList(data) {
  return request({ url: '/Dismai/order/order/select/list', method: 'post', data })
}

export function getOrderDetail(data) {
  return request({ url: '/Dismai/order/order/get', method: 'post', data })
}

export function getOrderCache(data) {
  return request({ url: '/Dismai/order/order/get/cache', method: 'post', data })
}

export function cancelOrder(data) {
  return request({ url: '/Dismai/order/order/cancel', method: 'post', data })
}

export function payOrder(data) {
  return request({ url: '/Dismai/order/order/pay', method: 'post', data })
}

export function checkPayStatus(data) {
  return request({ url: '/Dismai/order/order/pay/check', method: 'post', data })
}
