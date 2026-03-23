<template>

  <!-- 列表 -->
  <ContentWrap>
    <div class="flex mb-10px">

      <el-button @click="syncListing" type="primary" :loading="syncLoading" v-show="selectedProduct.length > 0"
        size="small">
        <Icon icon="ep:refresh" class="mr-5px" /> 同步选中商品
      </el-button>

    </div>
    <el-table row-key="id" v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleSelect">
      <el-table-column type="selection" width="55" />
      <el-table-column label="图片" align="center" prop="mainImageUrl">
        <template #default="{ row }">
          <el-image style="width: 60px; height: 60px" :src="row.mainImage.url" :preview-src-list="[row.mainImage.url]"
            fit="cover" :preview-teleported=true />
        </template>


      </el-table-column>
      <!-- <el-table-column label="商品视频" align="center" prop="videoUrl" /> -->
      <el-table-column label="商品ID" align="left" prop="sellerProductCode" min-width="130">
        <template #default="{ row }">
          <div>ASIN: {{ row.platformProductCode }}</div>
          <div> SKU: {{ row.sellerProductCode }}</div>
        </template>


      </el-table-column>

      <el-table-column label="商品名称" align="center" prop="title" min-width="150" />
      <el-table-column label="商品品牌" align="center" prop="brand" />
      <el-table-column label="配送类型" align="center" prop="fulfillTypeName" />
      <el-table-column label="库存" align="center">
        <template #default="{ row }">

          <el-tooltip v-if="row.inventory" effect="dark" placement="top">
            <template #content>
              <div>可售数量: {{ row.inventory.fulfillableQuantity }}</div>
              <div>待入库数量: {{ row.inventory.inboundWorkingQuantity + row.inventory.inboundShippedQuantity +
                row.inventory.inboundReceivingQuantity }}</div>
              <div>预留数量: {{ row.inventory.reservedQuantity }}</div>
              <div>不可售数量: {{ row.inventory.unfulfillableQuantity }}</div>
              <div>调查中库存: {{ row.inventory.researchingQuantity }}</div>
            </template>

            <span class="underline decoration-dashed underline-offset-4">{{ row.inventory.fulfillableQuantity }} </span>
          </el-tooltip>
          <span v-else>--</span>

        </template>

      </el-table-column>
      <el-table-column label="价格" align="center">
        <template #default="{ row }">
          <div v-if="row.price && row.price.length > 0">

            <el-tooltip effect="dark" placement="top">
              <template #content>

                <div v-for="(val, idx) in row.price" :key="idx">
                  {{ val.salePrice / 100 }} {{ val.currency }} [{{ formatToDate(val.startAt * 1000) }}, {{
                    val.endAt ? formatToDate(val.endAt * 1000) : '至今' }}]
                </div>

              </template>



              <span class="underline decoration-dashed underline-offset-4">{{ row.price[0].salePrice / 100 }}
                {{ row.price[0].currency }}</span>
            </el-tooltip>



          </div>
          <span v-else>--</span>
        </template>

      </el-table-column>
      <el-table-column label="费用(预估)" align="center">
        <template #default="{ row }">
          <div v-if="row.fee">
            <div>销售价: {{ row.fee.listingPrice.amount }} {{ row.fee.listingPrice.currencyCode }}</div>
            <div v-if="row.fee.salePrice">促销价: {{ row.fee.salePrice.amount }} {{
              row.fee.salePrice.currencyCode }}</div>
          </div>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="statusName" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <!-- <el-table-column label="更新时间" align="center" prop="updateTime" :formatter="dateFormatter" width="180px" /> -->
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button link type="primary" v-hasPermi="['erplus:cross-product:update']" @click="handleDetail(scope.row)">
            详情
          </el-button>
          <el-button link type="danger" v-hasPermi="['erplus:cross-product:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination :total="total" v-model:page="queryParamCopy.pageNo" v-model:limit="queryParamCopy.pageSize"
      @pagination="getList" />
  </ContentWrap>


</template>


<script setup lang="ts">

import { dateFormatter } from '@/utils/formatTime'
import { formatToDate } from '@/utils/dateUtil'
import * as CrossListingApi from '@/app/erplus/api/product/listing'

defineOptions({ name: 'AmazonListingList' })

const props = defineProps({
  platformId: {
    type: Number,
    required: true
  },
  queryParams: {
    type: Object,
    required: true
  }
})
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

// const { query } = useRoute()

const loading = ref(false) // 列表的加载中
const syncLoading = ref(false) // 同步的加载中
const list = ref<[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数


const queryParamCopy = ref<any>({})


const getList = async () => {
  loading.value = true

  try {
    // 获取数据的逻辑
    const data = await CrossListingApi.queryCrossProductListingPage(queryParamCopy.value)


    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const search = async () => {
  await getList()
}

defineExpose({ search })



const syncListing = async () => {

  try {
    syncLoading.value = true
    const param = Object.assign({}, queryParamCopy.value)
    await CrossListingApi.syncProductListing(param)
    message.success('同步任务已提交，请稍后刷新列表查看最新数据')
    // await getList()
  } finally {
    syncLoading.value = false
  }
}

const syncProdusts = async () => {

  const param = {
    productIds: selectedProduct.value
  }
  await CrossListingApi.syncProductListing(param)
  message.success('同步任务已提交，请稍后刷新列表查看最新数据')
}


/** 将传进来的值赋值给 formData */
watch(
  () => props.queryParams,
  (data) => {
    if (!data) {
      return
    }
    queryParamCopy.value = Object.assign({}, data)

  },
  {
    immediate: true,
    deep: true
  }
)

const handleDetail = async (row: any) => {
  const productDetail = await CrossListingApi.getProductListingDetail(row.id)
  console.log('productDetail', productDetail)
}



const selectedProduct = ref<any[]>([])
const handleSelect = (selection: any[]) => {
  selectedProduct.value = selection.map(item => item.id)
}





</script>