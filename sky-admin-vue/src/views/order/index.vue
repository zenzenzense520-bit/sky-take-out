<template>
  <el-card>
    <el-empty description="订单管理页面骨架，迭代三实现">
      <template #image>
        <el-icon :size="60" color="#909399"><List /></el-icon>
      </template>
      <p class="tip">规划功能：订单分页、接单/拒单、派送、完成、催单与详情查看</p>
    </el-empty>
  </el-card>
</template>

<script setup>
import { List } from '@element-plus/icons-vue'
</script>

<style scoped>
.tip {
  color: #909399;
  font-size: 14px;
}
</style>
