<template>
  <div class="category-container">
    <!-- 搜索与操作栏 -->
    <el-card class="box-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="分类名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入分类名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类类型">
          <el-select v-model="queryParams.type" placeholder="全部类型" clearable style="width: 140px">
            <el-option label="菜品分类" :value="1" />
            <el-option label="套餐分类" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
        <div class="action-btn-group">
          <el-button type="primary" :icon="Plus" @click="openDialog('add')">新增分类</el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column prop="type" label="分类类型" width="130" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : 'warning'">
              {{ scope.row.type === 1 ? '菜品分类' : '套餐分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序权重" width="110" align="center" />
        <el-table-column label="分类状态" width="120" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" min-width="180" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openDialog('edit', scope.row)">
              修改
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑分类弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增分类' : '修改分类'"
      width="480px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="分类类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio :label="1">菜品分类</el-radio>
            <el-radio :label="2">套餐分类</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="排序权重" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import {
  getCategoryPage,
  addCategory,
  updateCategory,
  deleteCategory,
  enableOrDisableCategory
} from '@/api/category'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 查询条件
const queryParams = reactive({
  name: '',
  type: undefined,
  page: 1,
  pageSize: 10
})

// 弹窗表单状态
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: undefined,
  name: '',
  type: 1,
  sort: 0
})

const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择分类类型', trigger: 'change' }],
  sort: [{ required: true, message: '请输入排序权重', trigger: 'blur' }]
}

// 加载分页数据
async function loadData() {
  loading.value = true
  try {
    const res = await getCategoryPage(queryParams)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

function resetQuery() {
  queryParams.name = ''
  queryParams.type = undefined
  queryParams.page = 1
  loadData()
}

function handleSizeChange(size) {
  queryParams.pageSize = size
  loadData()
}

function handlePageChange(page) {
  queryParams.page = page
  loadData()
}

// 切换状态
async function handleStatusChange(row) {
  try {
    await enableOrDisableCategory({ status: row.status, id: row.id })
    ElMessage.success('分类状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1 // 恢复回原状态
  }
}

// 打开弹窗
function openDialog(mode, row = null) {
  dialogMode.value = mode
  if (mode === 'edit' && row) {
    formData.id = row.id
    formData.name = row.name
    formData.type = row.type
    formData.sort = row.sort
  } else {
    formData.id = undefined
    formData.name = ''
    formData.type = 1
    formData.sort = 0
  }
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  submitLoading.value = true
  try {
    if (dialogMode.value === 'add') {
      await addCategory(formData)
      ElMessage.success('新增分类成功')
    } else {
      await updateCategory(formData)
      ElMessage.success('修改分类成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

// 删除分类
function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除分类【${row.name}】吗？若分类下关联了菜品或套餐将无法删除。`, '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteCategory(row.id)
    ElMessage.success('删除分类成功')
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.category-container {
  padding: 4px;
}
.box-card {
  margin-bottom: 16px;
}
.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}
.action-btn-group {
  margin-left: auto;
}
.table-card {
  padding-bottom: 16px;
}
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
