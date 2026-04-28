<template>
  <el-dialog v-model="visible" title="添加否定商品" width="800px" append-to-body>
    <div class="flex gap-24px h-450px p-4px">
      <!-- 左侧输入框 -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <div class="mb-12px bg-transparent flex flex-col gap-8px p-0">
          <div class="flex items-start gap-8px">
            <span class="text-13px font-medium whitespace-nowrap pt-4px">否定类型:</span>
            <el-radio-group v-model="selectedType" size="small">
              <el-radio label="ASIN_SAME_AS">商品 (ASIN)</el-radio>
              <el-radio label="ASIN_BRAND_SAME_AS">品牌</el-radio>
            </el-radio-group>
          </div>
        </div>

        <el-input 
          v-model="bulkValues" 
          type="textarea" 
          style="flex: 1"
          :input-style="{ height: '100%' }"
          :placeholder="selectedType === 'ASIN_SAME_AS' ? '手动输入 ASIN，每行一个' : '手动输入品牌 ID，每行一个'"
        />
        <el-button class="mt-10px" @click="parseValues">添加到列表 >></el-button>
      </div>
      
      <!-- 右侧已添加的商品 -->
      <div class="flex-1 border-1 border-gray-200 border-solid rounded-4px p-12px flex flex-col h-full overflow-hidden bg-white shadow-sm">
        <div class="flex justify-between items-center mb-12px">
          <span class="font-bold text-14px text-gray-700">待添加列表</span>
          <el-tag size="small" type="info" effect="dark">{{ pendingItems.length }}</el-tag>
        </div>
        <div class="flex-1 overflow-y-auto">
          <el-table :data="pendingItems" size="small" height="100%">
            <el-table-column label="值" prop="value" show-overflow-tooltip />
            <el-table-column label="类型" width="100">
              <template #default="{ row }">
                <span v-if="row.type === 'ASIN_SAME_AS'">商品 (ASIN)</span>
                <span v-else-if="row.type === 'ASIN_BRAND_SAME_AS'">品牌</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="50" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" @click="pendingItems.splice($index, 1)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="savingItems" @click="submitItems">确认添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { AmzAdvAdGroupManagerApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  shopId: number
  adGroupId: number
  existingItems?: any[]
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const selectedType = ref('ASIN_SAME_AS')
const bulkValues = ref('')
const pendingItems = ref<any[]>([])
const savingItems = ref(false)

const open = () => {
  visible.value = true
  bulkValues.value = ''
  pendingItems.value = []
}

const parseValues = () => {
  if (!bulkValues.value) return
  
  const lines = bulkValues.value.split('\n').map(l => l.trim()).filter(l => l)
  let skipCount = 0
  
  lines.forEach(text => {
    const isInPending = pendingItems.value.some(k => k.value?.trim().toLowerCase() === text.trim().toLowerCase() && k.type === selectedType.value)
    const isExisted = props.existingItems?.some(p => 
      p.state === 'ENABLED' && 
      p.expression?.[0]?.value?.trim().toLowerCase() === text.trim().toLowerCase() && 
      p.expression?.[0]?.type === selectedType.value
    )
    
    if (!isInPending && !isExisted) {
      pendingItems.value.push({
        value: text,
        type: selectedType.value
      })
    } else {
      skipCount++
    }
  })
  
  if (skipCount > 0) {
    ElMessage.info(`已自动过滤 ${skipCount} 个重复的否定项`)
  }
  bulkValues.value = ''
}

const submitItems = async () => {
  if (pendingItems.value.length === 0) {
    ElMessage.warning('没有要添加的否定项')
    return
  }
  savingItems.value = true
  try {
    await AmzAdvAdGroupManagerApi.batchCreateNegativeTargeting({
      shopId: props.shopId,
      groupId: props.adGroupId,
      items: pendingItems.value.map((k: any) => ({
        state: 'ENABLED',
        expression: [{
          type: k.type,
          value: k.value
        }]
      }))
    })
    ElMessage.success('否定商品添加成功')
    visible.value = false
    emit('success')
  } catch (e) {
    ElMessage.error('否定商品添加失败')
  } finally {
    savingItems.value = false
  }
}

defineExpose({ open })
</script>
