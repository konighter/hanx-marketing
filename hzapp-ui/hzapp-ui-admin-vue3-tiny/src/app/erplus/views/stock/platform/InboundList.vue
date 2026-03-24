<template>

  <ContentWrap>

    <el-form :model="queryForm" ref="queryFormRef" label-width="100px" class="mb-4" :inline="true">
      <el-form-item label="跨境平台" prop="platformId">
        <el-select
v-model="queryForm.platformId" placeholder="请选择跨境平台" clearable class="!w-240px"
          @change="handlePlatformChange">
          <el-option v-for="p in platforms" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="店铺" prop="shopId">
        <el-select
v-model="queryForm.shopId" placeholder="请选择店铺" clearable class="!w-240px"
          :disabled="!queryForm.platformId" @change="handleShopChange">
          <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="市场" prop="marketId">
        <el-select
v-model="queryForm.marketId" placeholder="请选择市场" clearable class="!w-240px"
          :disabled="!queryForm.shopId">
          <el-option v-for="r in markets" :key="r.id" :label="r.zoneName" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
      </el-form-item>
    </el-form>


  </ContentWrap>


  <ContentWrap>
    <!-- 列表 -->
    <el-table row-key="id" v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="ID" align="center" prop="planId" />
      <el-table-column label="仓储配置" align="center">
        <template #default="scope">
          {{ JSON.stringify(scope.row.placementOptions) }}
        </template>

      </el-table-column>
      <el-table-column label="运输方式" align="center">
        <template #default="scope">
          {{ JSON.stringify(scope.row.shipments) }}
        </template>

      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="操作" align="center" width="400">
        <template #default="scope">
          <el-button type="text" size="small" @click="generatePlacementOptions(scope.row)">生成仓储配置</el-button>
          <el-button type="text" size="small" @click="listPlacementOptions(scope.row)">查询分仓配置信息</el-button>
          <el-button type="text" size="small" @click="generatePackingOptions(scope.row)">生成包装配置</el-button>

        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
:total="total" v-model:page="queryForm.pageNo" v-model:limit="queryForm.pageSize"
      @pagination="handleQuery" />
  </ContentWrap>

</template>



<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import { Icon } from '@/components/Icon'

import { SellPlatformApi } from '@/app/erp/api/sellplatform' // 已存在模块
import { ShopApi } from '@/app/erplus/api/system/shop'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'

const queryFormRef = ref()
const queryForm = reactive({
  pageNo: 1,
  pageSize: 10,
  platformId: undefined,
  shopId: undefined,
  marketId: undefined
})

const loading = ref(false)
const list = ref([])
const total = ref(0) // 列表的总页数

const platforms = ref([])

const shops = ref([])

const markets = ref([])


const handleQuery = async () => {

  loading.value = true
  try {
    const data = await AmzInboundApi.listInboundPlansPage(queryForm) || []
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryFormRef.value.resetFields()
}


onMounted(async () => {
  await loadPlatforms()
})


/** 平台相关加载 */
const loadPlatforms = async () => {
  platforms.value = await SellPlatformApi.getSellPlatformListCache() || []
}

const loadShops = async (platformId: number) => {
  // 示例 API，替换为真实实现
  try {
    shops.value = await ShopApi.getPlatformShop(platformId) || []
  } catch {
    shops.value = []
  }
}

/** 事件处理 */
const handlePlatformChange = async () => {
  // 平台切换时清空店铺、品类与属性
  queryForm.shopId = undefined
  queryForm.marketId = undefined
  await loadShops(queryForm.platformId)
}

/** 当店铺变更后，只有在已选平台且至少选中一个店铺时才加载品类 */
const handleShopChange = async (shopIds: number[]) => {
  // 重置区域
  queryForm.marketId = undefined

}

const generatePlacementOptions = async (row: any) => {
  // 打开入库详情
  console.log('生成仓储配置', row)


  await AmzInboundApi.generatePlacementOptions({
    shopId: row.shopId,
    planId: row.planId
  })


}

const listPlacementOptions = async (row: any) => {
  // 打开入库详情
  console.log('查询分仓配置信息', row)


  await AmzInboundApi.listPlacementOptions({
    shopId: row.shopId,
    planId: row.planId
  })


}


const generatePackingOptions = async (row: any) => {
  // 打开入库详情
  console.log('生成包装配置', row)


  await AmzInboundApi.generatePackingOptions({
    shopId: row.shopId,
    planId: row.planId
  })

}









</script>