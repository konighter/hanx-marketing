<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-select v-model="queryParams.platform" placeholder="请选择平台" clearable class="!w-240px">
          <el-option label="Amazon Ads" value="AMAZON" />
          <el-option label="Meta Ads" value="META" />
          <el-option label="Google Ads" value="GOOGLE" />
          <el-option label="TikTok Ads" value="TIKTOK" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="authStatus">
        <el-select v-model="queryParams.authStatus" placeholder="请选择状态" clearable class="!w-240px">
          <el-option label="已授权" :value="1" />
          <el-option label="已失效" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="handleOpenAdd">
          <Icon icon="ep:plus" class="mr-5px" /> 新增授权
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column label="平台" align="center" prop="platform">
        <!-- <template #default="scope">
          <dict-tag :type="DICT_TYPE.ADS_PLATFORM" :value="scope.row.platform" />
        </template> -->
      </el-table-column>
      <el-table-column label="账号名称" align="center" prop="name" />
      <el-table-column label="状态" align="center" prop="authStatus">
        <template #default="scope">
          <el-tag :type="scope.row.authStatus === 1 ? 'success' : 'danger'">
            {{ scope.row.authStatus === 1 ? '已授权' : '已失效' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="最后同步时间"
        align="center"
        prop="lastSyncedAt"
        :formatter="dateFormatter"
        width="180px"
      />
      <!-- <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      /> -->
      <el-table-column label="操作" align="center" width="280px">
        <template #default="scope">
          <el-button link type="primary" @click="handleShowProfiles(scope.row)" v-if="scope.row.platform === 'AMAZON'"> 查看站点 </el-button>
          <el-button link type="primary" @click="handleAuth(scope.row)"> 重新授权 </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)"> 删除 </el-button>
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

  <!-- 授权引导对话框 -->
  <el-dialog v-model="dialogVisible" title="新增广告授权" width="400px">
    <el-form :model="authForm" label-width="80px">
      <el-form-item label="广告平台">
        <el-select v-model="authForm.platform" placeholder="请选择平台">
          <el-option label="Amazon Ads" value="AMAZON" />
          <el-option label="Meta Ads" value="META" />
          <el-option label="Google Ads" value="GOOGLE" />
          <el-option label="TikTok Ads" value="TIKTOK" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属店铺">
        <el-select v-model="authForm.shopId" placeholder="请选择店铺">
          <el-option v-for="shop in shopList" :key="shop.id" :label="shop.name" :value="shop.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleGoAuth">前往授权</el-button>
    </template>
  </el-dialog>

  <!-- Amazon Profile 列表抽屉 -->
  <el-drawer v-model="profileDrawerVisible" title="亚马逊站点列表 (Profiles)" size="600px">
    <el-table :data="profileList" v-loading="profileLoading">
      <el-table-column label="Profile ID" align="center" prop="profileId" />
      <el-table-column label="站点" align="center" prop="countryCode" width="80px" />
      <el-table-column label="区域" align="center" prop="region" width="80px" />
      <el-table-column label="币种" align="center" prop="currencyCode" width="80px" />
      <el-table-column label="时区" align="center" prop="timezone" />
    </el-table>
  </el-drawer>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { AdsAuthApi } from '@/app/erplus/api/adv/ads'
import { ShopApi, ShopVO } from '@/app/erplus/api/system/shop'
import { AdsAccount, AmazonProfile } from '../types/ads'

defineOptions({ name: 'AdsAuth' })

const message = useMessage()
const loading = ref(true)
const list = ref<AdsAccount[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  platform: undefined,
  authStatus: undefined,
  createTime: []
})
const queryFormRef = ref()
const shopList = ref<ShopVO[]>([])

const profileDrawerVisible = ref(false)
const profileLoading = ref(false)
const profileList = ref<AmazonProfile[]>([])

const getShopList = async () => {
  shopList.value = await ShopApi.getShopList({})
}

const getList = async () => {
  loading.value = true
  try {
    const data = await AdsAuthApi.getAccountPage(queryParams)
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

const dialogVisible = ref(false)
const authForm = reactive({
  platform: 'AMAZON',
  shopId: undefined
})

const handleOpenAdd = () => {
  authForm.shopId = undefined
  dialogVisible.value = true
}

const handleGoAuth = async () => {
  if (!authForm.shopId) {
    message.error('请选择所属店铺')
    return
  }
  try {
    const url = await AdsAuthApi.getAuthorizeUrl({
      platform: authForm.platform,
      shopId: authForm.shopId
    })
    window.open(url, '_blank')
    dialogVisible.value = false
    message.success('已打开授权页面，请在授权完成后刷新列表')
  } catch (error) {}
}

const handleAuth = async (row: AdsAccount) => {
  const url = await AdsAuthApi.getAuthorizeUrl({
    platform: row.platform,
    shopId: row.shopId || 1 // 理论上应该有 shopId
  })
  window.open(url, '_blank')
}

const handleDelete = async (id: number) => {
  try {
    await message.confirm('确认删除该广告账号授权吗？删除后将停止同步。')
    await AdsAuthApi.deleteAccount(id)
    message.success('删除成功')
    await getList()
  } catch (error) {}
}

const handleShowProfiles = async (row: AdsAccount) => {
  profileDrawerVisible.value = true
  profileLoading.value = true
  try {
    profileList.value = await AdsAuthApi.getAmazonProfileList(row.id)
  } finally {
    profileLoading.value = false
  }
}

onMounted(() => {
  getList()
  getShopList()
})
</script>
