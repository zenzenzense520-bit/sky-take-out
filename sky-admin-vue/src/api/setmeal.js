import request from './request'

// 套餐相关接口
export function addSetmeal(data) {
  return request({
    url: '/admin/setmeal',
    method: 'post',
    data
  })
}

export function getSetmealPage(params) {
  return request({
    url: '/admin/setmeal/page',
    method: 'get',
    params
  })
}

export function deleteSetmeal(ids) {
  return request({
    url: '/admin/setmeal',
    method: 'delete',
    params: { ids }
  })
}

export function getSetmealById(id) {
  return request({
    url: `/admin/setmeal/${id}`,
    method: 'get'
  })
}

export function updateSetmeal(data) {
  return request({
    url: '/admin/setmeal',
    method: 'put',
    data
  })
}

export function enableOrDisableSetmeal(params) {
  return request({
    url: `/admin/setmeal/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}
