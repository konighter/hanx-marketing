<template>
  <div class="ad-sync-dialog-trigger">
    <!-- 同步状态提示或简单的加载状态可以在这里添加 -->
    <el-button 
      type="primary" 
      circle
      :loading="loading"
      :disabled="!accountId"
      @click="visible = true"
    >
      <Icon icon="ep:refresh" />
    </el-button>

    <!-- 同步参数对话框 -->
    <el-dialog 
      v-model="visible" 
      title="同步广告数据" 
      width="500px" 
      append-to-body
      destroy-on-close
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="同步方式">
          <el-radio-group v-model="form.type">
            <el-radio label="incr">增量同步</el-radio>
            <el-radio label="all">全量同步</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="时间范围" v-if="form.type === 'incr'">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="!w-full"
          />
          <div class="text-[var(--el-text-color-placeholder)] text-12px mt-1">
            建议同步最近 7-14 天的数据以确保准确性。
          </div>
        </el-form-item>
        
        <div v-else class="text-[var(--el-text-color-placeholder)] text-12px ml-100px mb-10px">
          全量同步将拉取该账户下的所有历史数据，耗时可能较长，请耐心等待。
        </div>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirm" :loading="loading">
            确定同步
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { AdsSyncApi } from '@/app/erplus/api/adv/ads'

const props = defineProps<{
  accountId?: number
}>()

const emit = defineEmits(['success'])
const message = useMessage()

const visible = ref(false)
const loading = ref(false)

const form = reactive({
  type: 'incr',
  dateRange: [] as string[]
})

const handleConfirm = async () => {
  if (!props.accountId) return
  
  loading.value = true
  try {
    if (form.type === 'all') {
      await AdsSyncApi.syncAllMetadata(props.accountId)
      message.success('已触发全量同步任务')
    } else {
      // 如果后端支持传时间范围，可以在这里传递参数
      // 目前 AdsSyncApi.syncIncrMetadata 仅支持 accountId
      await AdsSyncApi.syncIncrMetadata(props.accountId)
      message.success('已触发增量同步任务')
    }
    visible.value = false
    emit('success')
  } catch (error) {
    console.error('同步失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ad-sync-dialog-trigger {
  display: inline-block;
}
</style>
