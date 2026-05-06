<template>
  <el-dialog
    v-model="visible"
    title="选择推广商品"
    width="900px"
    append-to-body
    destroy-on-close
  >
    <div class="mb-20px flex justify-between">
      <el-input
        v-model="queryParams.keyword"
        placeholder="搜索 SKU / ASIN / 标题"
        class="!w-300px"
        clearable
        @keyup.enter="handleQuery"
      >
        <template #append>
          <el-button @click="handleQuery">
            <Icon icon="ep:search" />
          </el-button>
        </template>
      </el-input>
      <div v-if="multiple" class="text-14px text-gray-500">
        已选择 <span class="text-blue-500 font-bold">{{ selection.length }}</span> 个商品
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="list"
      @selection-change="handleSelectionChange"
      height="450px"
      ref="tableRef"
      row-key="id"
    >
      <el-table-column v-if="multiple" type="selection" width="55" reserve-selection />
      <el-table-column label="图片" width="80">
        <template #default="scope">
          <el-image
            :src="scope.row.mainImage?.url || scope.row.mainImage"
            class="w-50px h-50px rounded"
            fit="cover"
          >
            <template #error>
              <div class="w-full h-full bg-gray-100 flex items-center justify-center">
                <Icon icon="ep:picture" class="text-gray-400" />
              </div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" min-width="250">
        <template #default="scope">
          <div class="font-bold text-14px line-clamp-2 mb-5px" :title="scope.row.title">
            {{ scope.row.title }}
          </div>
          <div class="flex gap-10px">
            <el-tag size="small" type="info">SKU: {{ scope.row.sellerProductCode || scope.row.sellerSku }}</el-tag>
            <el-tag size="small" type="success" v-if="scope.row.platformProductCode || scope.row.asin">
              商品ID: {{ scope.row.platformProductCode || scope.row.asin }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.statusName === 'Active' || scope.row.status === 'Active' ? 'success' : 'info'" size="small">
            {{ scope.row.statusName || scope.row.status || 'Unknown' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="!multiple" label="操作" width="100" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="handleSelectSingle(scope.row)">选择</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="mt-20px flex justify-end">
      <el-pagination
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="getList"
      />
    </div>

    <template #footer>
      <div class="flex justify-end gap-10px">
        <el-button @click="visible = false">取消</el-button>
        <el-button v-if="multiple" type="primary" :disabled="selection.length === 0" @click="handleConfirmMultiple">
          确认选择 ({{ selection.length }})
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { queryCrossProductListingPage } from '@/app/erplus/api/product/listing'
import { ElMessage } from 'element-plus'

const props = defineProps({
  multiple: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['confirm'])

const visible = ref(false)
const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const selection = ref<any[]>([])
const tableRef = ref()

const queryParams = reactive({
  shopId: undefined as number | undefined,
  keyword: '',
  pageNo: 1,
  pageSize: 10
})

/** 打开弹窗 */
const open = (shopId: number) => {
  if (!shopId) {
    ElMessage.warning('请先选择店铺')
    return
  }
  queryParams.shopId = shopId
  queryParams.pageNo = 1
  queryParams.keyword = ''
  selection.value = []
  visible.value = true
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const res = await queryCrossProductListingPage(queryParams)
    list.value = res.list || []
    total.value = res.total || 0
  } catch (error) {
    console.error('Failed to fetch products:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const handleSelectionChange = (rows: any[]) => {
  selection.value = rows
}

const handleSelectSingle = (row: any) => {
  emit('confirm', row)
  visible.value = false
}

const handleConfirmMultiple = () => {
  emit('confirm', selection.value)
  visible.value = false
}

defineExpose({ open })
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
