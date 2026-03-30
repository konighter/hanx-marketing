<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery"
          class="!w-240px" />
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-select
v-model="queryParams.platform" placeholder="请选择平台" clearable class="!w-240px"
          @change="sellPlatformChange">
          <el-option v-for="p in sellPlatform" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="区域" prop="region">
        <el-select v-model="queryParams.region" placeholder="请选择区域" clearable class="!w-240px">
          <el-option v-for="p in sellZone" :key="p.id" :label="p.zoneName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable class="!w-240px">
          <el-option
v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
v-model="queryParams.createTime" value-format="YYYY-MM-DD HH:mm:ss" type="daterange"
          start-placeholder="开始日期" end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]" class="!w-240px" />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="warning" plain @click="openPlatformAuthForm">
          <Icon icon="ep:key" class="mr-5px" /> 新增授权
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="ID" align="center" prop="id" />
      <el-table-column label="名称" align="left" prop="name" min-width="150"/>
      <el-table-column label="平台" align="center" prop="platformName" />
      <el-table-column label="国家" align="center" prop="countryCode" />
      <el-table-column label="授权状态" align="center" width="120px">
        <template #default="scope">
          <div class="flex flex-wrap gap-1 justify-center cursor-pointer" @click="showAuthDetails(scope.row)">
            <template v-if="scope.row.auths && scope.row.auths.length > 0">
              <el-tag 
                v-for="type in getAuthTypes(scope.row.auths)" 
                :key="type.scope"
                :type="type.hasValid ? 'success' : 'danger'"
                size="small"
              >
                {{ type.label }}
              </el-tag>
            </template>
            <el-tag v-else type="info" size="small">未授权</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" />
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="handleRefreshAuth(scope.row.id)" v-hasPermi="['ov:shop:update']">
            刷新授权
          </el-button>
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['ov:shop:update']">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['ov:shop:delete']">
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

  <!-- 表单弹窗：添加/修改 -->
  <ShopForm ref="formRef" @success="getList" />
  <ShopAuthForm ref="authRef" @success="getList" />
  <PlatformAuthForm ref="platformAuthRef" @success="getList" />

  <!-- 授权详情弹窗 -->
  <el-dialog v-model="authDetailsVisible" title="授权详情" width="500px">
    <el-table :data="currentShopAuths" stripe border>
      <el-table-column label="授权类别" align="center" prop="authScope">
        <template #default="scope">
          <el-tag>{{ getAuthLabel(scope.row.authScope) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
            {{ scope.row.status === 0 ? '有效' : '已失效' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" align="center" prop="expiryTime" :formatter="dateFormatter" width="180px" />
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { ShopApi, ShopVO } from '@/app/erplus/api/system/shop'
import ShopForm from './ShopForm.vue'
import ShopAuthForm from './ShopAuthForm.vue'
import PlatformAuthForm from './PlatformAuthForm.vue'
import { SellPlatformVO, SellPlatformApi } from "@/app/erp/api/sellplatform";
import { SellZoneApi, SellZoneVO } from "@/app/erp/api/sellzone";
import { PlatformAuthApi } from '@/app/erplus/api/authorization'

/** 店铺信息 列表 */
defineOptions({ name: 'Shop' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<ShopVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  platform: undefined,
  region: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

const authDetailsVisible = ref(false)
const currentShopAuths = ref<any[]>([])

const showAuthDetails = (row: ShopVO) => {
  currentShopAuths.value = row.auths || []
  authDetailsVisible.value = true
}

const getAuthLabel = (authType: string, short = false) => {
  if (authType === 'spapi') return short ? 'SP' : 'SP-API'
  if (authType === 'adv') return short ? 'AD' : 'Advertising'
  return authType.toUpperCase()
}

const getAuthTypes = (auths: any[]) => {
  if (!auths || auths.length === 0) return []
  const map = new Map<string, any>()
  auths.forEach((a) => {
    const scope = a.authType
    if (!map.has(scope)) {
      map.set(scope, {
        scope,
        label: getAuthLabel(scope, true),
        hasValid: a.status === 0,
        count: 1
      })
    } else {
      const existing = map.get(scope)
      existing.count++
      if (a.status === 0) {
        existing.hasValid = true
      }
    }
  })
  return Array.from(map.values()).map((t) => ({
    ...t,
    label: t.count > 1 ? `${t.label}(${t.count})` : t.label
  }))
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ShopApi.getShopPage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const authRef = ref()

const handleRefreshAuth = async (id: number) => {
  try {
    loading.value = true
    await PlatformAuthApi.refreshAuth(id)
    message.success('刷新授权成功')
    await getList()
  } catch (e) {
  } finally {
    loading.value = false
  }
}

const platformAuthRef = ref()
const openPlatformAuthForm = () => {
  platformAuthRef.value.open()
}




/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ShopApi.deleteShop(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch { }
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await ShopApi.exportShop(queryParams)
    download.excel(data, '店铺信息.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

const sellPlatform = ref<SellPlatformVO[]>([])
const sellZone = ref<SellZoneVO[]>([])

const sellPlatformChange = async () => {
  sellZone.value = await SellZoneApi.getSellZoneList({ platformId: queryParams.platform })
}

/** 初始化 **/
onMounted(async () => {
  await getList()

  sellPlatform.value = await SellPlatformApi.getSellPlatformList({})
})
</script>
