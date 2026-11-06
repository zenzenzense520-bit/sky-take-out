import request from './request'

// 分类相关接口
export function addCategory(data) {
  return request({
    url: '/admin/category',
    method: 'post',
    data
  })
}

export function getCategoryPage(params) {
  return request({
    url: '/admin/category/page',
    method: 'get',
    params
  })
}

export function deleteCategory(id) {
  return request({
    url: '/admin/category',
    method: 'delete',
    params: { id }
  })
}

export function updateCategory(data) {
  return request({
    url: '/admin/category',
    method: 'put',
    data
  })
}

export function enableOrDisableCategory(params) {
  return request({
    url: `/admin/category/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

export function getCategoryList(params) {
  return request({
    url: '/admin/category/list',
    method: 'get',
    params
  })
}
