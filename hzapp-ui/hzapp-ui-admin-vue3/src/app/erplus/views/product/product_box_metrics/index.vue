<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">

      <el-form-item label="日期" prop="datekey">
        <el-date-picker v-model="queryParams.datekey" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>



      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>


      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <div class="flex justify-right">
      <el-button type="text" @click="triggerTable">
        <Icon v-if="showTable" icon="fa:table" class="mr-5px" />
        <Icon v-else icon="ep:data-line" class="mr-5px" />

      </el-button>
      <el-button type="text" @click="openForm">
        <Icon icon="ep:setting" class="mr-5px" />

      </el-button>

    </div>
    <template v-if="showTable">
      <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :show-summary="true"
        sum-text="summary" fixed="right">
        <el-table-column label="日期" align="center" prop="datekey" width="80" :fixed="true" />
        <el-table-column label="产品ID" align="center" prop="productId" width="80" :fixed="true" />
        <el-table-column v-for="m in metricsDefs" :label="m.lable" align="center" :prop="m.field" :key="m.field" />

      </el-table>
      <!-- 分页 -->
      <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
        @pagination="getList" />
    </template>



    <template v-else>

      <el-card class="mb-20px" shadow="hover">
        <el-skeleton :loading="loading" :rows="4" animated>
          <Echart :height="500" :options="lineOptionsData" />
        </el-skeleton>
      </el-card>

    </template>


  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ProductMetricsForm ref="formRef" @success="updateMetrics" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as ProductMetricsApi from '@/app/erplus/api/product/productMetrics'
import ProductMetricsForm from './metricsForm.vue'


import { set } from 'lodash-es'
import { EChartsOption } from 'echarts'


/** 产品监控指标 列表 */
defineOptions({ name: 'ProductMetrics' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const { query } = useRoute() 

const loading = ref(true) // 列表的加载中
const list = ref<ProductMetricsApi.ProductMetricsVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  monitorId: undefined,
  metrics: ["sales", "visitors", "conversionRate", "stock", "price"] as string[],
  datekey: []
})
const queryFormRef = ref() // 搜索的表单
const metricsDefs = ref<ProductMetricsApi.MetricsDefVO[]>([
  {
    lable: '销量',
    field: 'sales'
  },
  {
    lable: '访客数',
    field: 'visitors'
  },
  {
    lable: '转化率',
    field: 'conversionRate'
  },
  {
    lable: '库存',
    field: 'stock'
  },
  {
    lable: '价格',
    field: 'price'
  }
])



/** 查询列表 */
const getList = async () => {

  queryParams.monitorId = query.monitorId
  queryParams.productId = query.productId

  loading.value = true
  try {
    const data = await ProductMetricsApi.getProductMetricsPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const updateMetrics = async (metrics: string[]) => {
  queryParams.metrics = metrics
  metricsDefs.value = await ProductMetricsApi.getMetricsDef(queryParams);
  getList()
}



/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}


/** 添加/修改操作 */
const formRef = ref()
const openForm = () => {
  formRef.value.open(queryParams.metrics)
}

const showTable = ref(true)

const triggerTable = () => {
  showTable.value = !showTable.value
}


/** 初始化 **/
onMounted(() => {

  getList()
  getMonthlySales()
})


import { lineOptions } from './echarts-data'

const lineOptionsData = reactive<EChartsOption>(lineOptions) as EChartsOption

// 每月销售总额
const getMonthlySales = async () => {
  const data = [
    { estimate: 100, actual: 120, name: 'analysis.january' },
    { estimate: 120, actual: 82, name: 'analysis.february' },
    { estimate: 161, actual: 91, name: 'analysis.march' },
    { estimate: 134, actual: 154, name: 'analysis.april' },
    { estimate: 105, actual: 162, name: 'analysis.may' },
    { estimate: 160, actual: 140, name: 'analysis.june' },
    { estimate: 165, actual: 145, name: 'analysis.july' },
    { estimate: 114, actual: 250, name: 'analysis.august' },
    { estimate: 163, actual: 134, name: 'analysis.september' },
    { estimate: 185, actual: 56, name: 'analysis.october' },
    { estimate: 118, actual: 99, name: 'analysis.november' },
    { estimate: 123, actual: 123, name: 'analysis.december' }
  ]
  set(
    lineOptionsData,
    'xAxis.data',
    data.map((v) => t(v.name))
  )
  set(lineOptionsData, 'series', [
    {
      name: t('analysis.estimate'),
      smooth: true,
      type: 'line',
      data: data.map((v) => v.estimate),
      animationDuration: 2800,
      animationEasing: 'cubicInOut'
    },
    {
      name: t('analysis.actual'),
      smooth: true,
      type: 'line',
      itemStyle: {},
      data: data.map((v) => v.actual),
      animationDuration: 2800,
      animationEasing: 'quadraticOut'
    }
  ])
}


</script>