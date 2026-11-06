<template>
  <el-container class="layout-container">
    <!-- 侧边导航 -->
    <el-aside width="210px" class="layout-aside">
      <div class="logo">苍穹外卖后台</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/employee">
          <el-icon><User /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><Dish /></el-icon>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/setmeal">
          <el-icon><Goods /></el-icon>
          <span>套餐管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <span class="page-title">{{ $route.meta.title }}</span>
        <el-button type="danger" plain size="small" @click="handleLogout">
          退出登录
        </el-button>
      </el-header>

      <!-- 主内容区 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 退出登录：二次确认后清理状态并跳转
async function handleLogout() {
  await ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100%;
}

.layout-aside {
  background-color: #001529;

  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
  }

  .el-menu {
    border-right: none;
  }
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;

  .page-title {
    font-size: 16px;
    font-weight: 600;
  }
}
</style>
