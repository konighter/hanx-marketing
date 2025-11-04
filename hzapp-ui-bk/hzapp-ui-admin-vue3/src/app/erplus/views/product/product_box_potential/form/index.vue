<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form ref="formRef" :model="formData" :rules="formRules" label-width="140px" v-loading="formLoading">
      <!-- 顶部右侧操作按钮（Unocss） -->
      <div class="flex w-full justify-end gap-2 mb-3">
        <el-button type="primary" @click="submitForm" :loading="formLoading" class="px-4 py-1">保存</el-button>
        <el-button v-if="optType == 'create'" @click="resetForm" class="px-4 py-1">重置</el-button>
        <el-button  @click="closeForm" :loading="formLoading" class="px-4 py-1">返回</el-button>
      </div>
      <!-- 基本信息 -->
      <el-divider content-position="left">基本信息</el-divider>
      <el-form-item label="标题" prop="title">
        <el-input v-model="formData.title" placeholder="请输入标题" class="!w-480px" />
      </el-form-item>
      <el-form-item label="品类" prop="category">
        <el-input v-model="formData.category" placeholder="请输入品类" class="!w-480px" />
      </el-form-item>
      <el-form-item label="目标平台" prop="platformId">
        <el-select v-model="formData.platformId" clearable placeholder="请选择目标平台" class="!w-480px">
          <el-option v-for="p in sellplatformList" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>

      <!-- 卖点分析 -->
      <el-divider content-position="left">卖点分析</el-divider>
      <el-form-item label="目标人群" prop="target">
        <el-input v-model="formData.target" placeholder="目标人群" class="!w-480px" />
      </el-form-item>
      <el-form-item label="产品特性" prop="feature">
        <el-input type="textarea" v-model="formData.feature" placeholder="产品特性" rows="4" class="!w-720px" />
      </el-form-item>

      <el-form-item label="卖点" prop="sellPoints">
        <el-input type="textarea" v-model="formData.sellPoints" placeholder="卖点分析" rows="4" class="!w-720px" />
      </el-form-item>

      <!-- 竞品分析 -->
      <el-divider content-position="left">竞品分析</el-divider>
      <el-form-item label="竞品清单" prop="competitorList">

        <el-table :data="formData.competitorList" size="small" class="w-full">
          <el-table-column prop="platform" label="平台" width="120">
            <template #default="scope">
              <el-select v-model="scope.row.platform" placeholder="请选择售卖平台" clearable class="!w-80%"
                v-if="scope.row.edit" size="small">
                <el-option v-for="SellPlatform in sellplatformList" :key="SellPlatform.id" :label="SellPlatform.name"
                  :value="SellPlatform.id" />
              </el-select>

              <span v-else>{{ scope.row.platform }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="asin" label="ASIN" width="160">
            <template #default="scope">
              <template v-if="scope.row.edit">
                <el-input v-model="scope.row.asin" placeholder="请输入ASIN" class="!w-80%" size="small" />
              </template>
              <template v-else>
                <span>{{ scope.row.asin }}</span>
              </template>
            </template>
          </el-table-column>

          <el-table-column prop="listDate" label="上架日期" width="140">
            <template #default="scope">
              <template v-if="scope.row.edit">
                <el-date-picker v-model="scope.row.listDate" value-format="YYYY-MM-DD" type="date" placeholder="选择日期"
                  class="!w-80%" size="small" />
              </template>
              <template v-else>
                <span>{{ scope.row.listDate }}</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              <template v-if="scope.row.edit">
                <el-input v-model="scope.row.price" :min="0" controls-position="right" class="!w-full" size="small">
                  <template #prefix>
                    <span>$</span>
                  </template>
                </el-input>
              </template>
              <template v-else>
                <span>$ {{ scope.row.price }}</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop="monthSales" label="月均销量" width="120">
            <template #default="scope">
              <template v-if="scope.row.edit">
                <el-input-number v-model="scope.row.monthSales" :min="0" controls-position="right" class="!w-full"
                  size="small" />
              </template>
              <template v-else>
                <span>{{ scope.row.monthSales }}</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop="reviews" label="评价数" width="100">
            <template #default="scope">
              <template v-if="scope.row.edit">
                <el-input-number v-model="scope.row.reviews" :min="0" controls-position="right" class="!w-full"
                  size="small" />
              </template>
              <template v-else>
                <span>{{ scope.row.reviews }}</span>
              </template>
            </template>
          </el-table-column>
          <el-table-column prop="autoMonitor" label="自动监控" width="100">
            <template #default="scope">
              <el-switch v-model="scope.row.autoMonitor" active-color="#13ce66" :disabled="!scope.row.edit" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" align="center">
            <template #default="scope">
              <template v-if="scope.row.edit">
                <el-button type="text" @click="() => { scope.row.edit = false }" size="small">完成</el-button>
                <el-button type="text" @click="removeCompetitor(scope.$index)" size="small">删除</el-button>
              </template>
              <template v-else>
                <el-button type="text" @click="() => { scope.row.edit = true }" size="small">编辑</el-button>
              </template>

            </template>
          </el-table-column>
        </el-table>
        <div class="mt-10px">
          <el-button size="small" @click="addCompetitor">新增竞品</el-button>
        </div>
      </el-form-item>

      <!-- 运营分析 -->

      <el-divider content-position="left">运营分析</el-divider>
      <el-form-item label="成本预估" prop="financeList">
        <el-table :data="formData.financeList" size="small" class="w-full">

          <el-table-column label="目标售价" align="center" width="120">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.targetPrice" :min="0" controls-position="right" size="small"
                class="!w-full">
                <template #prefix>
                  <span>$</span>
                </template>
              </el-input-number>
              <span v-else>$ {{ row.targetPrice }}</span>
            </template>
          </el-table-column>

          <el-table-column label="采购价" align="center" width="120">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.purchasePrice" :min="0" controls-position="right"
                size="small" class="!w-full">
                <template #prefix>
                  <span>$</span>
                </template>
              </el-input-number>
              <span v-else>$ {{ row.purchasePrice }}</span>
            </template>
          </el-table-column>

          <el-table-column label="头程(国内+国际)" align="center" width="140">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.freight" :min="0" controls-position="right" size="small"
                class="!w-full">
                <template #prefix>
                  <span>$</span>
                </template>
              </el-input-number>
              <span v-else>$ {{ row.freight }}</span>
            </template>
          </el-table-column>

          <el-table-column label="佣金(率%)" align="center" width="120">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.commissionRate" :min="0" :max="100"
                controls-position="right" size="small" class="!w-full">
                <template #suffix>
                  <span>%</span>
                </template>
              </el-input-number>
              <span v-else>{{ row.commissionRate }}%</span>
            </template>
          </el-table-column>

          <el-table-column label="FBA 成本" align="center" width="120">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.fbaCost" :min="0" controls-position="right" size="small"
                class="!w-full">
                <template #prefix>
                  <span>$</span>
                </template>
              </el-input-number>
              <span v-else>$ {{ row.fbaCost }}</span>
            </template>
          </el-table-column>

          <el-table-column label="推广-CPC" align="center" width="120">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.cpc" :min="0" controls-position="right" size="small"
                class="!w-full">
                <template #prefix>
                  <span>$</span>
                </template>
              </el-input-number>
              <span v-else>$ {{ row.cpc }}</span>
            </template>
          </el-table-column>

          <el-table-column label="推广-ACOS(%)" align="center" width="140">
            <template #default="{ row }">
              <el-input-number v-if="row.edit" v-model="row.targetAcos" :min="0" :max="100" controls-position="right"
                size="small" class="!w-full">
                <template #suffix>
                  <span>%</span>
                </template>
              </el-input-number>
              <span v-else>{{ row.targetAcos }}%</span>
            </template>
          </el-table-column>
          <el-table-column label="毛利率(估算)" align="center" width="140">
            <template #default="{ row }">
              <!-- <div>{{ formatPercent(calcGrossMargin(row)) }}</div> -->
              <span v-if="!row.edit">{{ row.gpm }}%</span>
            </template>
          </el-table-column>

          <el-table-column label="净利率(%)" align="center" width="120">
            <template #default="{ row }">

              <span v-if="!row.edit">{{ row.npm }}%</span>

            </template>
          </el-table-column>

          <el-table-column label="操作" width="140" align="center">
            <template #default="{ $index, row }">
              <template v-if="row.edit">
                <el-button type="text" size="small" @click="financeRowDone(row)">完成</el-button>
                <el-button type="text" size="small" @click="removeFinance($index)">删除</el-button>
              </template>
              <template v-else>
                <el-button type="text" size="small" @click="row.edit = true">编辑</el-button>
              </template>

            </template>
          </el-table-column>
        </el-table>

        <div class="mt-3">
          <el-button size="small" @click="addFinance">新增方案</el-button>
        </div>
      </el-form-item>

      <el-form-item label="一次性投入" prop="oneTimeInvest">
        <el-input-number v-model="formData.oneTimeInvest" :min="0" class="!w-240px" controls-position="right">
          <template #prefix>
            <span>$</span>
          </template>
        </el-input-number>
      </el-form-item>


      <el-divider content-position="left">分析意见与复盘</el-divider>
      <el-form-item label="关联商品" prop="reviewProduct">
        <el-input v-model="formData.reviewProduct" placeholder="关联商品ID或SKU" class="!w-360px" />
      </el-form-item>

      <!-- 分析意见 & 复盘 -->
        
        <el-form-item label="复盘结果" prop="analysisOpinion">
          <span v-if="formData.analysisOpinion"> {{ formData.analysisOpinion}}</span>
          <span v-else class="text-sm text-gray-400" >暂无复盘结果</span>
        </el-form-item>




    </el-form>
  </ContentWrap>


  <!-- 表单弹窗：添加/修改 -->

</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMessage } from '@/hooks/web/useMessage'
import { useTagsViewStore } from '@/store/modules/tagsView'
import { SellPlatformApi, SellPlatformVO } from '@/app/erp/api/sellplatform'
import { CompetitorVO, FinanceVO, ProductPotentialVO, ProductPotentialApi } from '@/app/erplus/api/product/productBox'
import Logger from '@/utils/Logger'

/** 选品分析 表单页 */
defineOptions({ name: 'ProductAnalysisForm' })

const { t } = useI18n()
const message = useMessage()
const { query } = useRoute()
const { push, currentRoute } = useRouter() // 路由跳转
const { delView } = useTagsViewStore() // 视图操作

const formLoading = ref(false)
const formRef = ref()
const optType = ref<'create' | 'update'>('create')
const sellplatformList = ref<SellPlatformVO[]>([])

const formData = reactive<ProductPotentialVO>({
  id: undefined as number | undefined,
  title: undefined as string | undefined,
  category: undefined as string | undefined,
  categoryName: undefined as string | undefined,
  platformId: undefined as number | undefined,
  platformName: undefined as string | undefined,
  purchaseSource: undefined as string | undefined,
  feature: undefined as string | undefined, // 产品特性
  target: undefined as string | undefined, // 目标人群
  sellPoints: undefined as string | undefined, // 卖点
  competitorList: [] as Array<CompetitorVO>, // 竞品分析
  financeList: [] as Array<FinanceVO>, // 运营分析
  oneTimeInvest: 0, // 一次性投入
  analysisOpinion: undefined as string | undefined,
  reviewProduct: undefined as string | undefined,
  status: undefined as number | undefined,
  createTime: undefined,
  creator: undefined,
} as ProductPotentialVO)

const formRules = reactive({
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '品类不能为空', trigger: 'blur' }],
  platformId: [{ required: true, message: '目标平台不能为空', trigger: 'change' }],
  purchaseSource: [{ required: true, message: '采购来源不能为空', trigger: 'blur' }],
  feature: [{ required: true, message: '产品特性不能为空', trigger: 'blur' }],
  target: [{ required: true, message: '目标人群不能为空', trigger: 'blur' }],
  sellPoints: [{ required: true, message: '卖点不能为空', trigger: 'blur' }],
  competitorList: [{ required: true, type: 'array', message: '竞品清单不能为空', trigger: 'change' }],
  financeList: [{ required: true, type: 'array', message: '成本预估不能为空', trigger: 'change' }],

})

onMounted(async () => {
  await loadPlatformList()
  optType.value = (query.type as 'create' | 'update') || 'create'
  console.log('路由参数：', query)
 const idParam = query.id
  const id = Number(idParam)
  if (!Number.isNaN(id) && id > 0) {
    await loadDetail(id)
  }

})

const loadPlatformList = async () => {
  sellplatformList.value = (await SellPlatformApi.getSellPlatformListCache()) || []
}

const loadDetail = async (id: number) => {
  formLoading.value = true
  try {
    const productPotential = await ProductPotentialApi.getProductPotential(id)
    Object.assign(formData, productPotential)
    console.log('选品提案详情：', productPotential)
    console.log('加载选品提案详情：', formData)
  } catch (e) {
    console.error(e)
  } finally {
    formLoading.value = false
  }
}



/* 竞品操作 */
const addCompetitor = () => {
  formData.competitorList.push({ platform: undefined, asin: undefined, listDate: undefined, price: 0, monthSales: 0, reviews: 0, autoMonitor: false, edit: true })
}
const removeCompetitor = (index: number) => {
  formData.competitorList.splice(index, 1)
}


/* 提交 */
const submitForm = async () => {
  await formRef.value.validate()
  formLoading.value = true
  try {
    // TODO: 调用后端保存接口
    const data = formData as unknown as ProductPotentialVO
    if (optType.value === 'update')
      await ProductPotentialApi.updateProductPotential(data)
    else
      await ProductPotentialApi.createProductPotential(data)

    message.success( query.type == 'create' ? t('common.createSuccess') : t('common.updateSuccess') || '保存成功')
    closeForm()
    
  } catch (e) {
    console.error(e)
  } finally {
    formLoading.value = false
  }
}

const closeForm = () => {
  delView(unref(currentRoute))
  push({ path: '/erplusv2/product_box/potential' })
}

const resetForm = () => {
  formRef.value?.resetFields()
  // 重置部分复杂数组

  formData.competitorList = []
  formData.financeList = []

}


/* 运营方案操作（多方案表格） */
const addFinance = () => {
  const idx = formData.financeList.length + 1
  formData.financeList.push({
    targetPrice: 0,
    purchasePrice: 0,
    freight: 0,
    commissionRate: 0,
    fbaCost: 0,
    cpc: 0,
    targetAcos: 0,
    gpm: 0,
    npm: 0,
    edit: true,
  } as FinanceVO)
}
const removeFinance = (index: number) => {
  formData.financeList.splice(index, 1)
}

const financeRowDone = (row: FinanceVO) => {
  row.gpm = grossMargin(row)
  row.npm = netMargin(row)
  row.edit = false
}

/* 计算毛利率（估算） */
const grossMargin = (row: FinanceVO) => {
  Logger.prettyInfo('计算毛利率：', row)
  const revenue = Number(row.targetPrice || 0)
  const cost = Number(row.purchasePrice || 0) + Number(row.freight || 0) + Number(row.fbaCost || 0) + (revenue * (Number(row.commissionRate || 0) / 100))
  if (revenue <= 0) return 0
  return parseFloat(((((revenue - cost) / revenue) * 100)).toFixed(2))
}

const netMargin = (row: FinanceVO) => {
  const revenue = Number(row.targetPrice || 0)
  const cost = Number(row.purchasePrice || 0) + Number(row.freight || 0) + Number(row.fbaCost || 0) + (revenue * (Number(row.commissionRate || 0) / 100)) + (revenue * (Number(row.targetAcos || 0) / 100))
  if (revenue <= 0) return 0
  return parseFloat(((((revenue - cost) / revenue) * 100)).toFixed(2))
}

</script>