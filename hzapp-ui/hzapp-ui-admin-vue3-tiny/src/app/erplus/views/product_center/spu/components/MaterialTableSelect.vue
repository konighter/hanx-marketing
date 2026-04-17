<template>
  <Dialog v-model="dialogVisible" :appendToBody="true" title="选择耗材/配件" width="800">
    <div class="mb-4 flex items-center gap-3">
      <el-input
        v-model="queryParams.name"
        placeholder="搜索耗材名称"
        clearable
        class="!w-60"
        @keyup.enter="getList"
      />
      <el-select v-model="queryParams.category" placeholder="请选择类型" clearable class="!w-40">
        <el-option label="包装材料" value="packaging" />
        <el-option label="填充材料" value="filling" />
        <el-option label="标签贴纸" value="label" />
        <el-option label="配件/零件" value="accessory" />
        <el-option label="其他" value="other" />
      </el-select>
      <el-button @click="getList">
        <Icon icon="ep:search" class="mr-1" /> 查询
      </el-button>
    </div>

    <el-table v-loading="loading" :data="list" show-overflow-tooltip @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="名称" prop="name" min-width="150" />
      <el-table-column label="编码" prop="code" min-width="120" />
      <el-table-column label="分类" prop="category" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.category === 'packaging'" size="small">包装材料</el-tag>
          <el-tag v-else-if="row.category === 'filling'" type="success" size="small">填充材料</el-tag>
          <el-tag v-else-if="row.category === 'label'" type="info" size="small">标签贴纸</el-tag>
          <el-tag v-else-if="row.category === 'accessory'" type="warning" size="small">配件/零件</el-tag>
          <el-tag v-else-if="row.category === 'other'" type="info" size="small">其他</el-tag>
          <el-tag v-else-if="row.category" size="small">{{ row.category }}</el-tag>
          <span v-else>通用</span>
        </template>
      </el-table-column>
      <el-table-column label="单位" prop="unit" width="80">
        <template #default="{ row }">
          {{ unitMap[row.unit] || row.unit || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="规格" min-width="120">
        <template #default="{ row }">
          <span v-if="row.length" class="text-xs text-gray-500">
            {{ row.length }}x{{ row.width }}x{{ row.height }} cm
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="mt-4 flex justify-end">
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </div>

    <template #footer>
      <div class="flex justify-end gap-2">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :disabled="selection.length === 0">
          确认选择 ({{ selection.length }})
        </el-button>
      </div>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { MaterialApi } from '@/app/erplus/api/material'
import { ProductUnitApi } from '@/app/erplus/api/product/unit'

defineOptions({ name: 'MaterialTableSelect' })

const dialogVisible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const selection = ref([])
const unitMap = ref<Record<number, string>>({})

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  category: ''
})

const emits = defineEmits(['success'])

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await MaterialApi.getMaterialPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取单位列表 */
const getUnitList = async () => {
  const data = await ProductUnitApi.getProductUnitSimpleList()
  unitMap.value = data.reduce((map: any, unit: any) => {
    map[unit.id] = unit.name
    return map
  }, {})
}

/** 选择变化 */
const handleSelectionChange = (val: any[]) => {
  selection.value = val
}

/** 确认选择 */
const handleConfirm = () => {
  emits('success', [...selection.value])
  dialogVisible.value = false
}

/** 打开弹窗 */
const open = () => {
  dialogVisible.value = true
  getList()
  getUnitList()
}

defineExpose({ open })
</script>
