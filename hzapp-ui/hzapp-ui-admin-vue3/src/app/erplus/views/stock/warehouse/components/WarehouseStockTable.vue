<template>
  <div class="h-full flex flex-col overflow-hidden">
    <!-- 顶部工具栏 -->
    <div class="flex justify-between items-center mb-10px flex-shrink-0">
      <!-- 左侧：搜索 -->
      <div class="flex items-center gap-2">
        <el-input v-model="queryParams.keyword" placeholder="搜索产品名称/编号" clearable class="!w-240px" size="small"
          @keyup.enter="handleSearch">
          <template #prefix>
            <Icon icon="ep:search" />
          </template>
        </el-input>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
      </div>

      <!-- 右侧：操作按钮 -->
      <div v-if="warehouseId" class="flex gap-2">
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
        <el-button type="success" size="small" v-if="warehouse?.type === 0">
          <Icon icon="ep:upload" class="mr-5px" />
          发货
        </el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <template v-if="warehouseId">
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
    </template>

    <el-empty v-else description="请从左侧选择一个仓库以查看库存" class="flex-1" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'
import { dateFormatter } from '@/utils/formatTime'
import { Icon } from '@/components/Icon'

defineOptions({ name: 'WarehouseStockTable' })

const props = defineProps<{
  warehouseId: number | null
  warehouse: any
}>()

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  keyword: undefined as string | undefined,
})

const getList = async () => {
  if (!props.warehouseId) {
    list.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    const params = {
      ...queryParams,
      warehouseId: props.warehouseId
    }
    const ret = await WarehouseApi.getWarehouseInventoryPage(params)
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

// 监听仓库ID变化
watch(() => props.warehouseId, (newVal) => {
  if (newVal) {
    queryParams.pageNo = 1
    queryParams.keyword = undefined
    getList()
  } else {
    list.value = []
    total.value = 0
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
