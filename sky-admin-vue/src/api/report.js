import request from './request'

// 数据统计相关接口（管理端）

// 营业额统计
export function getTurnoverStatistics(params) {
  return request({
    url: '/admin/report/turnoverStatistics',
    method: 'get',
    params
  })
}

// 用户统计
export function getUserStatistics(params) {
  return request({
    url: '/admin/report/userStatistics',
    method: 'get',
    params
  })
}

// 订单统计
export function getOrderReportStatistics(params) {
  return request({
    url: '/admin/report/ordersStatistics',
    method: 'get',
    params
  })
}

// 销量排名 Top10
export function getSalesTop10(params) {
  return request({
    url: '/admin/report/top10',
    method: 'get',
    params
  })
}

// 导出 30 天运营数据报表
export function exportReport() {
  return request({
    url: '/admin/report/export',
    method: 'get',
    responseType: 'blob'
  })
}
