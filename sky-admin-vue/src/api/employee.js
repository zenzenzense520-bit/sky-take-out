import request from './request'

// 员工相关接口，路径与后端 EmployeeController 对齐
export function login(data) {
  return request({
    url: '/admin/employee/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/admin/employee/logout',
    method: 'post'
  })
}

export function getEmployeePage(params) {
  return request({
    url: '/admin/employee/page',
    method: 'get',
    params
  })
}

export function addEmployee(data) {
  return request({
    url: '/admin/employee',
    method: 'post',
    data
  })
}

export function enableOrDisableEmployee(params) {
  return request({
    url: `/admin/employee/status/${params.status}`,
    method: 'post',
    params: { id: params.id }
  })
}

export function getEmployeeById(id) {
  return request({
    url: `/admin/employee/${id}`,
    method: 'get'
  })
}

export function updateEmployee(data) {
  return request({
    url: '/admin/employee',
    method: 'put',
    data
  })
}
