
<template>

<ContentWrap>


  <el-descriptions title="商品详情">
    <template #extra>
      <el-button @click="back">返 回</el-button>
    </template>
    <el-descriptions-item label="名称"> {{ spuDetail.name }}</el-descriptions-item>
    <el-descriptions-item label="品类">{{spuDetail.categoryId}}</el-descriptions-item>
    <el-descriptions-item label="品牌">{{spuDetail.brandId}}</el-descriptions-item>
    <el-descriptions-item label="规格"> {{ spuDetail.specType }}</el-descriptions-item>
    <el-descriptions-item label="单位">{{spuDetail.unit}}</el-descriptions-item>
    <el-descriptions-item label="关键字">{{spuDetail.keyword}}</el-descriptions-item>
    <el-descriptions-item label="成本价">{{spuDetail.costPrice}}</el-descriptions-item>
    <el-descriptions-item label="库存量">{{spuDetail.stock}}</el-descriptions-item>
  </el-descriptions>
</ContentWrap>

  <el-divider  />

  <!-- 列表 -->
  <ContentWrap>
    <el-row class="mb-3">
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >

      <el-form-item label="平台" prop="platform">
        <el-select v-model="queryParams.platform" placeholder="请选择平台类型" @change="sellPlatformChange" clearable >
          <el-option v-for="p in sellPlatform" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="区域" prop="sellZone">
        <el-select v-model="queryParams.sellZone" placeholder="请选择区域" @change="zoneChange" clearable>
          <el-option v-for="p in sellZone" :key="p.id" :label="p.zoneName" :value="p.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="店铺" prop="shopId">
        <el-select multiple v-model="queryParams.shopId" placeholder="请选择店铺" clearable>
          <el-option v-for="p in shops" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable>
          <el-option v-for="dict in getIntDictOptions(DICT_TYPE.OV_PROD_CLAIMSYNC_STATUS)"
                     :key="dict.value"
                     :label="dict.label" :value="dict.value" />

        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getList">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
      </el-form-item>
    </el-form>

    </el-row>

    <el-row class="mb-3">
       <el-button v-hasPermi="['erp:product-claim:create']" type="success" @click="claim" >新 增</el-button>
      <el-button v-hasPermi="['erp:product-claim:sync']" type="primary" @click="batchSync" >发 布</el-button>
    </el-row>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" @selectionChange="handleSelectionChange">

      <el-table-column type="selection" width="55" />
      <el-table-column type="expand" >
        <template #default="{row}">
          <SkuList is-activity-component="true" :prop-form-data="row" />

        </template>


      </el-table-column>



      <el-table-column label="变种类型" align="center" prop="specType" >

        <template #default="{row}">
          {{ getDictLabel(DICT_TYPE.OV_SPEC_TYPE, row.specType? 1 : 0)  }}
        </template>
      </el-table-column>
      <el-table-column label="平台" align="center" prop="platformName" />
      <el-table-column label="语言" align="center" prop="language" >

        <template #default="{row}">
          {{ getDictLabel(DICT_TYPE.OV_LANGUAGE_TYPE, row.language)  }}
        </template>

      </el-table-column>
      <el-table-column label="站点" align="center" prop="sellZoneName" />
      <el-table-column label="品类" align="center" prop="category" />
      <el-table-column label="品牌" align="center" prop="brandName" />
<!--      <el-table-column label="售价(max)" align="center" prop="sellPrice" />-->
      <el-table-column label="币种" align="center" prop="currency" >
        <template #default="{row}">
          {{ getDictLabel(DICT_TYPE.OV_CURRENCY, row.currency)  }}
        </template>

      </el-table-column>
<!--      <el-table-column label="扩展信息" align="center" prop="extra" />-->
      <el-table-column label="状态" align="center" prop="status" >

        <template #default="scope">
          <dict-tag :type="DICT_TYPE.OV_PROD_CLAIMSYNC_STATUS" :value="scope.row.status" />
        </template>

      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        :formatter="dateFormatter"
        width="180px"
      />
      <el-table-column label="操作" align="left" min-width="150px">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="claim('update', scope.row.id)"
            v-hasPermi="['erp:product-claim:update']"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.status === 0 || scope.row.status === 9"
            link
            type="primary"
            @click="syncClaim(scope.row.id)"
            v-hasPermi="['erp:product-claim:delete']"
          >
            发布
          </el-button>
          <el-button
            v-if="scope.row.status === 0 || scope.row.status === 1"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['erp:product-claim:delete']"
          >
            下架
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
  <ProductClaimFormV1 ref="formRef" @success="getList" />

</template>


<script setup lang="ts">

import { dateFormatter } from '@/utils/formatTime'
import { ProductClaimApi, ProductClaimVO } from '@/app/erp/api/claim'
import ProductClaimFormV1 from './ProductClaimFormV1.vue'
import {Spu, getSpu} from "@/app/erp/api/spu";
import {useTagsViewStore} from "@/store/modules/tagsView";
import {DICT_TYPE, getDictLabel, getIntDictOptions, getStrDictOptions} from "@/utils/dict";
import SkuList from "@/app/erp/views/product/spu/claim/components/SkuList.vue";
import {ShopApi, ShopVO} from "@/app/erp/api/shop";
import {SellZoneApi, SellZoneVO} from "@/app/erp/api/sellzone";
import {SellPlatformApi, SellPlatformVO} from "@/app/erp/api/sellplatform";

/** 商品认领 列表 */
defineOptions({ name: 'ProductClaim' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const { push, currentRoute } = useRouter() // 路由
const { params } = useRoute() // 查询参数
const { delView } = useTagsViewStore() // 视图操作

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  spuId: params.id,
  platform: undefined,
  sellZone: undefined,
  shopId: undefined,
  status: undefined,
})

const resetQuery = () => {
  queryParams.platform = undefined
  queryParams.sellZone = undefined
  queryParams.shopId = undefined
  queryParams.status = undefined
  getList()
}


const spuDetail = ref<Spu>({
  id:0,
  name: '', // 商品名称
  categoryId : 0, // 商品分类
  brandId : 0, // 商品品牌编号
  specType : false, // 商品规格
  unitId : 0, // 商品单位
  keyword : '', // 关键字
  costPrice: 0, // 成本价
  stock : 0, // 商品库存
})


const loading = ref(false) // 列表的加载中
const list = ref<ProductClaimVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数


const getSpuDetail = async () => {
  const spuData = await getSpu(params.id)
  spuDetail.value = spuData
}

/** 查询列表 */
const getList = async () => {
  queryParams.spuId = params.id as unknown as number
  console.log(queryParams)
  loading.value = true
  try {
    const data = await ProductClaimApi.getProductClaimPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}


/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const claim = () => {
  formRef.value.claim(spuDetail.value.id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ProductClaimApi.deleteProductClaim(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
  getSpuDetail()
})

/** 关闭按钮 */
const back = () => {
  delView(unref(currentRoute))
  push({ name: 'ProductSpu' })
}


/** 多选 */

const selectClaimShop = ref<ProductClaimVO[]>([])
const handleSelectionChange = (data: ProductClaimVO[]) => {
  selectClaimShop.value = data
  console.log(JSON.stringify(selectClaimShop.value))
}

/** 同步 */
const batchSync = () => {
  if (selectClaimShop.value.length === 0) {
    message.alertWarning("请选择需要同步的店铺")
    return
  }

  if (selectClaimShop.value.find(i => i.status === 1) !== undefined) {
    message.alertWarning("存在已同步店铺, 请重新选择")
    return
  }
  // todo
}

const syncClaim = (claimId: number) => {

}

// 下拉框联动
const sellPlatform = ref<SellPlatformVO[]>([])
const sellZone     = ref<SellZoneVO[]>([])
const shops        = ref<ShopVO[]>([])

const sellPlatformChange = async () => {
  queryParams.sellZone = undefined
  queryParams.shopId = []

  if (queryParams.platform) {
    sellZone.value = await SellZoneApi.getSellZoneList({ platformId: queryParams.platform })
  }
}

const zoneChange = async () => {
  queryParams.shopId = []
  if (queryParams.sellZone) {
    shops.value = await ShopApi.getShopList({ platformId: queryParams.platform,  region: queryParams.sellZone, status: 0})
  }
}

onMounted(async () => {
  sellPlatform.value = await SellPlatformApi.getSellPlatformList({})



})



</script>


<style scoped lang="scss">

</style>
