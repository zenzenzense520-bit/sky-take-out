import request from './request'

// 菜品相关接口
export function addDish(data) {
  return request({
    url: '/admin/dish',
    method: 'post',
    data
  })
}

export function getDishPage(params) {
  return request({
    url: '/admin/dish/page',
    method: 'get',
    params
  })
}

export function deleteDish(ids) {
  return request({
    url: '/admin/dish',
    method: 'delete',
    params: { ids: Array.isArray(ids) ? ids.join(',') : ids }
  })
}

export function getDishById(id) {
  return request({
    url: `/admin/dish/${id}`,
    method: 'get'
  })
}

export function updateDish(data) {
  return request({
    url: '/admin/dish',
    method: 'put',
    data
  })
}

export function enableOrDisableDish(params) {
  return request({
    url: `/admin/dish/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

export function getDishList(params) {
  return request({
    url: '/admin/dish/list',
    method: 'get',
    params
  })
}
