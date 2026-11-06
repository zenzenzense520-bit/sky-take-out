<template>
  <div class="order-container">
    <!-- 状态筛选 Tab 栏（带待处理角标） -->
    <el-card class="status-card" shadow="never">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane name="2">
          <template #label>
            <span>待接单</span>
            <el-badge
              v-if="statistics.toBeConfirmed > 0"
              :value="statistics.toBeConfirmed"
              class="tab-badge"
              type="danger"
            />
          </template>
        </el-tab-pane>
        <el-tab-pane name="3">
          <template #label>
            <span>待派送</span>
            <el-badge
              v-if="statistics.confirmed > 0"
              :value="statistics.confirmed"
              class="tab-badge"
              type="warning"
            />
          </template>
        </el-tab-pane>
        <el-tab-pane name="4">
          <template #label>
            <span>派送中</span>
            <el-badge
              v-if="statistics.deliveryInProgress > 0"
              :value="statistics.deliveryInProgress"
              class="tab-badge"
              type="primary"
            />
          </template>
        </el-tab-pane>
        <el-tab-pane label="已完成" name="5" />
        <el-tab-pane label="已取消" name="6" />
      </el-tabs>
    </el-card>

    <!-- 搜索筛选表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef">
        <el-form-item label="订单号">
          <el-input
            v-model="queryParams.number"
            placeholder="请输入订单号"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 160px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 340px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单列表 -->
    <el-card shadow="never">
      <el-table :data="orderList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="number" label="订单号" min-width="160" />

        <el-table-column label="订单菜品" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ formatDishesSummary(row.orderDetailList) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="收货人" min-width="130">
          <template #default="{ row }">
            <div>{{ row.consignee || '-' }}</div>
            <div class="sub-text">{{ row.phone }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />

        <el-table-column prop="orderTime" label="下单时间" min-width="165" />

        <el-table-column prop="amount" label="实收金额" min-width="100">
          <template #default="{ row }">
            <span class="amount-text">¥{{ Number(row.amount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="支付状态" min-width="95">
          <template #default="{ row }">
            <el-tag :type="getPayStatusTagType(row.payStatus)" size="small">
              {{ formatPayStatus(row.payStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="订单状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusTagType(row.status)" size="small">
              {{ formatOrderStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <!-- 待接单：接单 / 拒单 -->
            <template v-if="row.status === 2">
              <el-button type="primary" link size="small" @click="handleConfirm(row)">接单</el-button>
              <el-button type="danger" link size="small" @click="openRejectDialog(row)">拒单</el-button>
            </template>

            <!-- 待派送：派送 / 取消 -->
            <template v-else-if="row.status === 3">
              <el-button type="primary" link size="small" @click="handleDelivery(row)">派送</el-button>
              <el-button type="danger" link size="small" @click="openCancelDialog(row)">取消</el-button>
            </template>

            <!-- 派送中：完成 -->
            <template v-else-if="row.status === 4">
              <el-button type="success" link size="small" @click="handleComplete(row)">完成</el-button>
            </template>

            <!-- 详情按键全部状态均可查看 -->
            <el-button type="primary" link size="small" @click="handleDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchOrders"
          @current-change="fetchOrders"
        />
      </div>
    </el-card>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="订单详情"
      width="780px"
      destroy-on-close
    >
      <div v-if="currentDetail" class="detail-container">
        <!-- 订单基本信息 -->
        <el-descriptions title="基本信息" :column="3" border size="small">
          <el-descriptions-item label="订单号">{{ currentDetail.number }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getOrderStatusTagType(currentDetail.status)" size="small">
              {{ formatOrderStatus(currentDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentDetail.orderTime }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">
            {{ currentDetail.payMethod === 1 ? '微信支付' : '支付宝' }}
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPayStatusTagType(currentDetail.payStatus)" size="small">
              {{ formatPayStatus(currentDetail.payStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="amount-text">¥{{ Number(currentDetail.amount || 0).toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="打包费">¥{{ currentDetail.packAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="配送费">¥6.00</el-descriptions-item>
          <el-descriptions-item label="餐具数量">
            {{ currentDetail.tablewareStatus === 1 ? currentDetail.tablewareNumber + ' 份' : '根据餐量提供' }}
          </el-descriptions-item>
          <el-descriptions-item label="买家备注" :span="3">
            {{ currentDetail.remark || '无' }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentDetail.rejectionReason" label="拒单原因" :span="3">
            <span class="danger-text">{{ currentDetail.rejectionReason }}</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentDetail.cancelReason" label="取消原因" :span="3">
            <span class="danger-text">{{ currentDetail.cancelReason }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 收货人信息 -->
        <el-descriptions title="收货信息" :column="2" border size="small" style="margin-top: 16px">
          <el-descriptions-item label="收货人">{{ currentDetail.consignee }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="送达地址" :span="2">{{ currentDetail.address }}</el-descriptions-item>
        </el-descriptions>

        <!-- 菜品明细表 -->
        <div class="detail-dishes-title">菜品明细</div>
        <el-table :data="currentDetail.orderDetailList || []" border size="small" style="width: 100%">
          <el-table-column label="图片" width="70">
            <template #default="{ row }">
              <el-image
                :src="row.image"
                style="width: 45px; height: 45px; border-radius: 4px"
                fit="cover"
              >
                <template #error>
                  <div class="image-slot">无图</div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" min-width="140" />
          <el-table-column prop="dishFlavor" label="规格/口味" min-width="120">
            <template #default="{ row }">
              {{ row.dishFlavor || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="单价" width="90">
            <template #default="{ row }">¥{{ Number(row.amount || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="number" label="数量" width="80" align="center" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">
              ¥{{ (Number(row.amount || 0) * (row.number || 1)).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 拒单弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒单原因" width="500px" destroy-on-close>
      <el-form :model="rejectForm" label-width="90px">
        <el-form-item label="快捷选择">
          <el-select
            v-model="rejectQuickReason"
            placeholder="请选择常用拒单原因"
            @change="rejectForm.rejectionReason = rejectQuickReason"
            style="width: 100%"
          >
            <el-option label="菜品已售罄" value="菜品已售罄" />
            <el-option label="餐厅已打烊" value="餐厅已打烊" />
            <el-option label="超出配送范围" value="超出配送范围" />
            <el-option label="高峰期出餐时间过长" value="高峰期出餐时间过长" />
            <el-option label="其它原因" value="其它原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" required>
          <el-input
            v-model="rejectForm.rejectionReason"
            type="textarea"
            :rows="3"
            placeholder="请输入拒单具体原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认拒单</el-button>
      </template>
    </el-dialog>

    <!-- 取消订单弹窗 -->
    <el-dialog v-model="cancelDialogVisible" title="取消订单" width="500px" destroy-on-close>
      <el-form :model="cancelForm" label-width="90px">
        <el-form-item label="快捷选择">
          <el-select
            v-model="cancelQuickReason"
            placeholder="请选择常用取消原因"
            @change="cancelForm.cancelReason = cancelQuickReason"
            style="width: 100%"
          >
            <el-option label="顾客电话要求取消" value="顾客电话要求取消" />
            <el-option label="配送员无法接单" value="配送员无法接单" />
            <el-option label="恶劣天气无法配送" value="恶劣天气无法配送" />
            <el-option label="其它原因" value="其它原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="详细说明" required>
          <el-input
            v-model="cancelForm.cancelReason"
            type="textarea"
            :rows="3"
            placeholder="请输入取消原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitCancel">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getOrderPage,
  getOrderStatistics,
  getOrderDetail,
  confirmOrder,
  rejectOrder,
  cancelOrder,
  deliveryOrder,
  completeOrder
} from '@/api/order'

// 查询条件
const activeTab = ref('all')
const dateRange = ref([])
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  number: '',
  phone: '',
  status: null,
  beginTime: '',
  endTime: ''
})

// 数据状态
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const statistics = reactive({
  toBeConfirmed: 0,
  confirmed: 0,
  deliveryInProgress: 0
})

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref(null)

// 拒单与取消弹窗
const rejectDialogVisible = ref(false)
const rejectQuickReason = ref('')
const rejectForm = reactive({ id: null, rejectionReason: '' })

const cancelDialogVisible = ref(false)
const cancelQuickReason = ref('')
const cancelForm = reactive({ id: null, cancelReason: '' })

// 状态映射辅助函数
function formatOrderStatus(status) {
  const map = {
    1: '待付款',
    2: '待接单',
    3: '待派送',
    4: '派送中',
    5: '已完成',
    6: '已取消'
  }
  return map[status] || '未知'
}

function getOrderStatusTagType(status) {
  const map = {
    1: 'info',
    2: 'warning',
    3: 'primary',
    4: '',
    5: 'success',
    6: 'danger'
  }
  return map[status] || 'info'
}

function formatPayStatus(payStatus) {
  const map = {
    0: '未支付',
    1: '已支付',
    2: '退款'
  }
  return map[payStatus] || '未支付'
}

function getPayStatusTagType(payStatus) {
  const map = {
    0: 'danger',
    1: 'success',
    2: 'info'
  }
  return map[payStatus] || 'info'
}

function formatDishesSummary(detailList) {
  if (!detailList || detailList.length === 0) return '-'
  return detailList.map((d) => `${d.name} ×${d.number}`).join('；')
}

// 获取订单列表
async function fetchOrders() {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.beginTime = dateRange.value[0]
      queryParams.endTime = dateRange.value[1]
    } else {
      queryParams.beginTime = ''
      queryParams.endTime = ''
    }

    const res = await getOrderPage(queryParams)
    orderList.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

// 获取订单待处理统计数
async function fetchStatistics() {
  try {
    const res = await getOrderStatistics()
    if (res.data) {
      statistics.toBeConfirmed = res.data.toBeConfirmed || 0
      statistics.confirmed = res.data.confirmed || 0
      statistics.deliveryInProgress = res.data.deliveryInProgress || 0
    }
  } catch (e) {
    console.error('获取订单统计角标失败', e)
  }
}

// Tab 切换
function handleTabChange(tabName) {
  queryParams.page = 1
  if (tabName === 'all') {
    queryParams.status = null
  } else {
    queryParams.status = Number(tabName)
  }
  fetchOrders()
}

// 查询与重置
function handleQuery() {
  queryParams.page = 1
  fetchOrders()
  fetchStatistics()
}

function handleReset() {
  queryParams.number = ''
  queryParams.phone = ''
  dateRange.value = []
  queryParams.beginTime = ''
  queryParams.endTime = ''
  queryParams.page = 1
  fetchOrders()
  fetchStatistics()
}

// 查看详情
async function handleDetail(row) {
  try {
    const res = await getOrderDetail(row.id)
    currentDetail.value = res.data
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  }
}

// 接单
async function handleConfirm(row) {
  await ElMessageBox.confirm(`确认接单？订单号：${row.number}`, '提示', {
    confirmButtonText: '确认接单',
    cancelButtonText: '取消',
    type: 'info'
  })
  await confirmOrder(row.id)
  ElMessage.success('接单成功')
  fetchOrders()
  fetchStatistics()
}

// 拒单
function openRejectDialog(row) {
  rejectForm.id = row.id
  rejectForm.rejectionReason = ''
  rejectQuickReason.value = ''
  rejectDialogVisible.value = true
}

async function submitReject() {
  if (!rejectForm.rejectionReason.trim()) {
    return ElMessage.warning('请输入或选择拒单原因')
  }
  await rejectOrder(rejectForm)
  ElMessage.success('已拒单')
  rejectDialogVisible.value = false
  fetchOrders()
  fetchStatistics()
}

// 派送
async function handleDelivery(row) {
  await ElMessageBox.confirm(`确认开始派送？订单号：${row.number}`, '提示', {
    confirmButtonText: '确认派送',
    cancelButtonText: '取消',
    type: 'primary'
  })
  await deliveryOrder(row.id)
  ElMessage.success('订单已开始派送')
  fetchOrders()
  fetchStatistics()
}

// 取消订单
function openCancelDialog(row) {
  cancelForm.id = row.id
  cancelForm.cancelReason = ''
  cancelQuickReason.value = ''
  cancelDialogVisible.value = true
}

async function submitCancel() {
  if (!cancelForm.cancelReason.trim()) {
    return ElMessage.warning('请输入或选择取消原因')
  }
  await cancelOrder(cancelForm)
  ElMessage.success('订单已取消')
  cancelDialogVisible.value = false
  fetchOrders()
  fetchStatistics()
}

// 完成订单
async function handleComplete(row) {
  await ElMessageBox.confirm(`确认完成此订单？订单号：${row.number}`, '提示', {
    confirmButtonText: '确认完成',
    cancelButtonText: '取消',
    type: 'success'
  })
  await completeOrder(row.id)
  ElMessage.success('订单已完成')
  fetchOrders()
  fetchStatistics()
}

onMounted(() => {
  fetchOrders()
  fetchStatistics()
})
</script>

<style scoped lang="scss">
.order-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-card {
  :deep(.el-card__body) {
    padding-bottom: 0;
  }
}

.tab-badge {
  margin-left: 6px;
  :deep(.el-badge__content) {
    transform: translateY(-2px);
  }
}

.search-card {
  :deep(.el-card__body) {
    padding-bottom: 2px;
  }
}

.amount-text {
  font-weight: 600;
  color: #f56c6c;
}

.sub-text {
  font-size: 12px;
  color: #909399;
}

.danger-text {
  color: #f56c6c;
}

.detail-dishes-title {
  font-size: 14px;
  font-weight: 600;
  margin: 16px 0 10px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 11px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
