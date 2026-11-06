import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

// 路由表：登录页独立，业务页面挂载在 Layout 下
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/employee',
    children: [
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/employee/index.vue'),
        meta: { title: '员工管理' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('@/views/dish/index.vue'),
        meta: { title: '菜品管理' }
      },
      {
        path: 'setmeal',
        name: 'Setmeal',
        component: () => import('@/views/setmeal/index.vue'),
        meta: { title: '套餐管理' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '数据统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫：未登录一律跳转登录页
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
  } else if (!getToken()) {
    next('/login')
  } else {
    next()
  }
})

export default router
