<template>
  <el-card>
    <el-empty description="菜品管理页面骨架，迭代二实现">
      <template #image>
        <el-icon :size="60" color="#909399"><Dish /></el-icon>
      </template>
      <p class="tip">规划功能：菜品分页、口味维护、起售停售、批量删除、图片上传</p>
    </el-empty>
  </el-card>
</template>

<script setup>
import { Dish } from '@element-plus/icons-vue'
</script>

<style scoped>
.tip {
  color: #909399;
  font-size: 14px;
}
</style>
