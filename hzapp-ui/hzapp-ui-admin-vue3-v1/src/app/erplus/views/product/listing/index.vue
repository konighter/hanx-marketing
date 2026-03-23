<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="100px" :rules="rules">
      <el-form-item label="跨境平台" prop="platformId">
        <el-select v-model="queryParams.platformId" placeholder="请选择跨境平台" clearable class="!w-240px"
          @change="platformChange">
          <el-option v-for="p in platforms" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable class="!w-240px"
          :disabled="!queryParams.platformId">
          <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="市场" prop="marketId">

        <el-select v-model="queryParams.marketId" placeholder="请选择市场" clearable class="!w-240px"
          :disabled="!queryParams.shopId">
          <el-option v-for="r in regions" :key="r.id" :label="r.zoneName" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="配送类型" prop="fulfillType">
        <el-select v-model="queryParams.fulfillType" placeholder="请选择配送类型" clearable class="!w-240px">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="本地SKU" prop="sellerProductCode">
        <el-input v-model="queryParams.sellerProductCode" placeholder="请输入关联本地商品SKU" clearable
          @keyup.enter="handleQuery" class="!w-240px" />
      </el-form-item>
      <el-form-item label="平台商品ID" prop="platformProductCode">
        <el-input v-model="queryParams.platformProductCode" placeholder="请输入平台商品ID" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="商品名称" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入商品名称" clearable class="!w-240px" />
      </el-form-item>

      <el-form-item label="商品状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择商品状态" clearable class="!w-240px">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTimeRange">
        <el-date-picker v-model="queryParams.createTimeRange" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>
      <el-form-item label="更新时间" prop="createTimeRange">
        <el-date-picker v-model="queryParams.updateTimeRange" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>
      <div class="flex justify-center">
        <el-form-item>
          <el-button @click="handleQuery" type="primary">
            <Icon icon="ep:search" class="mr-5px" /> 搜索
          </el-button>
          <el-button @click="handleSync" :loading="syncLoading">
            <Icon icon="ep:search" class="mr-5px" /> 同步产品
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
    <component ref="tableRef" :is="tableComponent" v-bind="tableProps" />

  </ContentWrap>



</template>


<script setup lang="ts">

import { dateFormatter } from '@/utils/formatTime'
import { isEmpty } from '@/utils/is'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform' // 已存在模块
import { ShopApi } from '@/app/erplus/api/system/shop'
import AmazonList from './components/AmazonListingList.vue'

import * as CrossListingApi from '@/app/erplus/api/product/listing'


// import { CrossProductApi, CrossProduct } from '@/api/erplus/crossproduct'


/** 跨境商品信息SKU层次 列表 */
defineOptions({ name: 'CrossListing' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const syncLoading = ref(false) // 同步的加载中
const tableVisiable = ref(false) // 列表的显示与否
const list = ref<[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  platformId: undefined,
  shopId: undefined,
  marketId: undefined,
  fulfillType: undefined,
  sellerProductCode: undefined,
  platformProductCode: undefined,
  title: undefined,
  // categoryId: undefined,
  // brand: undefined,

  status: undefined,
  createTimeRange: [],
  updateTimeRange: []
})
const queryFormRef = ref() // 搜索的表单

const rules = reactive({
  platformId: [{ required: true, message: '请选择跨境平台', trigger: 'change' }],
  shopId: [{ required: true, message: '请选择店铺', trigger: 'change' }],
  // marketId: [{ required: true, message: '请选择区域', trigger: 'change' }],
  // fulfillType: [{ required: true, message: '请选择配送类型', trigger: 'change' }],
})




const tableProps = ref({})

const tableRef = ref()
const tableComponent = ref()
tableComponent.value = AmazonList

const tableComponents = {

  // 'Amazon': {
  //   comp: AmazonList,
  //   query: function () {

  //   }
  // }

}




const platforms = ref<SellPlatformVO[]>([])
const shops = ref<any[]>([])
const regions = ref<any[]>([])

/** 平台相关加载 */
const loadPlatforms = async () => {
  platforms.value = await SellPlatformApi.getSellPlatformListCache() || []
}

const loadShops = async (platformId: number) => {
  // 加载店铺列表 
  shops.value = await ShopApi.getPlatformShop(platformId) || []
  console.log('shops', shops.value)
}

const platformChange = async function () {
  // 重置店铺和区域
  queryParams.shopId = undefined
  queryParams.marketId = undefined


  if (queryParams.platformId) {
    await loadShops(queryParams.platformId)
  } else {
    shops.value = []
    regions.value = []
  }

  // 重置列表区域
  tableVisiable.value = false

  const platform = platforms.value.filter(p => p.id === queryParams.platformId)[0]
  console.log('platform', platform)


  tableProps.value = {
    platformId: platform.id,
    queryParams: queryParams
  }

  // if (platform && !isEmpty(tableComponents[platform.code])) {
  //   // tableComponent.value = tableComponents[platform.code].comp
  //   // tableProps.value = {
  //   //   platformId: platform.id,
  //   //   queryParams: queryParams
  //   // }
  // } else {
  //   tableComponent.value = null
  //   tableProps.value = {}
  //   message.warning('该平台暂无对应的商品列表展示')
  // }






}






onMounted(async () => {
  await loadPlatforms()
})



const handleQuery = async () => {
  tableVisiable.value = true
  tableRef.value.search()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
}

const handleSync = async () => {
  try {
    syncLoading.value = true
    await CrossListingApi.syncProductListing(queryParams)
    message.success('同步任务已提交，请稍后刷新列表查看最新数据')
  } finally {
    syncLoading.value = false
  }
}

</script>


<style scoped></style>
