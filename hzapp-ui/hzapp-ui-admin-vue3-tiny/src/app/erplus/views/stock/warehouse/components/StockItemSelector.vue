<template>
  <Dialog :title="title" v-model="dialogVisible" width="60%" :scroll="false" maxHeight="600px">
    <div class="flex flex-col min-h-400px">
      <!-- 搜索与类型切换 -->
      <div class="mb-15px flex justify-between items-center">
        <el-radio-group v-model="itemType" @change="handleTypeChange" class="mr-15px">
          <el-radio-button :label="ErpItemTypeEnum.SKU">商品 SKU</el-radio-button>
          <el-radio-button :label="ErpItemTypeEnum.MATERIAL">耗材 Material</el-radio-button>
        </el-radio-group>
        
        <div class="flex gap-2 flex-1 max-w-400px">
          <el-input
            v-model="queryParams.searchValue"
            :placeholder="itemType === ErpItemTypeEnum.SKU ? '搜索 SKU 编码/商品名' : '搜索耗材名称/编码'"
            clearable
            @keyup.enter="handleQuery"
          >
            <template #prefix>
              <Icon icon="ep:search" />
            </template>
          </el-input>
          <el-button color="#333" class="!bg-black !text-white" @click="handleQuery">查询</el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="list"
        height="400px"
        @selection-change="handleSelectionChange"
        v-if="multiple"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="图片" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.picUrl || row.image"
              :src="row.picUrl || row.image"
              class="h-40px w-40px rounded border"
              preview-teleported
            />
            <div v-else class="h-40px w-40px rounded border bg-gray-50 flex items-center justify-center">
              <Icon icon="ep:picture" class="text-gray-300" />
            </div>
          </template>
        </el-table-column>
        <el-table-column 
          :label="itemType === ErpItemTypeEnum.SKU ? 'SKU 编码' : '耗材编码'" 
          :prop="itemType === ErpItemTypeEnum.SKU ? 'code' : 'code'" 
          min-width="150" 
        />
        <el-table-column 
          :label="itemType === ErpItemTypeEnum.SKU ? '商品名称' : '耗材名称'" 
          :prop="itemType === ErpItemTypeEnum.SKU ? 'spuName' : 'name'" 
          min-width="200" 
        />
        <el-table-column v-if="warehouseId" label="可用库存" prop="totalCount" width="100" align="center">
          <template #default="{ row }">
            <span class="font-bold text-orange-600">{{ row.totalCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="单位" prop="unit" width="80" align="center" />
      </el-table>

      <el-table
        v-loading="loading"
        :data="list"
        height="400px"
        @row-click="handleRowClick"
        v-else
      >
        <el-table-column label="选择" width="55" align="center">
          <template #default="{ row }">
            <el-radio :label="row.id" v-model="selectedId">&nbsp;</el-radio>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.picUrl || row.image"
              :src="row.picUrl || row.image"
              class="h-40px w-40px rounded border"
            />
            <div v-else class="h-40px w-40px rounded border bg-gray-50 flex items-center justify-center">
              <Icon icon="ep:picture" class="text-gray-300" />
            </div>
          </template>
        </el-table-column>
        <el-table-column 
          :label="itemType === ErpItemTypeEnum.SKU ? 'SKU 编码' : '耗材编码'" 
          :prop="itemType === ErpItemTypeEnum.SKU ? 'code' : 'code'" 
          min-width="150" 
        />
        <el-table-column 
          :label="itemType === ErpItemTypeEnum.SKU ? '商品名称' : '耗材名称'" 
          :prop="itemType === ErpItemTypeEnum.SKU ? 'spuName' : 'name'" 
          min-width="200" 
        />
      </el-table>

      <!-- 分页 -->
      <div class="mt-15px flex justify-end">
        <el-pagination
          v-model:current-page="queryParams.pageNo"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </div>
    
    <template #footer>
      <div class="flex justify-end gap-2">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :disabled="multiple ? selectedItems.length === 0 : !selectedId" @click="handleConfirm">
          确 定
        </el-button>
      </div>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getSkuPage } from '@/app/erplus/api/product/spu'
import { MaterialApi } from '@/app/erplus/api/material'
import { WarehouseApi } from '@/app/erplus/api/stock/warehouse'
import { ErpItemTypeEnum } from './constants'

defineOptions({ name: 'StockItemSelector' })

const props = defineProps({
  title: { type: String, default: '选择入库明细' },
  multiple: { type: Boolean, default: true }
})

const emit = defineEmits(['change'])

const dialogVisible = ref(false)
const loading = ref(false)
const itemType = ref(ErpItemTypeEnum.SKU)
const list = ref<any[]>([])
const total = ref(0)
const selectedId = ref()
const selectedItems = ref<any[]>([])
const warehouseId = ref<number | string>()

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  searchValue: '',
  status: 1
})

const open = (options?: { type?: number, warehouseId?: number | string }) => {
  if (options?.type) itemType.value = options.type
  warehouseId.value = options?.warehouseId
  dialogVisible.value = true
  selectedId.value = undefined
  selectedItems.value = []
  handleQuery()
}

const handleTypeChange = () => {
  handleQuery()
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    let res: any
    if (warehouseId.value) {
      // 仓库模式：直接从仓库库存 Page 接口查已有的数据
      res = await WarehouseApi.getWarehouseInventoryPage({
        pageNo: queryParams.pageNo,
        pageSize: queryParams.pageSize,
        keyword: queryParams.searchValue,
        warehouseId: warehouseId.value,
        itemType: itemType.value // 依然支持区分 SKU/耗材 来过滤展示
      })
      // 抹平字段差异
      res.list = res.list.map(i => ({
        ...i,
        id: i.itemId, // 转换成物料 ID
        code: i.sellerSku,
        spuName: i.name, // 对应到统一展示字段
        picUrl: i.image
      }))
    } else if (itemType.value === ErpItemTypeEnum.SKU) {
      res = await getSkuPage({
        pageNo: queryParams.pageNo,
        pageSize: queryParams.pageSize,
        searchValue: queryParams.searchValue,
        status: 1
      })
    } else {
      res = await MaterialApi.getMaterialPage({
        pageNo: queryParams.pageNo,
        pageSize: queryParams.pageSize,
        name: queryParams.searchValue,
        status: 1
      })
    }
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (val: any[]) => {
  selectedItems.value = val
}

const handleRowClick = (row: any) => {
  if (!props.multiple) {
    selectedId.value = row.id
    selectedItems.value = [row]
  }
}

const handleConfirm = () => {
  const result = selectedItems.value.map(item => ({
    itemType: itemType.value,
    itemId: item.id,
    sellerSku: item.code,
    name: itemType.value === ErpItemTypeEnum.SKU ? item.spuName : item.name,
    picUrl: itemType.value === ErpItemTypeEnum.SKU ? item.picUrl : item.picUrl,
    code: item.code,
    availableStock: item.totalCount || 0 // 带出库存量
  }))
  emit('change', result)
  dialogVisible.value = false
}

defineExpose({ open })
</script>
