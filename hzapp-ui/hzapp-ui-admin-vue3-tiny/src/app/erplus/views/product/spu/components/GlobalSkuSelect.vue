<template>
  <Dialog :title="title" v-model="dialogVisible" width="70%" :scroll="false" maxHeight="600px">
    <div class="flex flex-col h-550px">
      <div class="mb-10px flex gap-10px">
        <el-input
          v-model="queryParams.searchValue"
          placeholder="请输入名称/编码"
          clearable
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
        <el-button color="#333" class="!bg-black !text-white" @click="handleQuery">
          <Icon icon="ep:search" />
          查询
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        row-key="id"
        class="flex-1"
        height="100%"
        show-overflow-tooltip
        @row-click="handleRowClick"
      >
        <el-table-column label="#" width="55">
          <template #default="{ row }">
            <el-radio :label="row.id" v-model="selectedSkuId" @change="handleSelected(row)"
              >&nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.picUrl"
              :src="row.picUrl"
              class="h-40px w-40px rounded"
              :preview-src-list="[row.picUrl]"
              preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column label="SKU 编码" prop="code" min-width="120" />
        <el-table-column label="商品名称" prop="spuName" min-width="200" />
        <el-table-column label="价格" align="center" width="100">
          <template #default="{ row }">
            {{ fenToYuan(row.price) }}
          </template>
        </el-table-column>
      </el-table>

      <Pagination
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
        class="mt-10px justify-end"
      />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :disabled="!selectedSku" @click="handleConfirm">确 定</el-button>
      </div>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getSkuPage, ProductSkuPageReqVO } from '@/app/erplus/api/product/spu'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'GlobalSkuSelect' })

const props = defineProps({
  title: {
    type: String,
    default: '选择目标 SKU'
  }
})

const emit = defineEmits(['change'])

const dialogVisible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const selectedSkuId = ref()
const selectedSku = ref<any>(null)

const queryParams = reactive<any>({
  pageNo: 1,
  pageSize: 10,
  searchValue: '',
  status: 1 // 只查已发布
})

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  selectedSkuId.value = undefined
  selectedSku.value = null
  handleQuery()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await getSkuPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 行点击处理 */
const handleRowClick = (row: any) => {
  selectedSkuId.value = row.id
  selectedSku.value = row
}

/** 选中后处理 (Radio 变更) */
const handleSelected = (row: any) => {
  selectedSku.value = row
}

/** 确定按钮操作 */
const handleConfirm = () => {
  if (!selectedSku.value) return
  emit('change', selectedSku.value)
  dialogVisible.value = false
}

defineExpose({ open })
</script>
