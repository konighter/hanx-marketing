<template>
  <div class="product-material-info">
    <div class="section-header mb-6">
      <div class="section-title">配件 & 耗材 (BOM)</div>
    </div>

    <div class="material-container bg-white p-6 rounded-lg border border-gray-100">
      <div class="flex justify-between items-center mb-4">
        <div class="text-xs text-gray-500">
          <Icon icon="ep:info-filled" class="mr-1" />
          定义生产该商品（SKU）时需要配套消耗的包装材料或配件明细。
        </div>
        <div class="flex items-center gap-4">
          <el-input
            v-model="keyword"
            placeholder="搜索耗材名称或编码"
            clearable
            class="!w-64"
          >
            <template #prefix>
              <Icon icon="ep:search" />
            </template>
          </el-input>
          <el-button type="primary" plain @click="handleOpenSelect">
            <Icon icon="ep:plus" class="mr-1" />
            添加耗材/配件
          </el-button>
        </div>
      </div>

      <el-table :data="paginatedData" border style="width: 100%" class="material-table">
        <el-table-column label="耗材名称" prop="materialName" min-width="200" />
        <el-table-column label="耗材编码" prop="materialCode" width="150" />
        <el-table-column label="单位" prop="materialUnit" width="100" align="center" />
        <el-table-column label="单产用量" width="180" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.usageQuantity"
              :min="0.01"
              :precision="2"
              size="small"
              class="!w-32"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">
              <Icon icon="ep:delete" />
            </el-button>
          </template>
        </el-table-column>

        <template #empty>
          <div class="empty-placeholder py-12 flex flex-col items-center justify-center">
            <Icon icon="ep:document" class="text-4xl text-gray-200 mb-2" />
            <span class="text-sm text-gray-400">暂无关联耗材，点击右上方按钮添加</span>
          </div>
        </template>
      </el-table>

      <!-- 分页 -->
      <Pagination
        v-model:page="currentPage"
        v-model:limit="pageSize"
        :total="filteredData.length"
        layout="total, prev, pager, next"
        :pager-count="5"
        small
      />
    </div>

    <!-- 耗材选择器 -->
    <MaterialTableSelect ref="materialSelectRef" @success="onMaterialSelectSuccess" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import MaterialTableSelect from '../components/MaterialTableSelect.vue'

defineOptions({ name: 'ProductMaterialInfo' })

const modelValue = defineModel<any[]>({ required: true, default: () => [] })

const materialSelectRef = ref()

// 搜索与分页状态
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 过滤后的数据
const filteredData = computed(() => {
  if (!keyword.value) return modelValue.value
  const lowSearch = keyword.value.toLowerCase()
  return modelValue.value.filter(
    (item) =>
      item.materialName?.toLowerCase().includes(lowSearch) ||
      item.materialCode?.toLowerCase().includes(lowSearch)
  )
})

// 计算分页后的数据
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredData.value.slice(start, end)
})

// 监听关键词变化，重置页码
watch(keyword, () => {
  currentPage.value = 1
})

const handleOpenSelect = () => {
  materialSelectRef.value.open()
}

const onMaterialSelectSuccess = (selection: any[]) => {
  selection.forEach((item) => {
    // 避免重复添加
    if (!modelValue.value.some((m) => m.materialId === item.id)) {
      modelValue.value.push({
        materialId: item.id,
        materialName: item.name,
        materialCode: item.code,
        materialUnit: item.unit,
        usageQuantity: 1.00
      })
    }
  })
}

const handleDelete = (row: any) => {
  const index = modelValue.value.findIndex(item => item.materialId === row.materialId)
  if (index !== -1) {
    modelValue.value.splice(index, 1)
    // 如果删除后当前页没数据了，回到上一页
    if (paginatedData.value.length === 0 && currentPage.value > 1) {
      currentPage.value--
    }
  }
}
</script>

<style scoped lang="scss">
.product-material-info {
  .section-header {
    display: flex;
    align-items: center;
    border-left: 3px solid #3b82f6;
    padding-left: 12px;
    height: 16px;
    
    .section-title {
      font-size: 14px;
      font-weight: 700;
      color: #1a202c;
    }
  }

  .material-table {
    :deep(.el-table__header-wrapper) {
      th {
        background-color: #f8fafc;
        color: #4a5568;
        font-weight: 600;
        font-size: 12px;
      }
    }
  }
}
</style>
