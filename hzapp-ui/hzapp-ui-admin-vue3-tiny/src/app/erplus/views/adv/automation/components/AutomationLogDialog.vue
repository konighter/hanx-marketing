<template>
  <el-dialog
    v-model="visible"
    :title="`运行日志 - ${planName}`"
    width="800px"
    append-to-body
  >
    <el-table v-loading="loading" :data="list" :stripe="true" height="450px">
      <el-table-column label="执行时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="触发规则" align="center" prop="ruleName" width="150" />
      <el-table-column label="触发指标" align="left" min-width="200">
        <template #default="scope">
          <div class="text-12px">
            <el-tag 
              v-for="(val, key) in scope.row.triggerData" 
              :key="key" 
              size="small" 
              type="info" 
              class="mr-5px mb-5px"
            >
              {{ key }}: {{ val }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="执行动作" align="center" width="150">
        <template #default="scope">
          <el-tag type="success" size="small">
            {{ scope.row.actionTaken || 'UNKNOWN' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
    
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
      layout="total, prev, pager, next"
    />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { AutomationApi } from '@/app/erplus/api/adv/automation'
import { formatDate } from '@/utils/formatTime'

const visible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const planId = ref<number>()
const planName = ref('')

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  planId: undefined as number | undefined
})

/** 打开弹窗 */
const open = (id: number, name: string) => {
  visible.value = true
  planId.value = id
  planName.value = name
  queryParams.planId = id
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AutomationApi.getLogPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

defineExpose({ open })
</script>
