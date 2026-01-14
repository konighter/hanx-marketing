<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px" :rules="rules">
      <el-form-item label="仓库名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入仓库名称" clearable @keyup.enter="handleQuery"
          class="!w-240px" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型仓库类型" clearable class="!w-240px"
          @change="onWarehouseTypeChange">
          <el-option v-for="t in WarehouseTypes" :key="t.value" :label="t.lable" :value="t.value" />
        </el-select>
      </el-form-item>
      <template v-if="isWarehouseExclusive">
        <el-form-item label="平台ID" prop="platformId">

          <el-select v-model="queryParams.platformId" placeholder="请输入平台ID" clearable @change="onPlatformChange"
            class="!w-240px">
            <el-option v-for="p in platforms" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="店铺" prop="shopId">
          <el-select v-model="queryParams.shopId" placeholder="请输入店铺" clearable @change="onShopChange" class="!w-240px">
            <el-option v-for="s in shops" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>

        <!-- <el-form-item label="市场" prop="marketId">
          <el-input v-model="queryParams.marketId" placeholder="请输入市场" clearable @keyup.enter="handleQuery"
            class="!w-240px" />
        </el-form-item> -->

      </template>


      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" /> 搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" /> 重置
        </el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['erplus:warehouse:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>

      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table row-key="id" v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true"
      @selection-change="handleRowCheckboxChange">
      <!-- <el-table-column type="selection" width="55" /> -->

      <el-table-column label="仓库名称" align="center" prop="name" />
      <el-table-column label="类型" align="center" prop="type">
        <template #default="scope">
          <span>
            {{
              WarehouseTypes.find(t => t.value === scope.row.type)?.lable || '未知类型'
            }}
          </span>
        </template>

      </el-table-column>

      <template v-if="isWarehouseExclusive">
        <el-table-column label="店铺" align="center" prop="shopId" />
        <el-table-column label="平台" align="center" prop="platformId" />
        <!-- <el-table-column label="市场" align="center" prop="marketId" /> -->
      </template>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="负责人" align="center" prop="principal" />
      <el-table-column label="状态" align="center" prop="status" />
      <!-- <el-table-column label="是否默认" align="center" prop="defaultStatus" />
      <el-table-column label="创建时间" align="center" prop="createTime" :formatter="dateFormatter" width="180px" /> -->
      <el-table-column label="操作" align="center" min-width="120px">
        <template #default="scope">
          <el-button link type="primary" @click="showInventoryDetail(scope.row.id)"
            v-hasPermi="['erplus:warehouse:update']">
            详情
          </el-button>
          <el-button link type="danger" @click="handleInbound(scope.row)" v-hasPermi="['erplus:warehouse:delete']">
            入库
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
      @pagination="getList" />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <WarehouseForm ref="formRef" @success="getList" />
  <WarehouseInboundForm ref="inboundRef" :warehouse="warehouseInbound" />
  <WarehouseInventoryDetail ref="detailRef" />
</template>

<script setup lang="ts">
import { isEmpty } from '@/utils/is'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { WarehouseApi, Warehouse, WarehouseTypes } from '@/app/erplus/api/stock/warehouse'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform' // 已存在模块
import { ShopApi } from '@/app/erplus/api/system/shop'
import WarehouseForm from './WarehouseForm.vue'
import WarehouseInboundForm from './WarehouseInboundForm.vue'
import WarehouseInventoryDetail from './WarehouseInventoryDetail.vue'

/** ERP 仓库 列表 */
defineOptions({ name: 'Warehouse' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<Warehouse[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  type: undefined,
  shopId: undefined,
  platformId: undefined,
  marketId: undefined,
  remark: undefined,
  principal: undefined,
  status: undefined,
  defaultStatus: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const isWarehouseExclusive = ref(false) // 是否独享仓库

const rules = reactive({
  type: [
    {
      required: true,
      message: '仓库类型为必填项',
      trigger: 'change'
    }
  ],
  // marketId: [
  //   {
  //     required: isWarehouseExclusive,
  //     message: '市场为必填项',
  //     trigger: 'change'
  //   }
  // ],
  platformId: [
    {
      required: isWarehouseExclusive,
      message: '平台ID为必填项',
      // trigger: 'change'
    }
  ],
  shopId: [
    {
      required: isWarehouseExclusive,
      message: '店铺为必填项',
      // trigger: 'change'
    }
  ]
})


/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WarehouseApi.getWarehousePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = async () => {
  await queryFormRef.value.validate()

  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  isWarehouseExclusive.value = false;
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await WarehouseApi.deleteWarehouse(id)
    message.success(t('common.delSuccess'))
    currentRow.value = {}
    // 刷新列表
    await getList()
  } catch { }
}

/** 批量删除ERP 仓库 */
const handleDeleteBatch = async () => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    await WarehouseApi.deleteWarehouseList(checkedIds.value);
    checkedIds.value = [];
    message.success(t('common.delSuccess'))
    await getList();
  } catch { }
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (records: Warehouse[]) => {
  checkedIds.value = records.map((item) => item.id!);
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await WarehouseApi.exportWarehouse(queryParams)
    download.excel(data, 'ERP 仓库.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(async () => {
  getList()
  await loadPlatforms()
})


const onWarehouseTypeChange = (value: number) => {
  const selectedType = WarehouseTypes.find(t => t.value === value);
  if (selectedType) {
    isWarehouseExclusive.value = selectedType.exclusive;
  } else {
    isWarehouseExclusive.value = false;
  }
  // 清空平台ID和店铺
  queryParams.platformId = undefined;
  queryParams.shopId = undefined;
  queryParams.marketId = undefined;
}



const platforms = ref<SellPlatformVO[]>([])
const shops = ref<any[]>([])
const markets = ref<any[]>([])

/** 平台相关加载 */
const loadPlatforms = async () => {
  platforms.value = await SellPlatformApi.getSellPlatformListCache() || []
}

const loadShops = async (platformId: number) => {
  // 加载店铺列表 
  shops.value = await ShopApi.getPlatformShop(platformId) || []
  console.log('shops', shops.value)
}

const onPlatformChange = async function () {
  // 重置店铺和区域
  queryParams.shopId = undefined
  queryParams.marketId = undefined
  await loadShops(queryParams.platformId)
}

const onShopChange = async function () {
  // 重置区域
  queryParams.marketId = undefined

}


const inboundRef = ref() // 入库表单引用
const warehouseInbound = ref<Warehouse>()

const handleInbound = async (warehouse: Warehouse) => {
  warehouseInbound.value = warehouse;
  inboundRef.value.showDrawer();
}



// 详情页

const detailRef = ref()
const showInventoryDetail = (id: number) => {
  detailRef.value.openDetail(id)
}


</script>