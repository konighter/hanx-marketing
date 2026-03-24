<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
v-model="queryParams.name" placeholder="请输入渠道名称" clearable @keyup.enter="handleQuery"
          class="!w-240px" />
      </el-form-item>
      <el-form-item label="类型" prop="channelType">
        <el-select v-model="queryParams.channelType" placeholder="请选择渠道类型" clearable class="!w-240px">
          <el-option v-for="item in ChannelTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="openForm('create')">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="渠道名称" align="center" prop="name" min-width="150" />
      <el-table-column label="渠道类型" align="center" prop="channelType" width="120">
        <template #default="scope">
          <el-tag :type="channelTypeTag(scope.row.channelType)">
            {{ channelTypeName(scope.row.channelType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Webhook" align="center" prop="webhookUrl" min-width="200">
        <template #default="scope">
          <span class="text-xs text-gray-500">{{ maskUrl(scope.row.webhookUrl) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center" width="260" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleTestSend(scope.row)">
            <Icon icon="ep:position" class="mr-3px" /> 测试
          </el-button>
          <el-button link type="primary" @click="openForm('update', scope.row.id)">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
:total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </ContentWrap>

  <!-- 表单弹窗 -->
  <NotifyChannelForm ref="formRef" @success="getList" />
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { NotifyChannelApi, ChannelTypeOptions } from '@/app/system/api/notifyChannel'
import NotifyChannelForm from './NotifyChannelForm.vue'

defineOptions({ name: 'NotifyChannel' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const list = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  channelType: undefined,
  status: undefined
})
const queryFormRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await NotifyChannelApi.getPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 新增/修改 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除 */
const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await NotifyChannelApi.delete(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

/** 测试发送 */
const handleTestSend = async (row: any) => {
  try {
    await message.confirm(`确认向【${row.name}】发送测试消息?`)
    await NotifyChannelApi.testSend(row.id)
    message.success('测试消息已发送，请检查对应渠道')
  } catch {}
}

/** 渠道类型显示 */
const channelTypeName = (type: number) => {
  const item = ChannelTypeOptions.find(i => i.value === type)
  return item ? item.label : '未知'
}

const channelTypeTag = (type: number) => {
  const map: Record<number, string> = { 1: 'primary', 2: 'warning', 3: 'success' }
  return map[type] || 'info'
}

/** Webhook URL 脱敏 */
const maskUrl = (url: string) => {
  if (!url) return ''
  if (url.length <= 30) return url
  return url.substring(0, 25) + '...' + url.substring(url.length - 8)
}

onMounted(() => {
  getList()
})
</script>
