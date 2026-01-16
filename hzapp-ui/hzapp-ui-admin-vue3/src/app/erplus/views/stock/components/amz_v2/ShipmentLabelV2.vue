<template>
  <div class="shipment-label-v2" v-loading="loading">
    <div v-if="hasShipments" class="space-y-6">
      <el-alert title="请为每个货件查看箱子详情并打印标签" type="info" :closable="false" class="mb-4" />

      <!-- 全局配置 -->
      <div class="bg-white p-4 rounded-lg shadow-sm border border-gray-100 flex items-center justify-between">
        <div class="flex items-center gap-6">
          <div class="flex items-center gap-2">
            <span class="text-sm font-medium text-gray-500 whitespace-nowrap">页面类型:</span>
            <el-select v-model="labelConfig.pageType" placeholder="选择页面类型" class="w-48" size="small">
              <el-option v-for="type in LABEL_PAGE_TYPES" :key="type.value" :label="type.label" :value="type.value" />
            </el-select>
          </div>
          <div class="flex items-center gap-2">
            <span class="text-sm font-medium text-gray-500 whitespace-nowrap">文件格式:</span>
            <el-select v-model="labelConfig.labelFormat" placeholder="选择格式" class="w-32" size="small">
              <el-option v-for="fmt in LABEL_FORMATS" :key="fmt.value" :label="fmt.label" :value="fmt.value" />
            </el-select>
          </div>
        </div>
        <div class="text-xs text-gray-400">
          * 配置将应用于所有下载和预览操作
        </div>
      </div>

      <div v-for="shipment in confirmedShipments" :key="shipment.shipmentId"
        class="shipment-card bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="shipment-header bg-gray-50 p-4 border-b flex justify-between items-center">
          <div class="flex items-center gap-4">
            <span class="font-bold text-gray-700">货件 ID: {{ shipment.shipmentId }}</span>
            <el-tag type="info">{{ shipment.destination?.warehouseId || '未知' }}</el-tag>
            <span class="text-xs text-gray-400">
              {{ shipment.destination?.address?.city }}, {{ shipment.destination?.address?.countryCode }}
            </span>
          </div>
          <div class="flex items-center gap-2">
            <el-button type="primary" size="small" link @click="handleBatchDownload(shipment)">
              <Icon icon="ep:download" class="mr-1" />批量下载
            </el-button>
          </div>
        </div>

        <div class="p-4 bg-white">
          <el-table :data="shipment.boxes" border size="small" stripe>
            <el-table-column label="箱号" min-width="120">
              <template #default="{ row }">
                <div class="font-bold text-gray-700 leading-tight">{{ row.templateName }}</div>
                <div class="text-[10px] text-gray-400 font-mono">{{ row.boxId }}</div>
              </template>
            </el-table-column>
            <el-table-column label="尺寸(长*宽*高)" width="140" align="center">
              <template #default="{ row }">
                {{ row.dimensions.length }} x {{ row.dimensions.width }} x {{ row.dimensions.height }} <span>{{
                  row.dimensions.unitOfMeasurement }}</span>
              </template>
            </el-table-column>
            <el-table-column label="重量" width="100" align="center">
              <template #default="{ row }">
                {{ row.weight.value }} <span>{{ row.weight.unit }}</span>
              </template>
            </el-table-column>
            <el-table-column label="内容摘要" min-width="200">
              <template #default="{ row }">
                <div class="flex flex-wrap gap-1">
                  <el-tag v-for="item in row.items" :key="item.msku" size="small" type="info" effect="plain"
                    class="!px-1">
                    {{ item.msku }} x <span class="font-bold">{{ item.quantity }}</span>
                  </el-tag>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center" fixed="right">
              <template #default="{ row }">
                <div class="flex justify-center gap-2" v-if="row.labelUrl">
                  <el-button type="primary" size="small" @click="handlePreview(row.labelUrl)"
                    :loading="previewingId === (row.boxId || row.packageId)">预览</el-button>
                  <el-button type="success" size="small" @click="handlePrint(row.labelUrl)"
                    :loading="printingId === (row.boxId || row.packageId)">打印</el-button>
                </div>
                <div v-else>
                  <el-button type="primary" size="small" @click="handleGenerate(shipment, row)"
                    :loading="generatingId === (row.boxId || row.packageId)">生成</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- Footer Actions -->
      <div class="flex justify-center gap-4 mt-8 pb-10">
        <el-button type="primary" @click="$emit('next')" v-if="!readonly">确认发货</el-button>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="p-20 text-center bg-white rounded-lg border-2 border-dashed">
      <Icon icon="ep:printer" :size="64" class="text-gray-200 mb-4" />
      <h3 class="text-gray-500 font-bold">尚未生成货件 labels</h3>
      <p class="text-gray-400 text-sm mt-2">请先完成分仓和运输配置</p>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="标签预览" width="800px" top="5vh" append-to-body destroy-on-close>
      <div class="flex flex-col items-center gap-4 py-4 min-h-[500px]" v-loading="previewLoading">
        <template v-if="previewUrl">
          <img v-if="labelConfig.labelFormat === 'PNG'" :src="previewUrl" class="max-w-full shadow-md" />
          <iframe v-else-if="labelConfig.labelFormat === 'PDF'" :src="previewUrl"
            class="w-full h-[600px] border shadow-sm rounded"></iframe>
        </template>
        <el-empty v-else description="暂无预览数据" />
      </div>
      <template #footer>
        <div class="flex justify-center gap-2">
          <el-button @click="previewVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleDownloadSingle">下载</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage } from 'element-plus'
import { Icon } from '@/components/Icon'
import { LABEL_PAGE_TYPES, LABEL_FORMATS, SHIPPING_MODES_MAP } from './constants'

const props = defineProps({
  shipmentPlan: {
    type: Object,
    required: true
  },
  active: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  }
})

const planId = computed(() => props.shipmentPlan?.extralId || '')
const emit = defineEmits(['back', 'next'])

const loading = ref(false)
const confirmedShipments = ref<any[]>([])
const labelConfig = ref({
  pageType: 'PACKAGE_LABEL_THERMAL_PRINT',
  labelFormat: 'PDF'
})

const hasShipments = computed(() => confirmedShipments.value && confirmedShipments.value.length > 0)

// 预览相关
const previewVisible = ref(false)
const previewLoading = ref(false)
const previewUrl = ref('')
const previewingId = ref('')
const printingId = ref('')
const generatingId = ref('')
const currentActiveItem = ref<any>(null)

/** 批量获取标签下载链接 */
const handleBatchDownload = async (shipment: any) => {
  if (!planId.value || !shipment.shipmentId) return
  try {
    const res = await AmzInboundApi.getLabels({
      planId: planId.value,
      shipmentId: shipment.shipmentId,
      pageType: labelConfig.value.pageType,
      labelFormat: labelConfig.value.labelFormat
    })
    const url = res.downloadUrl || res.labelDownloadUrl
    if (url) {
      window.open(url, '_blank')
      ElMessage.success('已开始下载标签')
    } else {
      ElMessage.warning('未能获取下载链接')
    }
  } catch (error) {
    ElMessage.error('批量获取标签失败')
  }
}

/** 预览单箱标签 */
const handlePreview = async (url: string) => {
  if (!url) return

  previewVisible.value = true
  previewLoading.value = true
  previewUrl.value = ''

  try {
    // 为 PDF 强制开启在线预览（通过 Blob 绕过 Content-Disposition: attachment）
    if (labelConfig.value.labelFormat === 'PDF') {
      try {
        const res = await fetch(url)
        const blob = await res.blob()
        previewUrl.value = URL.createObjectURL(blob)
      } catch (e) {
        console.warn('Fetch blob failed, falling back to direct URL', e)
        previewUrl.value = url
      }
    } else {
      previewUrl.value = url
    }
  } catch (error) {
    ElMessage.error('获取预览内容失败')
  } finally {
    previewLoading.value = false
  }
}

/** 打印单箱标签 */
const handlePrint = async (url: string) => {
  if (!url) return
  try {
    const res = await fetch(url)
    const blob = await res.blob()

    const printWin = window.open(URL.createObjectURL(blob), '_blank')
    if (printWin) {
      printWin.addEventListener('load', () => {
        printWin.print()
      })
    }
    ElMessage.success('已发送至浏览器打印')
  } catch (error) {
    ElMessage.error('打印操作失败')
  }
}

/** 手动生成单箱标签 */
const handleGenerate = async (shipment: any, box: any) => {
  generatingId.value = box.boxId || box.packageId
  try {
    const res = await AmzInboundApi.getLabels({
      planId: planId.value,
      shipmentId: shipment.shipmentId,
      packageId: box.boxId || box.packageId,
      pageType: labelConfig.value.pageType,
      labelFormat: labelConfig.value.labelFormat
    })
    const url = res.downloadUrl || res.labelDownloadUrl
    if (url) {
      box.labelUrl = url
      ElMessage.success('标签生成成功')
    } else {
      ElMessage.warning('未能获取标签链接')
    }
  } catch (error) {
    ElMessage.error('生成标签失败')
  } finally {
    generatingId.value = ''
  }
}

/** 下载预览中的单个标签 */
const handleDownloadSingle = () => {
  if (previewUrl.value) {
    window.open(previewUrl.value, '_blank')
  }
}

const init = async () => {
  if (!planId.value) return
  loading.value = true
  try {
    const res = await AmzInboundApi.getLabels({
      planId: planId.value
    })
    // 假设 res 返回的是 shipment 列表，每个 shipment 有 boxes
    if (res && Array.isArray(res)) {
      confirmedShipments.value = res
    } else if (res && res.shipments) {
      confirmedShipments.value = res.shipments
    }
  } catch (error) {
    console.error('Failed to init label page', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.active) init()
})

watch(() => props.active, (val) => {
  if (val) init()
})
</script>

<style scoped>
.shipment-card {
  transition: all 0.3s ease;
}

.shipment-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.shipment-header {
  background: linear-gradient(to right, #f8fafc, #ffffff);
}
</style>
