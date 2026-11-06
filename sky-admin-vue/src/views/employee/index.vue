<template>
  <div>
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="员工姓名">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入员工姓名"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="name" label="员工姓名" />
      <el-table-column prop="username" label="登录账号" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="sex" label="性别" width="70" />
      <el-table-column prop="idNumber" label="身份证号" />
      <el-table-column label="账号状态" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="最后操作时间" />
    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pagination"
      layout="total, prev, pager, next"
      :total="total"
      :current-page="queryParams.page"
      :page-size="queryParams.pageSize"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEmployeePage, enableOrDisableEmployee } from '@/api/employee'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 分页查询参数
const queryParams = reactive({
  name: '',
  page: 1,
  pageSize: 10
})

// 加载员工分页数据
async function loadData() {
  loading.value = true
  try {
    const res = await getEmployeePage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 重置查询条件
function resetQuery() {
  queryParams.name = ''
  queryParams.page = 1
  loadData()
}

// 翻页
function handlePageChange(page) {
  queryParams.page = page
  loadData()
}

// 启停账号
async function handleStatusChange(row) {
  await enableOrDisableEmployee({ status: row.status, id: row.id })
  ElMessage.success('状态修改成功')
}

onMounted(loadData)
</script>

<style scoped>
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
