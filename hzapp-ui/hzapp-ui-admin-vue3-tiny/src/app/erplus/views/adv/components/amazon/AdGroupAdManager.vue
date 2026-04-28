<template>
  <div class="ad-manager">
    <div class="flex justify-between items-center mb-10px w-full">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">广告列表:</span>
      </div>
      <el-button 
        size="small" 
        :disabled="disabled"
        @click="addAd"
        class="add-btn"
      >
        <el-icon><Plus /></el-icon>
        <span class="ml-4px">添加商品广告</span>
      </el-button>
    </div>
 
    <el-dialog v-model="createDialogVisible" title="添加商品广告" width="500px" append-to-body>
      <el-form label-position="top">
        <el-form-item label="请输入 SKU (每行一个)">
          <el-input
            v-model="skusText"
            type="textarea"
            :rows="10"
            placeholder="请输入 SKU，多个 SKU 请换行输入"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false" size="small">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateAd" size="small">提交</el-button>
      </template>
    </el-dialog>

    <el-table v-loading="loading" :data="visibleAds" border size="small" style="width: 100%">
      <el-table-column label="图片" width="70" align="center">
        <template #default="{ row }">
          <el-image 
            v-if="row.image"
            :src="row.image" 
            class="w-40px h-40px rounded-4px"
            :preview-src-list="[row.image]"
            preview-teleported
          />
          <div v-else class="w-40px h-40px bg-gray-100 rounded-4px flex items-center justify-center text-gray-400">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="广告" min-width="180">
        <template #default="{ row }">
          <div v-if="row.externalId" class="flex flex-col">
            <span class="text-12px font-bold">{{ row.name }}</span>
          </div>
          <div v-else class="flex gap-10px">
            <el-input v-model="row.asin" size="small" placeholder="ASIN" class="w-100px" :disabled="disabled" />
            <el-input v-model="row.sku" size="small" placeholder="SKU" class="w-100px" :disabled="disabled" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="名称" prop="name" show-overflow-tooltip />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status" :type="getStatusType(row.status)" size="mini">
            {{ row.status }}
          </el-tag>
          <el-tag v-else type="info" size="mini">NEW</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row, $index }">
          <template v-if="row.externalId">
            <el-button 
              v-if="row.status?.toUpperCase() === 'ENABLED'"
              type="warning" 
              link 
              size="small" 
              :disabled="disabled"
              @click="handleStatusChange(row, 'PAUSED')"
            >禁用</el-button>
            <el-button 
              v-else-if="row.status?.toUpperCase() === 'PAUSED'"
              type="success" 
              link 
              size="small" 
              :disabled="disabled"
              @click="handleStatusChange(row, 'ENABLED')"
            >启用</el-button>
          </template>
          <el-button 
            v-else
            type="danger" 
            link 
            size="small" 
            :disabled="disabled"
            @click="removeAd($index)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Picture, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { AdsAdApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  adGroupId?: number
  campaignId?: number
  disabled?: boolean
}>()

const loading = ref(false)
const creating = ref(false)
const createDialogVisible = ref(false)
const skusText = ref('')
const ads = ref<any[]>([])

// 只展示非归档状态的广告
const visibleAds = computed(() => {
  return (ads.value || []).filter((ad: any) => ad.status?.toUpperCase() !== 'ARCHIVED')
})

const fetchAds = async () => {
  if (!props.adGroupId) return
  loading.value = true
  try {
    const res = await AdsAdApi.getAdPage({ 
      adGroupIds: [props.adGroupId],
      pageSize: 100 
    })
    ads.value = res.list || []
  } finally {
    loading.value = false
  }
}

watch(() => props.adGroupId, fetchAds, { immediate: true })

const getStatusType = (status: string) => {
  switch (status?.toUpperCase()) {
    case 'ENABLED': return 'success'
    case 'PAUSED': return 'warning'
    case 'ARCHIVED': return 'danger'
    default: return 'info'
  }
}

const addAd = () => {
  skusText.value = ''
  createDialogVisible.value = true
}

const handleCreateAd = async () => {
  if (!skusText.value.trim()) {
    ElMessage.warning('请输入 SKU')
    return
  }
  
  const skus = skusText.value.split('\n')
    .map(s => s.trim())
    .filter(s => s !== '')

  if (skus.length === 0) {
    ElMessage.warning('请输入有效的 SKU')
    return
  }

  creating.value = true
  try {
    const requests = skus.map(sku => ({
      adGroupId: props.adGroupId?.toString(),
      campaignId: props.campaignId?.toString(),
      name: sku,
      data: {
        sku: [sku]
      }
    }))
 
    await AdsAdApi.createAd({
      adGroupId: props.adGroupId,
      campaignId: props.campaignId,
      createRequests: requests
    })
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    fetchAds() // 刷新列表
  } catch (error) {
    console.error('创建广告失败', error)
  } finally {
    creating.value = false
  }
}

const removeAd = (index: number) => {
  ads.value.splice(index, 1)
}

const handleStatusChange = async (row: any, status: string) => {
  const statusName = status === 'ENABLED' ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${statusName}该广告吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await AdsAdApi.updateAdStatus({ id: row.id, status })
    row.status = status
    ElMessage.success(`${statusName}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新状态失败', error)
      ElMessage.error(`${statusName}失败`)
    }
  }
}
</script>

<style scoped>
.ad-manager :deep(.el-table .cell) {
  padding: 4px 8px;
}

.add-btn {
  border: 1px solid var(--el-border-color);
  background: #fff;
  color: var(--el-text-color-regular);
  height: 24px;
  padding: 0 8px;
  font-size: 12px;
}
.add-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
</style>
