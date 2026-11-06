<template>
  <div class="setmeal-container">
    <!-- 搜索栏 -->
    <el-card class="box-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="套餐名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入套餐名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="套餐分类">
          <el-select v-model="queryParams.categoryId" placeholder="全部套餐分类" clearable style="width: 160px">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="售卖状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="停售" :value="0" />
            <el-option label="起售" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
        <div class="action-btn-group">
          <el-button type="danger" :icon="Delete" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            批量删除 ({{ selectedIds.length }})
          </el-button>
          <el-button type="primary" :icon="Plus" @click="openDialog('add')">新增套餐</el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="name" label="套餐名称" min-width="150" />
        <el-table-column label="套餐图片" width="100" align="center">
          <template #default="scope">
            <el-image
              v-if="scope.row.image"
              :src="scope.row.image"
              :preview-src-list="[scope.row.image]"
              preview-teleported
              fit="cover"
              class="setmeal-img"
            />
            <span v-else class="no-img">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="套餐分类" width="130" align="center" />
        <el-table-column prop="price" label="售价" width="110" align="center">
          <template #default="scope">
            <span class="price-text">¥{{ Number(scope.row.price).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="售卖状态" width="120" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" min-width="170" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
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

    <!-- 新增/编辑套餐弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增套餐' : '修改套餐'"
      width="780px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入套餐名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="套餐分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择套餐分类" style="width: 100%">
                <el-option
                  v-for="item in categoryList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐价格" prop="price">
              <el-input-number
                v-model="formData.price"
                :precision="2"
                :step="1"
                :min="0.01"
                :max="99999"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售卖状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :label="1">起售</el-radio>
                <el-radio :label="0">停售</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 套餐包含菜品配置 -->
        <el-form-item label="包含菜品" prop="setmealDishes">
          <div class="dish-config-box">
            <div class="dish-config-header">
              <span class="tip-text">配置本套餐所包含的菜品明细与份数</span>
              <el-button type="primary" link :icon="Plus" @click="openDishSelectDialog">添加菜品</el-button>
            </div>

            <el-table :data="formData.setmealDishes" border size="small" style="width: 100%">
              <el-table-column prop="name" label="菜品名称" min-width="140" />
              <el-table-column prop="price" label="单价" width="100" align="center">
                <template #default="scope">
                  ¥{{ Number(scope.row.price).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column label="份数" width="130" align="center">
                <template #default="scope">
                  <el-input-number v-model="scope.row.copies" :min="1" :max="99" size="small" style="width: 100px" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="scope">
                  <el-button link type="danger" size="small" @click="removeSetmealDish(scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>

        <!-- 套餐图片上传 -->
        <el-form-item label="套餐图片">
          <el-upload
            class="avatar-uploader"
            action="/admin/common/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸 200x200，支持 JPG、PNG 格式，不超过 5MB</div>
        </el-form-item>

        <el-form-item label="套餐描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入套餐描述信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定保存</el-button>
      </template>
    </el-dialog>

    <!-- 选择菜品弹窗 -->
    <el-dialog v-model="dishSelectVisible" title="选择菜品添加到套餐" width="650px" append-to-body>
      <div style="margin-bottom: 12px; display: flex; gap: 10px;">
        <el-select
          v-model="dishSelectCategoryId"
          placeholder="筛选菜品分类"
          clearable
          style="width: 180px"
          @change="loadCandidateDishes"
        >
          <el-option
            v-for="item in dishCategoryList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-input
          v-model="dishSearchName"
          placeholder="输入菜品名称过滤"
          clearable
          style="width: 200px"
          @input="filterCandidateDishes"
        />
      </div>

      <el-table
        ref="candidateTableRef"
        :data="filteredCandidateDishes"
        border
        height="320"
        @selection-change="handleCandidateSelection"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="categoryName" label="分类" width="120" align="center" />
        <el-table-column prop="price" label="售价" width="100" align="center">
          <template #default="scope">
            ¥{{ Number(scope.row.price).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="dishSelectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDishSelection">确定添加 ({{ tempSelectedDishes.length }})</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import {
  getSetmealPage,
  addSetmeal,
  updateSetmeal,
  deleteSetmeal,
  getSetmealById,
  enableOrDisableSetmeal
} from '@/api/setmeal'
import { getCategoryList } from '@/api/category'
import { getDishList, getDishPage } from '@/api/dish'
import { getToken } from '@/utils/auth'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryList = ref([]) // 套餐分类 (type=2)
const dishCategoryList = ref([]) // 菜品分类 (type=1)
const selectedIds = ref([])

// 搜索条件
const queryParams = reactive({
  name: '',
  categoryId: undefined,
  status: undefined,
  page: 1,
  pageSize: 10
})

// 上传鉴权头
const uploadHeaders = computed(() => ({
  token: getToken() || ''
}))

// 弹窗状态
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: undefined,
  name: '',
  categoryId: undefined,
  price: 0,
  image: '',
  description: '',
  status: 1,
  setmealDishes: []
})

const validateDishes = (rule, value, callback) => {
  if (!formData.setmealDishes || formData.setmealDishes.length === 0) {
    callback(new Error('请至少为套餐添加一种菜品'))
  } else {
    callback()
  }
}

const formRules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择套餐分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入套餐价格', trigger: 'blur' }],
  setmealDishes: [{ validator: validateDishes, trigger: 'change' }]
}

// 候选菜品选择器状态
const dishSelectVisible = ref(false)
const dishSelectCategoryId = ref(undefined)
const dishSearchName = ref('')
const allCandidateDishes = ref([])
const filteredCandidateDishes = ref([])
const tempSelectedDishes = ref([])
const candidateTableRef = ref(null)

// 加载套餐分类列表（type=2）
async function loadSetmealCategories() {
  try {
    const res = await getCategoryList({ type: 2 })
    categoryList.value = res.data || []
  } catch (error) {
    console.error('加载套餐分类失败', error)
  }
}

// 加载菜品分类列表（type=1）
async function loadDishCategories() {
  try {
    const res = await getCategoryList({ type: 1 })
    dishCategoryList.value = res.data || []
  } catch (error) {
    console.error('加载菜品分类失败', error)
  }
}

// 加载套餐分页数据
async function loadData() {
  loading.value = true
  try {
    const res = await getSetmealPage(queryParams)
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
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.page = 1
  loadData()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

function handleSizeChange(size) {
  queryParams.pageSize = size
  loadData()
}

function handlePageChange(page) {
  queryParams.page = page
  loadData()
}

// 切换售卖状态
async function handleStatusChange(row) {
  try {
    await enableOrDisableSetmeal({ status: row.status, id: row.id })
    ElMessage.success('售卖状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

// 移除套餐内菜品
function removeSetmealDish(index) {
  formData.setmealDishes.splice(index, 1)
}

// 打开菜品选择弹窗
async function openDishSelectDialog() {
  dishSelectVisible.value = true
  dishSearchName.value = ''
  dishSelectCategoryId.value = undefined
  await loadCandidateDishes()
}

// 加载待选菜品
async function loadCandidateDishes() {
  try {
    const res = await getDishPage({
      page: 1,
      pageSize: 100,
      categoryId: dishSelectCategoryId.value,
      status: 1
    })
    allCandidateDishes.value = res.data.records || []
    filterCandidateDishes()
  } catch (error) {
    console.error('加载菜品列表失败', error)
  }
}

// 过滤菜品
function filterCandidateDishes() {
  if (!dishSearchName.value) {
    filteredCandidateDishes.value = allCandidateDishes.value
  } else {
    filteredCandidateDishes.value = allCandidateDishes.value.filter(d =>
      d.name.toLowerCase().includes(dishSearchName.value.toLowerCase())
    )
  }
}

function handleCandidateSelection(selection) {
  tempSelectedDishes.value = selection
}

// 确认选中的菜品
function confirmDishSelection() {
  tempSelectedDishes.value.forEach(dish => {
    const alreadyExists = formData.setmealDishes.some(sd => sd.dishId === dish.id)
    if (!alreadyExists) {
      formData.setmealDishes.push({
        dishId: dish.id,
        name: dish.name,
        price: dish.price,
        copies: 1
      })
    }
  })
  dishSelectVisible.value = false
  tempSelectedDishes.value = []
}

// 图片上传处理
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('上传图片大小不能超过 5MB！')
    return false
  }
  return true
}

function handleUploadSuccess(res) {
  if (res.code === 1) {
    formData.image = res.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

// 打开新增/修改弹窗
async function openDialog(mode, row = null) {
  dialogMode.value = mode
  if (mode === 'edit' && row) {
    const res = await getSetmealById(row.id)
    const data = res.data
    formData.id = data.id
    formData.name = data.name
    formData.categoryId = data.categoryId
    formData.price = Number(data.price)
    formData.image = data.image || ''
    formData.description = data.description || ''
    formData.status = data.status
    formData.setmealDishes = (data.setmealDishes || []).map(sd => ({
      dishId: sd.dishId,
      name: sd.name,
      price: Number(sd.price),
      copies: sd.copies || 1
    }))
  } else {
    formData.id = undefined
    formData.name = ''
    formData.categoryId = undefined
    formData.price = 0
    formData.image = ''
    formData.description = ''
    formData.status = 1
    formData.setmealDishes = []
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
      await addSetmeal(formData)
      ElMessage.success('新增套餐成功')
    } else {
      await updateSetmeal(formData)
      ElMessage.success('修改套餐成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

// 单个删除
function handleDelete(row) {
  if (row.status === 1) {
    ElMessage.warning('起售中的套餐不能删除')
    return
  }
  ElMessageBox.confirm(`确定要删除套餐【${row.name}】吗？`, '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSetmeal([row.id])
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

// 批量删除
function handleBatchDelete() {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`确定要批量删除选中的 ${selectedIds.value.length} 个套餐吗？起售中的套餐将无法删除。`, '批量删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSetmeal(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadSetmealCategories()
  loadDishCategories()
  loadData()
})
</script>

<style scoped>
.setmeal-container {
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
  display: flex;
  gap: 10px;
}
.table-card {
  padding-bottom: 16px;
}
.setmeal-img {
  width: 44px;
  height: 44px;
  border-radius: 6px;
  display: inline-block;
}
.no-img {
  color: #c0c4cc;
  font-size: 12px;
}
.price-text {
  color: #f56c6c;
  font-weight: bold;
}
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.dish-config-box {
  width: 100%;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  padding: 12px;
  background-color: #fafafa;
}
.dish-config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.tip-text {
  font-size: 13px;
  color: #909399;
}
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.avatar-uploader:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
</style>
