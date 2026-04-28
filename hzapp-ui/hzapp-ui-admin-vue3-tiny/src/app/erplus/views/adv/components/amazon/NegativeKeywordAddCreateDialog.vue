<template>
  <el-dialog v-model="visible" title="添加否定关键词" width="800px" append-to-body>
    <div class="flex gap-24px h-450px p-4px">
      <!-- 左侧输入框 -->
      <div class="flex-1 flex flex-col h-full overflow-hidden">
        <div class="mb-12px bg-transparent flex flex-col gap-8px p-0">
          <div class="flex items-start gap-8px">
            <span class="text-13px font-medium whitespace-nowrap pt-4px">匹配方式:</span>
            <el-checkbox-group v-model="selectedMatchTypes" size="small" class="flex flex-wrap gap-x-10px gap-y-2px">
              <el-checkbox label="NEGATIVE_EXACT">否定精准</el-checkbox>
              <el-checkbox label="NEGATIVE_PHRASE">否定词组</el-checkbox>
            </el-checkbox-group>
          </div>
        </div>

        <el-input 
          v-model="bulkKeywords" 
          type="textarea" 
          style="flex: 1"
          :input-style="{ height: '100%' }"
          placeholder="手动输入否定关键词，每行一个"
        />
        <el-button class="mt-10px" @click="parseKeywords">添加到列表 >></el-button>
      </div>
      
      <!-- 右侧已添加的关键词 -->
      <div class="flex-1 border-1 border-gray-200 border-solid rounded-4px p-12px flex flex-col h-full overflow-hidden bg-white shadow-sm">
        <div class="flex justify-between items-center mb-12px">
          <span class="font-bold text-14px text-gray-700">待添加列表</span>
          <el-tag size="small" type="info" effect="dark">{{ pendingKeywords.length }}</el-tag>
        </div>
        <div class="flex-1 overflow-y-auto">
          <el-table :data="pendingKeywords" size="small" height="100%">
            <el-table-column label="关键词" prop="keywordText" show-overflow-tooltip />
            <el-table-column label="匹配" prop="matchType" width="100">
              <template #default="{ row }">
                <span v-if="row.matchType === 'NEGATIVE_EXACT'">否定精准</span>
                <span v-else-if="row.matchType === 'NEGATIVE_PHRASE'">否定词组</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="50" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" @click="pendingKeywords.splice($index, 1)">
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
      <el-button type="primary" @click="submitKeywords">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'

const props = defineProps<{
  existingKeywords?: any[]
}>()

const emit = defineEmits(['success'])

const visible = ref(false)
const selectedMatchTypes = ref(['NEGATIVE_EXACT'])
const bulkKeywords = ref('')
const pendingKeywords = ref<any[]>([])

const open = () => {
  visible.value = true
  bulkKeywords.value = ''
  pendingKeywords.value = []
}

const parseKeywords = () => {
  if (!bulkKeywords.value) return
  if (selectedMatchTypes.value.length === 0) {
    ElMessage.warning('请至少选择一种匹配方式')
    return
  }
  const lines = bulkKeywords.value.split('\n').map(l => l.trim()).filter(l => l)
  let skipCount = 0
  
  lines.forEach(text => {
    selectedMatchTypes.value.forEach(matchType => {
      const isInPending = pendingKeywords.value.some(k => k.keywordText?.trim().toLowerCase() === text.trim().toLowerCase() && k.matchType === matchType)
      const isExisted = props.existingKeywords?.some(k => 
        k.state === 'ENABLED' && 
        k.keywordText?.trim().toLowerCase() === text.trim().toLowerCase() && 
        k.matchType === matchType
      )
      
      if (!isInPending && !isExisted) {
        pendingKeywords.value.push({
          keywordText: text,
          matchType: matchType,
          state: 'ENABLED'
        })
      } else {
        skipCount++
      }
    })
  })
  
  if (skipCount > 0) {
    ElMessage.info(`已自动过滤 ${skipCount} 个重复的否定关键词`)
  }
  bulkKeywords.value = ''
}

const submitKeywords = () => {
  if (pendingKeywords.value.length === 0) {
    ElMessage.warning('没有要添加的否定关键词')
    return
  }
  
  emit('success', pendingKeywords.value)
  visible.value = false
}

defineExpose({ open })
</script>
