<template>
  <div class="report-container">
    <!-- 顶部操作栏：日期筛选与 Excel 导出 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-wrapper">
        <div class="filter-left">
          <span class="filter-label">统计区间：</span>
          <el-radio-group v-model="dateShortcut" size="default" @change="handleShortcutChange">
            <el-radio-button label="7">近 7 天</el-radio-button>
            <el-radio-button label="30">近 30 天</el-radio-button>
            <el-radio-button label="thisWeek">本周</el-radio-button>
            <el-radio-button label="thisMonth">本月</el-radio-button>
          </el-radio-group>

          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            :clearable="false"
            style="width: 260px; margin-left: 12px"
            @change="handleDateRangeChange"
          />
        </div>

        <div class="filter-right">
          <el-button type="success" :icon="Download" :loading="exporting" @click="handleExport">
            导出运营数据 Excel
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 概览指标卡片 -->
    <el-row :gutter="16" class="metrics-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="4" :xl="4">
        <el-card shadow="never" class="metric-card">
          <div class="metric-title">营业额（元）</div>
          <div class="metric-value amount">¥{{ overview.turnover.toFixed(2) }}</div>
          <div class="metric-desc">统计区间累计营业额</div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="5">
        <el-card shadow="never" class="metric-card">
          <div class="metric-title">有效订单数（单）</div>
          <div class="metric-value">{{ overview.validOrderCount }}</div>
          <div class="metric-desc">总订单数 {{ overview.totalOrderCount }} 单</div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="5">
        <el-card shadow="never" class="metric-card">
          <div class="metric-title">订单完成率</div>
          <div class="metric-value success">
            {{ (overview.orderCompletionRate * 100).toFixed(1) }}%
          </div>
          <div class="metric-desc">有效订单 / 全部订单</div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="5">
        <el-card shadow="never" class="metric-card">
          <div class="metric-title">平均客单价（元）</div>
          <div class="metric-value">¥{{ overview.unitPrice.toFixed(2) }}</div>
          <div class="metric-desc">有效订单实收平均值</div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="5" :xl="5">
        <el-card shadow="never" class="metric-card">
          <div class="metric-title">新增用户（人）</div>
          <div class="metric-value primary">{{ overview.newUserCount }}</div>
          <div class="metric-desc">当前累计用户 {{ overview.totalUsers }} 人</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区 -->
    <el-row :gutter="16" class="charts-row">
      <!-- 营业额趋势折线图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>营业额趋势统计</span>
            </div>
          </template>
          <div ref="turnoverChartRef" class="chart-container" />
        </el-card>
      </el-col>

      <!-- 用户增长双折线图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>用户增长统计</span>
            </div>
          </template>
          <div ref="userChartRef" class="chart-container" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="charts-row" style="margin-top: 16px">
      <!-- 订单量统计图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>订单量与完成趋势</span>
            </div>
          </template>
          <div ref="orderChartRef" class="chart-container" />
        </el-card>
      </el-col>

      <!-- 销量排名 Top10 水平条形图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>热销商品 Top 10</span>
            </div>
          </template>
          <div ref="top10ChartRef" class="chart-container" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  getTurnoverStatistics,
  getUserStatistics,
  getOrderReportStatistics,
  getSalesTop10,
  exportReport
} from '@/api/report'

// 日期工具
function formatDate(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function getInitialRange(days = 30) {
  const end = new Date()
  const start = new Date()
  start.setDate(end.getDate() - days + 1)
  return [formatDate(start), formatDate(end)]
}

// 筛选状态
const dateShortcut = ref('30')
const dateRange = ref(getInitialRange(30))
const exporting = ref(false)

// 概览指标
const overview = reactive({
  turnover: 0,
  validOrderCount: 0,
  totalOrderCount: 0,
  orderCompletionRate: 0,
  unitPrice: 0,
  newUserCount: 0,
  totalUsers: 0
})

// 图表 DOM 引用与 ECharts 实例
const turnoverChartRef = ref(null)
const userChartRef = ref(null)
const orderChartRef = ref(null)
const top10ChartRef = ref(null)

let turnoverChart = null
let userChart = null
let orderChart = null
let top10Chart = null

// 日期快捷选项切换
function handleShortcutChange(val) {
  const end = new Date()
  let start = new Date()
  if (val === '7') {
    start.setDate(end.getDate() - 6)
  } else if (val === '30') {
    start.setDate(end.getDate() - 29)
  } else if (val === 'thisWeek') {
    const day = end.getDay() || 7
    start.setDate(end.getDate() - day + 1)
  } else if (val === 'thisMonth') {
    start = new Date(end.getFullYear(), end.getMonth(), 1)
  }
  dateRange.value = [formatDate(start), formatDate(end)]
  loadAllData()
}

function handleDateRangeChange() {
  dateShortcut.value = ''
  loadAllData()
}

// 加载并渲染营业额
async function loadTurnover() {
  const [begin, end] = dateRange.value
  const res = await getTurnoverStatistics({ begin, end })
  const dates = res.data.dateList ? res.data.dateList.split(',') : []
  const turnovers = res.data.turnoverList
    ? res.data.turnoverList.split(',').map(Number)
    : []

  // 计算累计营业额
  const sum = turnovers.reduce((acc, cur) => acc + (cur || 0), 0)
  overview.turnover = sum
  calculateUnitPrice()

  if (!turnoverChart && turnoverChartRef.value) {
    turnoverChart = echarts.init(turnoverChartRef.value)
  }
  turnoverChart?.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br />营业额：¥{c}'
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '金额 (元)'
    },
    series: [
      {
        name: '营业额',
        type: 'line',
        smooth: true,
        data: turnovers,
        itemStyle: { color: '#ffb020' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 176, 32, 0.4)' },
            { offset: 1, color: 'rgba(255, 176, 32, 0.02)' }
          ])
        }
      }
    ]
  })
}

// 加载并渲染用户统计
async function loadUsers() {
  const [begin, end] = dateRange.value
  const res = await getUserStatistics({ begin, end })
  const dates = res.data.dateList ? res.data.dateList.split(',') : []
  const totalUsers = res.data.totalUserList
    ? res.data.totalUserList.split(',').map(Number)
    : []
  const newUsers = res.data.newUserList
    ? res.data.newUserList.split(',').map(Number)
    : []

  overview.newUserCount = newUsers.reduce((acc, cur) => acc + (cur || 0), 0)
  overview.totalUsers = totalUsers.length > 0 ? totalUsers[totalUsers.length - 1] : 0

  if (!userChart && userChartRef.value) {
    userChart = echarts.init(userChartRef.value)
  }
  userChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['总用户量', '新增用户'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: { type: 'value', name: '用户数 (人)' },
    series: [
      {
        name: '总用户量',
        type: 'line',
        smooth: true,
        data: totalUsers,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: newUsers,
        itemStyle: { color: '#67c23a' }
      }
    ]
  })
}

// 加载并渲染订单统计
async function loadOrders() {
  const [begin, end] = dateRange.value
  const res = await getOrderReportStatistics({ begin, end })
  const dates = res.data.dateList ? res.data.dateList.split(',') : []
  const orderCounts = res.data.orderCountList
    ? res.data.orderCountList.split(',').map(Number)
    : []
  const validOrderCounts = res.data.validOrderCountList
    ? res.data.validOrderCountList.split(',').map(Number)
    : []

  overview.totalOrderCount = res.data.totalOrderCount || 0
  overview.validOrderCount = res.data.validOrderCount || 0
  overview.orderCompletionRate = res.data.orderCompletionRate || 0
  calculateUnitPrice()

  if (!orderChart && orderChartRef.value) {
    orderChart = echarts.init(orderChartRef.value)
  }
  orderChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单总数', '有效订单'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: { type: 'value', name: '订单量 (单)' },
    series: [
      {
        name: '订单总数',
        type: 'bar',
        data: orderCounts,
        itemStyle: { color: '#909399' }
      },
      {
        name: '有效订单',
        type: 'bar',
        data: validOrderCounts,
        itemStyle: { color: '#409eff' }
      }
    ]
  })
}

// 加载并渲染销量 Top10
async function loadTop10() {
  const [begin, end] = dateRange.value
  const res = await getSalesTop10({ begin, end })
  const names = res.data.nameList ? res.data.nameList.split(',') : []
  const numbers = res.data.numberList
    ? res.data.numberList.split(',').map(Number)
    : []

  // Top10 在条形图从下往上排，reverse 让排名第一的在最上方
  const reversedNames = [...names].reverse()
  const reversedNumbers = [...numbers].reverse()

  if (!top10Chart && top10ChartRef.value) {
    top10Chart = echarts.init(top10ChartRef.value)
  }
  top10Chart?.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}：销量 {c} 份'
    },
    grid: { left: '3%', right: '6%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'value',
      name: '销量 (份)'
    },
    yAxis: {
      type: 'category',
      data: reversedNames,
      axisLabel: {
        width: 120,
        overflow: 'truncate'
      }
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: reversedNumbers,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#36cfc9' },
            { offset: 1, color: '#13c2c2' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}'
        }
      }
    ]
  })
}

// 计算平均客单价
function calculateUnitPrice() {
  if (overview.validOrderCount > 0 && overview.turnover > 0) {
    overview.unitPrice = overview.turnover / overview.validOrderCount
  } else {
    overview.unitPrice = 0
  }
}

// 加载所有数据
async function loadAllData() {
  try {
    await Promise.all([loadTurnover(), loadUsers(), loadOrders(), loadTop10()])
  } catch (e) {
    ElMessage.error('加载统计数据失败')
  }
}

// 导出 Excel 运营报表
async function handleExport() {
  exporting.value = true
  try {
    const data = await exportReport()
    const blob = new Blob([data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `运营数据报表_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('运营数据 Excel 报表导出成功')
  } catch (e) {
    ElMessage.error('导出 Excel 报表失败')
  } finally {
    exporting.value = false
  }
}

function handleResize() {
  turnoverChart?.resize()
  userChart?.resize()
  orderChart?.resize()
  top10Chart?.resize()
}

onMounted(() => {
  nextTick(() => {
    loadAllData()
    window.addEventListener('resize', handleResize)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  turnoverChart?.dispose()
  userChart?.dispose()
  orderChart?.dispose()
  top10Chart?.dispose()
})
</script>

<style scoped lang="scss">
.report-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card {
  .filter-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
  }

  .filter-left {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;

    .filter-label {
      font-size: 14px;
      color: #606266;
      font-weight: 500;
    }
  }
}

.metrics-row {
  .metric-card {
    background: #fff;
    border-radius: 8px;

    .metric-title {
      font-size: 13px;
      color: #909399;
      margin-bottom: 8px;
    }

    .metric-value {
      font-size: 26px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 6px;

      &.amount {
        color: #ffb020;
      }
      &.success {
        color: #67c23a;
      }
      &.primary {
        color: #409eff;
      }
    }

    .metric-desc {
      font-size: 12px;
      color: #c0c4cc;
    }
  }
}

.charts-row {
  .chart-card {
    border-radius: 8px;

    .chart-header {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }

    .chart-container {
      width: 100%;
      height: 340px;
    }
  }
}
</style>
