<template>
  <el-drawer v-model="visible" title="货件审核" size="90%" :destroy-on-close="true">
    <div v-loading="loading" class="audit-container h-full flex flex-col p-4 bg-gray-50">
      <el-row :gutter="20" class="flex-1 overflow-hidden">
        <!-- 左侧：SKU列表与概览 -->
        <el-col :span="16" class="h-full flex flex-col gap-4 overflow-hidden">
          <!-- SKU列表 -->
          <div class="flex-1 bg-white p-4 rounded shadow-sm overflow-hidden flex flex-col">
            <h3 class="text-lg font-bold mb-4">商品列表</h3>
            <el-table :data="shipmentData.items" border style="width: 100%" height="100%">
              <el-table-column type="index" width="60" label="序号" align="center" />
              <el-table-column label="图片" width="80" align="center">
                <template #default="scope">
                  <el-image :src="scope.row.image" class="w-12 h-12 rounded" fit="cover" />
                </template>
              </el-table-column>
              <el-table-column label="商品名称" min-width="150">
                <template #default="scope">
                  <div class="flex flex-col gap-1">
                    <span class="font-bold">{{ scope.row.sellerSku }}</span>
                    <span class="text-xs text-gray-400 truncate">{{ scope.row.name }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="发货数量" width="100" align="center" />
              <el-table-column prop="availableCount" label="可用库存" width="100" align="center" />
              <el-table-column prop="dimension" label="长*宽*高" width="150" align="center" />
              <el-table-column prop="weight" label="重量" width="100" align="center" />
            </el-table>
          </div>

          <!-- 货件概述 -->
          <ShipmentOverview :shipmentData="shipmentData" />
        </el-col>

        <!-- 右侧：审核信息 -->
        <el-col :span="8" class="h-full flex flex-col gap-4">
          <div class="bg-white p-6 rounded shadow-sm flex flex-col h-full">
            <h3 class="text-lg font-bold mb-6">审核工作台</h3>

            <div class="flex-1 space-y-6">
              <div>
                <span class="text-gray-500 text-sm block mb-1">当前状态</span>
                <el-tag size="large">{{ getStatusLabel(shipmentData.status) }}</el-tag>
              </div>

              <div>
                <span class="text-gray-500 text-sm block mb-1">审核意见</span>
                <el-input v-model="auditForm.remark" type="textarea" :rows="6" placeholder="请输入审核意见（驳回时必填）" />
              </div>
            </div>

            <div class="pt-6 border-t flex flex-row gap-4">
              <el-button type="danger" plain size="large" class="flex-1" @click="handleReject" :loading="submitting">
                驳回修改
              </el-button>
              <el-button type="primary" size="large" class="flex-1" @click="handlePass" :loading="submitting">
                通过审核
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { shipmentApi, ShipmentStatus } from '@/app/erplus/api/stock/shipment'
import { ElMessage, ElMessageBox } from 'element-plus'
import ShipmentOverview from './ShipmentOverview.vue'

defineOptions({ name: 'ShipmentAudit' })

const emit = defineEmits(['success'])

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const shipmentData = ref<any>({})

const auditForm = reactive({
  id: undefined,
  remark: '',
  pass: true
})

/** 打开抽屉 */
const open = async (row: any) => {
  visible.value = true
  loading.value = true
  try {
    const data = await shipmentApi.getShipment(row.id)
    shipmentData.value = data
    auditForm.id = data.id
    auditForm.remark = ''
  } catch (error) {
    ElMessage.error('获取货件详情失败')
    visible.value = false
  } finally {
    loading.value = false
  }
}

/** 状态展示 */
const getStatusLabel = (status: number) => {
  const statusMap: Record<number, string> = {
    [ShipmentStatus.INIT]: '已保存',
    [ShipmentStatus.AUDITING]: '待审核',
    [ShipmentStatus.PENDING_SHIPMENT]: '待配货',
    [ShipmentStatus.PENDING_BOXING]: '待装箱',
    [ShipmentStatus.PENDING_LABEL]: '待贴标',
    [ShipmentStatus.PENDING_DELIVERY]: '待发货',
    [ShipmentStatus.SHIPPED]: '已发货',
    [ShipmentStatus.CANCELED]: '已取消'
  }
  return statusMap[status] || '未知'
}

/** 审核通过 */
const handlePass = async () => {
  try {
    await ElMessageBox.confirm('确定通过该货件审核吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    submitting.value = true
    await shipmentApi.auditShipment({
      shipmentId: auditForm.id,
      remark: auditForm.remark,
      action: 1
    })
    ElMessage.success('审核通过成功')
    visible.value = false
    emit('success')
  } catch (e) {
    // Cancelled or Error
  } finally {
    submitting.value = false
  }
}

/** 审核驳回 */
const handleReject = async () => {
  if (!auditForm.remark) {
    ElMessage.warning('请填写审核意见')
    return
  }
  try {
    await ElMessageBox.confirm('确定驳回该货件吗？', '提示', {
      type: 'error',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    submitting.value = true
    await shipmentApi.auditShipment({
      shipmentId: auditForm.id,
      remark: auditForm.remark,
      action: 2
    })
    ElMessage.success('已成功驳回')
    visible.value = false
    emit('success')
  } catch (e) {
    // Cancelled or Error
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<style scoped>
.audit-container :deep(.el-card__header) {
  padding: 8px 12px;
  font-weight: bold;
  font-size: 14px;
}

.audit-container :deep(.el-card__body) {
  padding: 12px;
}
</style>
