<template>
  <div class="h-full flex flex-col overflow-hidden">
    <div class="flex justify-between items-center mb-10px flex-shrink-0">
      <div class="flex gap-2">
        <el-input v-model="queryParams.keyword" placeholder="搜索产品名称/编号" clearable class="!w-240px" size="small"
          @input="handleSearch">
          <template #prefix>
            <Icon icon="ep:search" />
          </template>
        </el-input>
      </div>
      <div>
        <el-button size="small">
          <Icon icon="ep:plus" class="mr-5px" />
          入库
        </el-button>
        <el-button size="small">
          <Icon icon="ep:upload" class="mr-5px" />
          出库
        </el-button>
        <el-button size="small">
          <Icon icon="ep:refresh" class="mr-5px" />
          调拨
        </el-button>
        <el-button type="success" size="small" v-if="warehouseType === 0">
          <Icon icon="ep:upload" class="mr-5px" />
          发货
        </el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" class="flex-1"
      height="100%">
      <el-table-column label="图片" align="center" width="80">
        <template #default="scope">
          <el-image :src="scope.row.image" class="w-48px h-48px border-rounded" preview-teleported
            :preview-src-list="[scope.row.image]" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="SKU / 商品信息" align="left" prop="sellerSku" min-width="250">
        <template #default="{ row }">
          <div class="product-info-cell group">
            <div class="mb-2px line-clamp-1 text-gray-700 font-bold">
              <span>{{ row.sellerSku }}</span>
            </div>
            <div class="flex items-center gap-5px text-gray-400 text-xs truncate">
              <span>{{ row.name || 'Fun & Chic 17cm Doll Clothes,Pink Princess Dress with Sequin & Pom-Pom Bow'
                }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="可用库存" align="center" prop="totalCount" width="100">
        <template #default="scope">
          <span :class="{ 'text-red-500': scope.row.totalCount < 10 }" class="font-bold">
            {{ scope.row.totalCount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="入库中" align="center" prop="inboundingCount" width="100" />
      <el-table-column label="出库中" align="center" prop="outboundingCount" width="100" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180" :formatter="dateFormatter" />
    </el-table>

    <Pagination class="justify-end mt-10px" :total="total" v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'
import { Icon } from '@/components/Icon'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'WarehouseStockTable' })

const props = defineProps<{
  warehouseId: number | null
  warehouseType?: number
}>()

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  keyword: undefined,
})

const getList = async () => {
  if (!props.warehouseId) return
  loading.value = true
  try {
    const ret = await WarehouseApi.getWarehouseInventoryPage({
      ...queryParams,
      warehouseId: props.warehouseId
    })
    list.value = ret.list
    total.value = ret.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

watch(() => props.warehouseId, (newId) => {
  if (newId) {
    queryParams.pageNo = 1
    queryParams.keyword = undefined
    getList()
  }
})

onMounted(() => {
  getList()
})
</script>

<style scoped>
.product-info-cell:hover .opacity-0 {
  opacity: 1;
}
</style>
