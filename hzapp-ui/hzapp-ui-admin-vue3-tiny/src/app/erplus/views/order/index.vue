<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="100px" :rules="rules">
      <el-form-item label="店铺" prop="shopId">
        <el-cascader
          v-model="selectedShopPath"
          :options="shopCascaderList"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="请选择平台/店铺"
          clearable
          class="!w-240px"
          @change="handleShopChange"
        />
      </el-form-item>
      <el-form-item label="配送类型" prop="fulfillType">
        <el-select v-model="queryParams.fulfillType" placeholder="请选择配送类型" clearable class="!w-240px">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="本地SKU" prop="sellerProductCode">
        <el-input v-model="queryParams.sellerProductCode" placeholder="请输入关联本地商品SKU" clearable class="!w-240px" />
      </el-form-item>
      <el-form-item label="平台商品ID" prop="platformProductCode">
        <el-input v-model="queryParams.platformProductCode" placeholder="请输入平台商品ID" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="商品名称" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入商品名称" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="订单ID" prop="orderId">
        <el-input v-model="queryParams.orderId" placeholder="请输入订单ID" clearable class="!w-240px" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTimeRange">
        <el-date-picker
v-model="queryParams.createTimeRange" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>
      <el-form-item label="更新时间" prop="createTimeRange">
        <el-date-picker
v-model="queryParams.updateTimeRange" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>
      <div class="flex justify-center">
        <el-form-item>
          <el-button @click="handleQuery" type="primary">
            <Icon icon="ep:search" class="mr-5px" /> 搜索
          </el-button>

          <el-button @click="handSync">
            <Icon icon="ep:refresh" class="mr-5px" /> 同步订单
          </el-button>

          <el-button @click="resetQuery">
            <Icon icon="ep:refresh" class="mr-5px" /> 重置
          </el-button>

        </el-form-item>
      </div>

    </el-form>
  </ContentWrap>
  <!-- 列表 -->
  <ContentWrap v-show="tableVisiable">
    <el-table
row-key="id" v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange" :tree-props="{ children: 'orderItemList' }">
      <el-table-column type="selection" width="55" :selectable="selectable" />
      <!-- <el-table-column type="index" width="20" /> -->
      <el-table-column label="图片" align="left">
        <template #default="{ row }">
          <div style="display: inline-flex; align-items: center; vertical-align: middle;">
            <el-image
v-if="row.orderItemList && row.orderItemList.length > 0" style="width: 60px; height: 60px"
              :src="row.orderItemList[0].mainImageUrl" :preview-src-list="[row.mainImageUrl]" fit="cover"
              :preview-teleported=true />
            <el-image
v-else style="width: 60px; height: 60px" :src="row.mainImageUrl"
              :preview-src-list="[row.mainImageUrl]" fit="cover" :preview-teleported=true />
          </div>

        </template>
      </el-table-column>
      <el-table-column label="订单ID/产品ID" align="left" :min-width="200">
        <template #default="{ row }">
          <div>

            <div v-if="row.orderItemList && row.orderItemList.length > 0">
              订单ID: {{ row.platformOrderId }}

            </div>
            <div v-else>
              <div> <span>产品ID: </span>{{ row.platformProductCode }} </div>
              <div> <span>买家SKU: </span>{{ row.sellerSkuCode }} </div>

            </div>
          </div>
        </template>



      </el-table-column>
      <el-table-column label="配送类型" align="center" prop="fulfillTypeName" />
      <el-table-column label="订单金额" align="center" prop="totalAmount" :tooltip-formatter="amountToolTipFormat">
        <template #default="{ row }">
          <span v-if="row.orderItemList && row.orderItemList.length > 0">{{ row.totalAmount }}</span>
          <div v-else>

            <el-tooltip effect="dark" placement="top">
              <template #content>
                <div>商品价格: {{ row.itemPrice / 100 }} {{ row.currency }}</div>
                <div>税费: {{ row.itemTax / 100 }} {{ row.currency }}</div>
                <div>运费: {{ row.shipFee / 100 }} {{ row.currency }}</div>
                <div>优惠: {{ row.promoDiscount / 100 }} {{ row.currency }}</div>
              </template>

              <span>{{ row.totalAmount / 100 }} {{ row.currency }}</span>
            </el-tooltip>

          </div>

        </template>

      </el-table-column>
      <el-table-column label="费用" align="center">
        <template #default="{ row }">

          <span v-if="row.orderItemList && row.orderItemList.length > 0">{{ 0 }}</span>
          <div v-else>
            <el-tooltip effect="dark" placement="top">
              <template #content>
                <div v-if="row.actualReferralFee">
                  <div>佣金: {{ row.actualReferralFee / 100 }} {{ row.currency }}</div>
                  <div>平台: {{ row.actualFulfillFee / 100 }} {{ row.currency }}</div>
                </div>
                <div>
                  <div>佣金(预估): {{ row.estimatedReferralFee / 100 }} {{ row.currency }}</div>
                  <div>平台(预估): {{ row.estimatedFulfillFee / 100 }} {{ row.currency }}</div>
                </div>

              </template>

              <span> <span v-if="!row.actualTotalFee">预估:</span> {{ row.estimatedTotalFee / 100 }} {{ row.currency }}
              </span>
            </el-tooltip>



          </div>
        </template>


      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="statusName" />

      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center" min-width="120px">
        <!-- <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)"
            v-hasPermi="['erplus:cross-order:update']">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['erplus:cross-order:delete']">
            删除
          </el-button>
        </template> -->
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
:total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </ContentWrap>

  <!-- 同步订单弹窗 -->
  <el-dialog title="同步订单" v-model="syncDialogVisible" width="500px">
    <el-form :model="syncForm" label-width="100px">
      <el-form-item label="同步方式">
        <el-radio-group v-model="syncForm.syncType">
          <el-radio label="time">时间范围</el-radio>
          <el-radio label="order">订单号</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="syncForm.syncType === 'time'" label="订单时间">
        <el-date-picker
          v-model="syncForm.timeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD HH:mm:ss"
          :default-time="[new Date('2024-01-01 00:00:00'), new Date('2024-01-01 23:59:59')]"
          :disabled-date="(date: Date) => date.getTime() > Date.now()"
        />
      </el-form-item>
      <el-form-item v-if="syncForm.syncType === 'order'" label="订单号">
        <el-input
          v-model="syncForm.orderIds"
          type="textarea"
          :rows="4"
          placeholder="请输入订单号，多个订单号请换行分隔"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="syncDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleDoSync" :loading="syncLoading">开始同步</el-button>
    </template>
  </el-dialog>

</template>


<script setup lang="ts">

import dayjs from 'dayjs'
import { dateFormatter } from '@/utils/formatTime'
import { isEmpty } from '@/utils/is'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform' // 已存在模块
import { ShopApi } from '@/app/erplus/api/system/shop'

import * as OrderApi from '@/app/erplus/api/order/order'


// import { CrossProductApi, CrossProduct } from '@/api/erplus/crossproduct'


/** 跨境订单信息 列表 */
defineOptions({ name: 'CrossOrderList' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(false) // 列表的加载中
const tableVisiable = ref(false) // 列表的显示与否
const list = ref<[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  platformId: undefined,
  shopId: undefined,
  fulfillType: undefined,
  sellerProductCode: undefined,
  platformProductCode: undefined,
  title: undefined,
  orderId: undefined,
  status: undefined,
  createTimeRange: [],
  updateTimeRange: []
})
const queryFormRef = ref() // 搜索的表单

const rules = reactive({
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
})

const shopCascaderList = ref<any[]>([])
const selectedShopPath = ref<any[]>([])
const syncDialogVisible = ref(false)
const syncLoading = ref(false)
const syncForm = reactive({
  syncType: 'time',
  timeRange: [] as string[],
  orderIds: ''
})

const platforms = ref<SellPlatformVO[]>([])

/** 平台相关加载 */
const getShopCascaderList = async () => {
  try {
    shopCascaderList.value = await ShopApi.getCascaderShopList()
  } catch (error) {
    console.error(error)
  }
}

const handleShopChange = (path: any[]) => {
  if (path && path.length === 2) {
    queryParams.platformId = path[0]
    queryParams.shopId = path[1]
  } else {
    queryParams.platformId = undefined
    queryParams.shopId = undefined
  }
}

onMounted(async () => {
  await getShopCascaderList()
})



const handleQuery = async () => {
  await queryFormRef.value.validate()
  queryParams.pageNo = 1
  await getList()
  message.success('查询成功')
}



const resetQuery = () => {
  queryFormRef.value.resetFields()
}

const handSync = async () => {
  // 必须先选店铺
  if (!queryParams.shopId) {
    message.warning('请先选择店铺')
    return
  }
  // 默认同步最近7天（浏览器本地时间，后端会按店铺时区重新解析）
  syncForm.timeRange = [
    dayjs().subtract(7, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
    dayjs().format('YYYY-MM-DD HH:mm:ss')
  ]
  syncDialogVisible.value = true
}

const handleDoSync = async () => {
  syncLoading.value = true
  try {
    const data = {
      platformId: queryParams.platformId,
      shopId: queryParams.shopId,
      syncType: syncForm.syncType,
      createTimeStart: syncForm.syncType === 'time' ? syncForm.timeRange[0] : undefined,
      createTimeEnd: syncForm.syncType === 'time' ? syncForm.timeRange[1] : undefined,
      orderIds: syncForm.syncType === 'order' ? syncForm.orderIds.split('\n').filter(id => id.trim()) : undefined
    }
    await OrderApi.syncCrossOrders(data)
    message.success('同步任务已提交，请稍后刷新列表查看最新数据')
    syncDialogVisible.value = false
  } finally {
    syncLoading.value = false
  }
}

/** 查询列表 */
const getList = async () => {
  tableVisiable.value = true
  loading.value = true
  try {
    const data = await OrderApi.queryCrossOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleRowCheckboxChange = (selection: any[]) => {
  console.log('selection', selection)
}

const selectable = (row: any, index: number) => {
  // 只有当行数据的某个字段满足条件时，才允许选择该行
  return row.orderItemList && row.orderItemList.length > 0;
};


const amountToolTipFormat = (row: any) => {
  if (row.orderItemList && row.orderItemList.length > 0) {
    return `订单金额: ${row.totalAmount}`;
  } else {

    return `
    订单金额: ${row.totalAmount / 100} ${row.currency}
    `
  }




  return `订单金额: ${row.totalAmount / 100} ${row.currency}`;
};


</script>


<style scoped></style>
