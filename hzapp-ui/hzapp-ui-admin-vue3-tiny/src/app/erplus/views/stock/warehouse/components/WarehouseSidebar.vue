<template>
  <ContentWrap title="仓库列表" class="!mb-0 h-full">
    <template #header>
      <div class="flex justify-end w-full">
        <el-button type="primary" size="small" @click="handleAdd">
          <Icon icon="ep:plus" class="mr-5px" />
          新增
        </el-button>
      </div>
    </template>
    <div class="flex-1 flex flex-col overflow-hidden h-0">
      <!-- 搜索栏 -->
      <div class="flex gap-5px mb-10px flex-shrink-0">
        <el-input v-model="queryParams.name" placeholder="搜仓库名称" clearable @input="handleSearch" class="flex-1">
          <template #prefix>
            <Icon icon="ep:search" />
          </template>
        </el-input>

        <el-popover placement="bottom-end" :width="280" trigger="click">
          <template #reference>
            <el-button link>
              <Icon icon="ep:more-filled" />
            </el-button>
          </template>
          <el-form :model="queryParams" label-width="80px" size="small">
            <el-form-item label="仓库类型">
              <el-select v-model="queryParams.type" placeholder="请选择类型" clearable @change="handleSearch">
                <el-option v-for="item in WarehouseTypes" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-popover>
      </div>

      <!-- 列表滚动区 -->
      <el-scrollbar class="flex-1 overflow-hidden">
        <div v-loading="loading" class="pr-10px">
          <div
v-for="item in list" :key="item.id"
            class="warehouse-item p-10px mb-5px cursor-pointer border-rounded transition-all"
            :class="{ 'active bg-[var(--el-color-primary-light-9)] border-primary': modelValue === item.id }"
            @click="handleClick(item)">
            <div class="flex justify-between items-center mb-5px">
              <span class="font-bold text-14px">{{ item.name }}</span>
              <el-tag :type="WarehouseTypeMap.get(item.type) === '平台仓' ? 'success' : 'info'" size="small">
                {{ WarehouseTypeMap.get(item.type) }}
              </el-tag>
            </div>
            <div class="text-12px text-gray-500 truncate">{{ item.remark || '暂无备注' }}</div>
          </div>
          <el-empty v-if="list.length === 0" description="未搜索到仓库" />
        </div>
      </el-scrollbar>

      <!-- 分页组件 - 强制固定在底部 -->
      <div
        class="flex justify-between items-center mt-10px pt-10px border-t border-[var(--el-border-color-lighter)] flex-shrink-0">
        <el-button size="small" :disabled="queryParams.pageNo <= 1" @click="handlePageChange(-1)">
          上一页
        </el-button>
        <span class="text-12px text-gray-500">第 {{ queryParams.pageNo }} 页</span>
        <el-button size="small" :disabled="isLastPage" @click="handlePageChange(1)">
          下一页
        </el-button>
      </div>
    </div>
  </ContentWrap>

  <!-- 表单弹窗：新增/修改 -->
  <WarehouseForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { WarehouseApi, WarehouseTypes, WarehouseTypeMap } from '@/app/erplus/api/stock/warehouse'
import { ContentWrap } from '@/components/ContentWrap'
import { Icon } from '@/components/Icon'
import WarehouseForm from '../WarehouseForm.vue'

defineOptions({ name: 'WarehouseSidebar' })

const props = defineProps<{
  modelValue: number | null
}>()

const emit = defineEmits(['update:modelValue', 'select'])

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  type: undefined,
})

const isLastPage = computed(() => {
  return queryParams.pageNo * queryParams.pageSize >= total.value
})

const getList = async () => {
  loading.value = true
  try {
    const data = await WarehouseApi.getWarehousePage(queryParams)
    list.value = data.list
    total.value = data.total

    // Defaults
    if (list.value.length > 0 && !props.modelValue) {
      handleClick(list.value[0])
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

const handlePageChange = (delta: number) => {
  queryParams.pageNo += delta
  getList()
}

const handleClick = (item: any) => {
  emit('update:modelValue', item.id)
  emit('select', item)
}

const formRef = ref()
const handleAdd = () => {
  formRef.value.open('create')
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.warehouse-item {
  border: 1px solid transparent;
}

.warehouse-item:hover {
  background-color: var(--el-color-primary-light-9);
}

.warehouse-item.active {
  border-color: var(--el-color-primary);
}

:deep(.el-card) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
