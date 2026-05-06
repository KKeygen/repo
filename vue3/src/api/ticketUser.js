import request from '@/utils/request'

export function getTicketUserList(data) {
  return request({ url: '/Dismai/user/ticket/user/list', method: 'post', data })
}

export function addTicketUser(data) {
  return request({ url: '/Dismai/user/ticket/user/add', method: 'post', data })
}

export function deleteTicketUser(data) {
  return request({ url: '/Dismai/user/ticket/user/delete', method: 'post', data })
}
