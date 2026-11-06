<template>
  <el-card>
    <el-empty description="套餐管理页面骨架，迭代二实现">
      <template #image>
        <el-icon :size="60" color="#909399"><Goods /></el-icon>
      </template>
      <p class="tip">规划功能：套餐分页、套餐菜品关联、起售停售、批量删除</p>
    </el-empty>
  </el-card>
</template>

<script setup>
import { Goods } from '@element-plus/icons-vue'
</script>

<style scoped>
.tip {
  color: #909399;
  font-size: 14px;
}
</style>
