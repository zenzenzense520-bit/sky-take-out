<template>
  <div class="dish-container">
    <!-- 搜索栏 -->
    <el-card class="box-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="菜品名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入菜品名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="菜品分类">
          <el-select v-model="queryParams.categoryId" placeholder="全部菜品分类" clearable style="width: 160px">
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
          <el-button type="primary" :icon="Plus" @click="openDialog('add')">新增菜品</el-button>
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
        <el-table-column prop="name" label="菜品名称" min-width="150" />
        <el-table-column label="菜品图片" width="100" align="center">
          <template #default="scope">
            <el-image
              v-if="scope.row.image"
              :src="scope.row.image"
              :preview-src-list="[scope.row.image]"
              preview-teleported
              fit="cover"
              class="dish-img"
            />
            <span v-else class="no-img">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="菜品分类" width="130" align="center" />
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

    <!-- 新增/编辑菜品弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增菜品' : '修改菜品'"
      width="700px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜品名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入菜品名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜品分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
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
            <el-form-item label="菜品价格" prop="price">
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

        <!-- 口味配置区域 -->
        <el-form-item label="口味配置">
          <div class="flavor-box">
            <div class="flavor-header">
              <span class="tip-text">配置菜品可供顾客自选的口味或做法</span>
              <el-button type="primary" link :icon="Plus" @click="addFlavorItem">添加口味</el-button>
            </div>
            <div v-if="formData.flavors.length === 0" class="flavor-empty">
              暂未配置口味，点击上方“添加口味”按钮添加
            </div>
            <div
              v-for="(flavor, index) in formData.flavors"
              :key="index"
              class="flavor-row"
            >
              <el-select
                v-model="flavor.name"
                placeholder="口味名称"
                filterable
                allow-create
                default-first-option
                style="width: 140px; margin-right: 12px"
                @change="handlePresetFlavorChange(flavor)"
              >
                <el-option
                  v-for="preset in presetFlavors"
                  :key="preset.name"
                  :label="preset.name"
                  :value="preset.name"
                />
              </el-select>

              <div class="flavor-tags">
                <el-tag
                  v-for="(val, tagIdx) in flavor.valueList"
                  :key="tagIdx"
                  closable
                  @close="removeFlavorTag(flavor, tagIdx)"
                  style="margin-right: 6px; margin-bottom: 4px"
                >
                  {{ val }}
                </el-tag>
                <el-input
                  v-if="flavor.inputVisible"
                  v-model="flavor.inputValue"
                  size="small"
                  style="width: 90px; margin-bottom: 4px"
                  @keyup.enter="confirmTagInput(flavor)"
                  @blur="confirmTagInput(flavor)"
                />
                <el-button
                  v-else
                  size="small"
                  @click="showTagInput(flavor)"
                  style="margin-bottom: 4px"
                >
                  + 新选项
                </el-button>
              </div>

              <el-button
                type="danger"
                link
                :icon="Delete"
                @click="removeFlavorItem(index)"
                style="margin-left: 12px"
              >
                删除
              </el-button>
            </div>
          </div>
        </el-form-item>

        <!-- 菜品图片上传 -->
        <el-form-item label="菜品图片">
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

        <el-form-item label="菜品描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入菜品描述信息"
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import { getDishPage, addDish, updateDish, deleteDish, getDishById, enableOrDisableDish } from '@/api/dish'
import { getCategoryList } from '@/api/category'
import { getToken } from '@/utils/auth'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryList = ref([])
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

// 预设口味数据
const presetFlavors = [
  { name: '甜味', values: ['无糖', '少糖', '半糖', '多糖', '全糖'] },
  { name: '温度', values: ['热饮', '常温', '去冰', '微冰', '多冰'] },
  { name: '忌口', values: ['不要葱', '不要蒜', '不要香菜', '不要辣'] },
  { name: '辣度', values: ['不辣', '微辣', '中辣', '重辣', '特辣'] }
]

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
  flavors: []
})

const formRules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入菜品价格', trigger: 'blur' }]
}

// 加载分类列表（仅菜品分类 type=1）
async function loadCategories() {
  try {
    const res = await getCategoryList({ type: 1 })
    categoryList.value = res.data || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

// 加载菜品分页数据
async function loadData() {
  loading.value = true
  try {
    const res = await getDishPage(queryParams)
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
    await enableOrDisableDish({ status: row.status, id: row.id })
    ElMessage.success('售卖状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

// 口味管理
function addFlavorItem() {
  formData.flavors.push({
    name: '',
    valueList: [],
    inputVisible: false,
    inputValue: ''
  })
}

function removeFlavorItem(index) {
  formData.flavors.splice(index, 1)
}

function handlePresetFlavorChange(flavor) {
  const matched = presetFlavors.find(p => p.name === flavor.name)
  if (matched && flavor.valueList.length === 0) {
    flavor.valueList = [...matched.values]
  }
}

function removeFlavorTag(flavor, tagIdx) {
  flavor.valueList.splice(tagIdx, 1)
}

function showTagInput(flavor) {
  flavor.inputVisible = true
  flavor.inputValue = ''
}

function confirmTagInput(flavor) {
  if (flavor.inputValue && flavor.inputValue.trim()) {
    flavor.valueList.push(flavor.inputValue.trim())
  }
  flavor.inputVisible = false
  flavor.inputValue = ''
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

// 打开弹窗
async function openDialog(mode, row = null) {
  dialogMode.value = mode
  if (mode === 'edit' && row) {
    const res = await getDishById(row.id)
    const data = res.data
    formData.id = data.id
    formData.name = data.name
    formData.categoryId = data.categoryId
    formData.price = Number(data.price)
    formData.image = data.image || ''
    formData.description = data.description || ''
    formData.status = data.status

    // 解析口味列表
    formData.flavors = (data.flavors || []).map(f => {
      let valueList = []
      try {
        valueList = typeof f.value === 'string' ? JSON.parse(f.value) : f.value
      } catch (e) {
        valueList = f.value ? [f.value] : []
      }
      return {
        name: f.name,
        valueList: Array.isArray(valueList) ? valueList : [],
        inputVisible: false,
        inputValue: ''
      }
    })
  } else {
    formData.id = undefined
    formData.name = ''
    formData.categoryId = undefined
    formData.price = 0
    formData.image = ''
    formData.description = ''
    formData.status = 1
    formData.flavors = []
  }
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  // 格式化口味数据
  const formattedFlavors = formData.flavors
    .filter(f => f.name && f.valueList.length > 0)
    .map(f => ({
      name: f.name,
      value: JSON.stringify(f.valueList)
    }))

  const submitPayload = {
    id: formData.id,
    name: formData.name,
    categoryId: formData.categoryId,
    price: formData.price,
    image: formData.image,
    description: formData.description,
    status: formData.status,
    flavors: formattedFlavors
  }

  submitLoading.value = true
  try {
    if (dialogMode.value === 'add') {
      await addDish(submitPayload)
      ElMessage.success('新增菜品成功')
    } else {
      await updateDish(submitPayload)
      ElMessage.success('修改菜品成功')
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
    ElMessage.warning('起售中的菜品不能删除')
    return
  }
  ElMessageBox.confirm(`确定要删除菜品【${row.name}】吗？`, '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDish([row.id])
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

// 批量删除
function handleBatchDelete() {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`确定要批量删除选中的 ${selectedIds.value.length} 个菜品吗？起售中或关联套餐的菜品将无法删除。`, '批量删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDish(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.dish-container {
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
.dish-img {
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
.flavor-box {
  width: 100%;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  padding: 12px;
  background-color: #fafafa;
}
.flavor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.tip-text {
  font-size: 13px;
  color: #909399;
}
.flavor-empty {
  font-size: 13px;
  color: #c0c4cc;
  text-align: center;
  padding: 10px 0;
}
.flavor-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.flavor-tags {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
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
