<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="企业名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入企业名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input
          v-model="queryParams.contactName"
          placeholder="请输入联系人"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="审批状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择审批状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.SYSTEM_TENANT_APPLY_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="企业名称" align="center" prop="name" />
      <el-table-column label="联系人" align="center" prop="contactName" />
      <el-table-column label="联系手机" align="center" prop="contactMobile" />
      <el-table-column label="租户套餐" align="center" prop="packageId">
        <template #default="scope">
          <template v-for="item in packageList" :key="item.id">
            <el-tag v-if="item.id === scope.row.packageId" type="success">
              {{ item.name }}
            </el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="申请备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="审批状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.SYSTEM_TENANT_APPLY_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
        :formatter="dateFormatter"
      />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleAudit(scope.row)"
            v-if="scope.row.status === 0"
            v-hasPermi="['system:tenant:update']"
          >
            审批
          </el-button>
          <el-button
            link
            type="info"
            @click="handleDetail(scope.row)"
          >
            详情
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

  <!-- 审批弹窗 -->
  <TenantApplyAuditForm ref="auditFormRef" @success="getList" />

  <!-- 详情弹窗 -->
  <Dialog v-model="detailVisible" title="申请详情" width="600px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="企业名称">{{ detailData.name }}</el-descriptions-item>
      <el-descriptions-item label="联系人">{{ detailData.contactName }}</el-descriptions-item>
      <el-descriptions-item label="联系手机">{{ detailData.contactMobile }}</el-descriptions-item>
      <el-descriptions-item label="申请备注">{{ detailData.remark || '无' }}</el-descriptions-item>
      <el-descriptions-item label="租户套餐">
        <template v-for="item in packageList" :key="item.id">
          <span v-if="item.id === detailData.packageId">{{ item.name }}</span>
        </template>
      </el-descriptions-item>
      <el-descriptions-item label="审批状态">
        <dict-tag :type="DICT_TYPE.SYSTEM_TENANT_APPLY_STATUS" :value="detailData.status" />
      </el-descriptions-item>
      <el-descriptions-item v-if="detailData.status !== 0" label="审批人ID">
        {{ detailData.auditUserId }}
      </el-descriptions-item>
      <el-descriptions-item v-if="detailData.status !== 0" label="审批时间">
        {{ dateFormatter(null, null, detailData.auditTime) }}
      </el-descriptions-item>
      <el-descriptions-item v-if="detailData.status !== 0" label="审批备注">
        {{ detailData.auditRemark || '无' }}
      </el-descriptions-item>
      <el-descriptions-item v-if="detailData.tenantId" label="生成的租户ID">
        {{ detailData.tenantId }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ dateFormatter(null, null, detailData.createTime) }}
      </el-descriptions-item>
    </el-descriptions>
  </Dialog>
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as TenantApplyApi from '@/api/system/tenantApply'
import * as TenantPackageApi from '@/api/system/tenantPackage'
import TenantApplyAuditForm from './TenantApplyAuditForm.vue'

defineOptions({ name: 'SystemTenantApply' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  contactName: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const packageList = ref([] as TenantPackageApi.TenantPackageVO[]) // 租户套餐列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await TenantApplyApi.getTenantApplyPage(queryParams)
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

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 审批操作 */
const auditFormRef = ref()
const handleAudit = (row: any) => {
  auditFormRef.value.open(row.id, row.packageId)
}

/** 详情操作 */
const detailVisible = ref(false)
const detailData = ref({} as TenantApplyApi.TenantApplyVO)
const handleDetail = (row: TenantApplyApi.TenantApplyVO) => {
  detailData.value = row
  detailVisible.value = true
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获取租户套餐列表
  packageList.value = await TenantPackageApi.getTenantPackageList()
})
</script>
