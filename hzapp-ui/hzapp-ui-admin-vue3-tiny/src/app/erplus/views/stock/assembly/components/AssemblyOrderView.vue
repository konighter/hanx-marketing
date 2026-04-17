<template>
  <el-drawer v-model="drawerVisible" title="装配单详情与处理" size="600px">
    <div v-loading="loading" v-if="order">
      <!-- 基础信息 -->
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="单据编号">{{ order.no }}</el-descriptions-item>
        <el-descriptions-item label="产成品 SKU">{{ order.skuName || order.skuId }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ order.plannedQty }}</el-descriptions-item>
        <el-descriptions-item label="单据状态">
          <el-tag :type="getStatusType(order.status)">{{ getStatusLabel(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="生产批次" v-if="order.batchNo">
          <el-tag type="success">{{ order.batchNo }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 耗材清单 -->
      <h4 class="mt-20px mb-10px flex items-center">
        耗材清单（BOM 展开）
        <el-tooltip content="实际装配时将根据此清单核减对应耗材库存" placement="top">
          <Icon icon="ep:question-filled" class="ml-5px cursor-help" />
        </el-tooltip>
      </h4>
      
      <el-table :data="getPagedItems" border size="small">
        <el-table-column label="耗材名称" prop="materialName" min-width="150" show-overflow-tooltip />
        <el-table-column label="预期需求" prop="expectedQty" width="100" align="center">
          <template #default="scope">
            {{ scope.row.expectedQty }} {{ scope.row.materialUnit }}
          </template>
        </el-table-column>
        <el-table-column label="缺货量" prop="shortfallQty" width="100" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger font-bold': scope.row.shortfallQty > 0 }">
              {{ scope.row.shortfallQty || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
            <Icon v-if="scope.row.shortfallQty > 0" icon="ep:warning" class="text-warning" />
            <Icon v-else icon="ep:success-filled" class="text-success" />
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div v-if="items.length > pageSize" class="mt-15px">
        <Pagination
          small
          layout="prev, pager, next"
          :total="items.length"
          v-model:page="pageNo"
          v-model:limit="pageSize"
          class="!m-0 !mt-0 !mb-0 !float-none"
        />
      </div>

      <!-- 底部操作区 -->
      <div class="mt-30px flex justify-end gap-10px">
        <el-button v-if="order.status === 0" type="primary" @click="handleStart"> 启动装配 </el-button>
        <el-button v-if="order.status === 1" type="success" @click="handleComplete"> 完成装配 </el-button>
        <el-button v-if="order.status <= 1" type="danger" plain @click="handleCancel"> 取消订单 </el-button>
        <el-button @click="drawerVisible = false"> 关 闭 </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { AssemblyApi, AssemblyOrderVO, AssemblyItemVO } from '@/app/erplus/api/stock/assembly'
import { ElMessageBox, ElMessage } from 'element-plus'

const drawerVisible = ref(false)
const loading = ref(false)
const order = ref<any>(null)
const items = ref<AssemblyItemVO[]>([])

// 分页相关状态
const pageNo = ref(1)
const pageSize = ref(10)

/** 截取当前页的数据 */
const getPagedItems = computed(() => {
  const start = (pageNo.value - 1) * pageSize.value
  return items.value.slice(start, start + pageSize.value)
})

/** 打开详情页 */
const open = async (id: number) => {
  drawerVisible.value = true
  loading.value = true
  try {
    pageNo.value = 1 // 打开时重置页码
    order.value = await AssemblyApi.getAssemblyOrder(id)
    items.value = await AssemblyApi.getAssemblyOrderItems(id)
  } finally {
    loading.value = false
  }
}
defineExpose({ open })

const emit = defineEmits(['success'])

/** 启动装配 */
const handleStart = async () => {
  try {
    await ElMessageBox.confirm('启动装配将检查耗材实时库存，是否继续？', '提示', { type: 'info' })
    loading.value = true
    await AssemblyApi.startAssemblyOrder(order.value.id)
    ElMessage.success('已启动')
    await open(order.value.id) // 刷新详情
    emit('success')
  } catch {} finally {
    loading.value = false
  }
}

/** 完成装配 */
const handleComplete = async () => {
  try {
    await ElMessageBox.confirm('确认已完成离线装配？点击确定后将增加成品库存并自动核减耗材库存。', '确认完成', {
      type: 'success',
      confirmButtonText: '确定并入库',
      cancelButtonText: '取消'
    })
    loading.value = true
    await AssemblyApi.completeAssemblyOrder(order.value.id)
    ElMessage.success('装配任务已闭环，库存已更新')
    drawerVisible.value = false
    emit('success')
  } catch (e: any) {
    // 刷新详情以更新缺货状态
    await open(order.value.id)
  } finally {
    loading.value = false
  }
}

/** 取消订单 */
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确认取消该装配单？', '警示', { type: 'warning' })
    loading.value = true
    await AssemblyApi.cancelAssemblyOrder(order.value.id)
    ElMessage.info('已取消')
    drawerVisible.value = false
    emit('success')
  } catch {} finally {
    loading.value = false
  }
}

const getStatusLabel = (status: number) => {
  const labels = ['待启动', '装配中', '已完成', '已取消']
  return labels[status] || '未知'
}

const getStatusType = (status: number) => {
  const types = ['', 'warning', 'success', 'danger']
  return types[status] || 'info'
}
</script>
