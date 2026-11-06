import request from './request'

// 订单相关接口（管理端）
export function getOrderPage(params) {
  return request({
    url: '/admin/order/conditionSearch',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/admin/order/details/${id}`,
    method: 'get'
  })
}

export function confirmOrder(id) {
  return request({
    url: '/admin/order/confirm',
    method: 'put',
    data: { id }
  })
}

export function rejectOrder(data) {
  return request({
    url: '/admin/order/rejection',
    method: 'put',
    data
  })
}

export function cancelOrder(data) {
  return request({
    url: '/admin/order/cancel',
    method: 'put',
    data
  })
}

export function deliveryOrder(id) {
  return request({
    url: `/admin/order/delivery/${id}`,
    method: 'put'
  })
}

export function completeOrder(id) {
  return request({
    url: `/admin/order/complete/${id}`,
    method: 'put'
  })
}
