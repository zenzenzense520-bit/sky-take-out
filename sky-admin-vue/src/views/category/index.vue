<template>
  <el-card>
    <el-empty description="分类管理页面骨架，迭代二实现">
      <template #image>
        <el-icon :size="60" color="#909399"><Menu /></el-icon>
      </template>
      <p class="tip">规划功能：分类分页、新增/编辑、删除关联校验、按类型查询</p>
    </el-empty>
  </el-card>
</template>

<script setup>
import { Menu } from '@element-plus/icons-vue'
</script>

<style scoped>
.tip {
  color: #909399;
  font-size: 14px;
}
</style>
