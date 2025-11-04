<template>
  <!-- 列表 -->
  <ContentWrap>
    <el-button
      type="primary"
      plain
      @click="openForm('create')"
      v-hasPermi="['system:tenant:create']"
    >
      <Icon icon="ep:plus" class="mr-5px" /> 新增
    </el-button>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
<!--      <el-table-column label="ID" align="center" prop="id" />-->
      <el-table-column label="套餐ID" align="center" prop="tenantPackageId" >
        <template #default="scope">
            {{scope.row.packageName}}({{scope.row.tenantPackageId}})
        </template>

      </el-table-column>
      <el-table-column
        label="过期时间"
        align="center"
        prop="expireTime"
        :formatter="dateFormatter2"
        width="180px"
      />
      <el-table-column label="订购人数" align="center" prop="accountCount" />
      <el-table-column label="订购状态" align="center" prop="status" >
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>

      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['system:tenant:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['system:tenant:delete']"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
    <!-- 表单弹窗：添加/修改 -->
    <TenantPackageSubscriptionForm ref="formRef" @success="getList" />
</template>
<script setup lang="ts">
import {dateFormatter, dateFormatter2} from '@/utils/formatTime'
import * as TenantApi from '@/api/system/tenant'
import TenantPackageSubscriptionForm from './TenantPackageSubscriptionForm.vue'
import {DICT_TYPE} from "@/utils/dict";

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const props = defineProps<{
  tenantId: undefined // 租户ID（主表的关联字段）
}>()
const loading = ref(false) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  tenantId: undefined
})

/** 监听主表的关联字段的变化，加载对应的子表数据 */
watch(
  () => props.tenantId,
  (val) => {
    queryParams.tenantId = val
    if (!val) {
      list.value = []
      total.value = 0
      return
    }
    handleQuery()
  },
  { immediate: false }
)

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await TenantApi.getTenantPackageSubscriptionPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  if (!props.tenantId) {
    message.error('请选择一个租户')
    return
  }
  formRef.value.open(type, id, props.tenantId)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await TenantApi.deleteTenantPackageSubscription(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}
</script>
