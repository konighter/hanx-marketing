<!-- 跨境电商ERP - 平台商品同步 -->
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
      <el-form-item label="商品名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入商品名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="SKU" prop="sku">
        <el-input
          v-model="queryParams.sku"
          placeholder="请输入SKU"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-select
          v-model="queryParams.platform"
          placeholder="请选择平台"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in platformOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="同步状态" prop="syncStatus">
        <el-select
          v-model="queryParams.syncStatus"
          placeholder="请选择同步状态"
          clearable
          class="!w-240px"
        >
          <el-option
            v-for="item in syncStatusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="handleBatchSync"
          :disabled="selectedProducts.length === 0"
        >
          <Icon icon="ep:upload" class="mr-5px" /> 批量同步
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table 
      v-loading="loading" 
      :data="list" 
      :stripe="true" 
      :show-overflow-tooltip="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="商品图片" align="center" width="100">
        <template #default="scope">
          <el-image 
            style="width: 60px; height: 60px" 
            :src="scope.row.imageUrl || 'https://via.placeholder.com/60'"
            :preview-src-list="scope.row.imageUrl ? [scope.row.imageUrl] : []"
            fit="cover"
            :preview-teleported=true
          />
        </template>
      </el-table-column>
      <el-table-column label="SKU" align="center" prop="sku" width="120" />
      <el-table-column label="名称" align="left" prop="name" min-width="200" show-overflow-tooltip />
      <el-table-column label="平台同步状态" align="center" min-width="300">
        <template #default="scope">
          <div class="flex flex-wrap gap-1">
            <el-tag 
              v-for="platform in scope.row.platforms" 
              :key="platform.code"
              :type="getSyncStatusType(platform.syncStatus)"
              class="mb-1 mr-1"
            >
              {{ platform.name }}: {{ getSyncStatusText(platform.syncStatus) }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="最后同步时间" align="center" prop="lastSyncTime" width="180" />
      <el-table-column label="操作" align="center" width="250">
        <template #default="scope">
          <el-button link type="primary" @click="handleSync(scope.row)">
            同步
          </el-button>
          <el-button link type="primary" @click="handlePlatformSelect(scope.row)">
            选择平台
          </el-button>
          <el-button link type="primary" @click="handleViewPlatformDetail(scope.row)">
            查看详情
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

  <!-- 平台选择弹窗 -->
  <el-dialog title="选择同步平台" v-model="platformDialogVisible" width="500px">
    <el-form>
      <el-form-item label="商品">
        <div>{{ currentProduct.name }}</div>
      </el-form-item>
      <el-form-item label="选择平台">
        <el-checkbox-group v-model="selectedPlatforms">
          <el-checkbox 
            v-for="platform in platformOptions" 
            :key="platform.value" 
            :label="platform.value"
          >
            {{ platform.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="platformDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirmPlatformSelect">确 定</el-button>
    </template>
  </el-dialog>

  <!-- 平台详情弹窗 -->
  <el-dialog title="平台商品详情" v-model="platformDetailVisible" width="800px">
    <el-tabs v-model="activePlatformTab">
      <el-tab-pane 
        v-for="platform in currentProductPlatforms" 
        :key="platform.code" 
        :label="platform.name"
        :name="platform.code"
      >
        <el-descriptions :column="2" border>
          <el-descriptions-item label="平台SKU">{{ platform.platformSku || '-' }}</el-descriptions-item>
          <el-descriptions-item label="平台商品ID">{{ platform.platformProductId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="平台店铺">{{ platform.shopName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="同步状态">
            <el-tag :type="getSyncStatusType(platform.syncStatus)">
              {{ getSyncStatusText(platform.syncStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="平台价格">{{ platform.price ? `${platform.currencySymbol}${platform.price}` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="平台库存">{{ platform.stock !== undefined ? platform.stock : '-' }}</el-descriptions-item>
          <el-descriptions-item label="最后同步时间">{{ platform.lastSyncTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="同步错误信息" :span="2">
            <div class="text-red-500">{{ platform.errorMessage || '-' }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <div class="mt-4">
          <el-button type="primary" @click="handleSyncPlatform(platform.code)">
            同步到{{ platform.name }}
          </el-button>
          <el-button type="primary" @click="handleEditPlatformMapping(platform)">
            编辑映射
          </el-button>
          <el-button type="danger" @click="handleRemovePlatform(platform.code)">
            移除平台
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>

  <!-- 平台映射编辑弹窗 -->
  <el-dialog title="编辑平台映射" v-model="mappingDialogVisible" width="600px">
    <el-form :model="mappingForm" label-width="120px">
      <el-form-item label="平台SKU">
        <el-input v-model="mappingForm.platformSku" placeholder="请输入平台SKU" />
      </el-form-item>
      <el-form-item label="平台分类">
        <el-cascader
          v-model="mappingForm.platformCategory"
          :options="platformCategoryOptions"
          :props="{ checkStrictly: true }"
          clearable
          placeholder="请选择平台分类"
          class="w-full"
        />
      </el-form-item>
      <el-form-item label="平台价格">
        <el-input-number 
          v-model="mappingForm.price" 
          :precision="2" 
          :step="0.1" 
          :min="0"
          class="w-full"
        />
      </el-form-item>
      <el-form-item label="平台库存策略">
        <el-radio-group v-model="mappingForm.stockStrategy">
          <el-radio :label="'sync'">同步实际库存</el-radio>
          <el-radio :label="'fixed'">固定库存</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="固定库存" v-if="mappingForm.stockStrategy === 'fixed'">
        <el-input-number 
          v-model="mappingForm.fixedStock" 
          :precision="0" 
          :step="1" 
          :min="0"
          class="w-full"
        />
      </el-form-item>
      <el-form-item label="标题模板">
        <el-input v-model="mappingForm.titleTemplate" placeholder="例如：{{name}} - {{brand}}" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="mappingDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="savePlatformMapping">保 存</el-button>
    </template>
  </el-dialog>

  <!-- 批量同步弹窗 -->
  <el-dialog title="批量同步" v-model="batchSyncDialogVisible" width="500px">
    <el-form>
      <el-form-item label="已选商品数">
        <div>{{ selectedProducts.length }}个</div>
      </el-form-item>
      <el-form-item label="选择目标平台">
        <el-checkbox-group v-model="batchSelectedPlatforms">
          <el-checkbox 
            v-for="platform in platformOptions" 
            :key="platform.value" 
            :label="platform.value"
          >
            {{ platform.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="batchSyncDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirmBatchSync" :loading="batchSyncLoading">开始同步</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'

/** 跨境电商ERP - 平台商品同步 */
defineOptions({ name: 'ProductSync' })

const message = useMessage() // 消息弹窗

// 定义类型
interface PlatformInfo {
  code: string
  name: string
  syncStatus: string
  platformSku?: string
  platformProductId?: string
  shopName?: string
  price?: number
  currencySymbol?: string
  stock?: number
  lastSyncTime?: string
  errorMessage?: string
}

interface Product {
  id: number
  name: string
  sku: string
  imageUrl: string
  lastSyncTime?: string
  platforms: PlatformInfo[]
}

// 列表相关
const loading = ref(false)
const list = ref<Product[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined as string | undefined,
  sku: undefined as string | undefined,
  platform: undefined as string | undefined,
  syncStatus: undefined as string | undefined
})
const queryFormRef = ref()

// 平台选项
const platformOptions = [
  { label: '亚马逊', value: 'amazon' },
  { label: 'eBay', value: 'ebay' },
  { label: 'Shopee', value: 'shopee' },
  { label: 'Lazada', value: 'lazada' },
  { label: 'Wish', value: 'wish' }
]

// 同步状态选项
const syncStatusOptions = [
  { label: '未同步', value: 'not_synced' },
  { label: '同步成功', value: 'success' },
  { label: '同步失败', value: 'failed' },
  { label: '同步中', value: 'syncing' }
]

// 选中的商品
const selectedProducts = ref<Product[]>([])

// 平台选择弹窗
const platformDialogVisible = ref(false)
const currentProduct = ref<Partial<Product>>({})
const selectedPlatforms = ref<string[]>([])

// 平台详情弹窗
const platformDetailVisible = ref(false)
const activePlatformTab = ref('')
const currentProductPlatforms = ref<PlatformInfo[]>([])

// 平台映射编辑弹窗
const mappingDialogVisible = ref(false)
const mappingForm = reactive({
  platformCode: '',
  platformSku: '',
  platformCategory: [],
  price: 0,
  stockStrategy: 'sync',
  fixedStock: 100,
  titleTemplate: ''
})

// 平台分类选项（示例数据）
const platformCategoryOptions = [
  {
    value: 'electronics',
    label: '电子产品',
    children: [
      {
        value: 'phones',
        label: '手机',
        children: [
          { value: 'smartphones', label: '智能手机' },
          { value: 'accessories', label: '手机配件' }
        ]
      },
      {
        value: 'computers',
        label: '电脑',
        children: [
          { value: 'laptops', label: '笔记本电脑' },
          { value: 'desktops', label: '台式电脑' }
        ]
      }
    ]
  },
  {
    value: 'clothing',
    label: '服装',
    children: [
      { value: 'mens', label: '男装' },
      { value: 'womens', label: '女装' },
      { value: 'kids', label: '童装' }
    ]
  }
]

// 批量同步弹窗
const batchSyncDialogVisible = ref(false)
const batchSelectedPlatforms = ref<string[]>([])
const batchSyncLoading = ref(false)

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 模拟数据，实际项目中应该调用API
    setTimeout(() => {
      list.value = [
        {
          id: 1,
          name: 'Apple iPhone 13 Pro Max',
          sku: 'IP13PM-256-GR',
          imageUrl: 'https://m.media-amazon.com/images/I/61i8Vjb17SL._AC_SX679_.jpg',
          lastSyncTime: '2023-06-10 15:30:22',
          platforms: [
            { code: 'amazon', name: '亚马逊', syncStatus: 'success', platformSku: 'AMZN-IP13PM-256-GR', platformProductId: 'B09G9HD6PD', shopName: 'TechGadgets', price: 899.99, currencySymbol: '$', stock: 45, lastSyncTime: '2023-06-10 15:30:22' },
            { code: 'ebay', name: 'eBay', syncStatus: 'success', platformSku: 'EBAY-IP13PM-256-GR', platformProductId: '384358217383', shopName: 'tech_gadgets_store', price: 909.99, currencySymbol: '$', stock: 42, lastSyncTime: '2023-06-09 11:20:15' },
            { code: 'shopee', name: 'Shopee', syncStatus: 'failed', platformSku: 'SHPE-IP13PM-256-GR', platformProductId: '', shopName: 'TechGadgetsOfficial', price: 0, currencySymbol: '$', stock: 0, lastSyncTime: '2023-06-08 09:45:33', errorMessage: '商品图片上传失败，请检查图片格式' }
          ]
        },
        {
          id: 2,
          name: 'Samsung Galaxy S21 Ultra 5G',
          sku: 'SGS21U-256-BK',
          imageUrl: 'https://m.media-amazon.com/images/I/61bLefD79-L._AC_SX679_.jpg',
          lastSyncTime: '2023-06-09 10:15:40',
          platforms: [
            { code: 'amazon', name: '亚马逊', syncStatus: 'success', platformSku: 'AMZN-SGS21U-256-BK', platformProductId: 'B08N3J7GHL', shopName: 'TechGadgets', price: 799.99, currencySymbol: '$', stock: 38, lastSyncTime: '2023-06-09 10:15:40' },
            { code: 'lazada', name: 'Lazada', syncStatus: 'syncing', platformSku: 'LZDA-SGS21U-256-BK', platformProductId: '1234567890', shopName: 'TechGadgetsAsia', price: 12999.00, currencySymbol: '₱', stock: 25, lastSyncTime: '2023-06-09 10:10:22' }
          ]
        },
        {
          id: 3,
          name: 'Sony WH-1000XM4 无线降噪耳机',
          sku: 'SWXM4-BK',
          imageUrl: 'https://m.media-amazon.com/images/I/71o8Q5XJS5L._AC_SX679_.jpg',
          lastSyncTime: '2023-06-08 16:45:12',
          platforms: [
            { code: 'amazon', name: '亚马逊', syncStatus: 'success', platformSku: 'AMZN-SWXM4-BK', platformProductId: 'B0863TXGM3', shopName: 'TechGadgets', price: 299.99, currencySymbol: '$', stock: 120, lastSyncTime: '2023-06-08 16:45:12' },
            { code: 'ebay', name: 'eBay', syncStatus: 'success', platformSku: 'EBAY-SWXM4-BK', platformProductId: '264774650475', shopName: 'tech_gadgets_store', price: 309.99, currencySymbol: '$', stock: 115, lastSyncTime: '2023-06-08 14:30:05' },
            { code: 'wish', name: 'Wish', syncStatus: 'not_synced', platformSku: '', platformProductId: '', shopName: '', price: 0, currencySymbol: '$', stock: 0, lastSyncTime: '' }
          ]
        }
      ]
      total.value = 3
      loading.value = false
    }, 500)
  } catch (error) {
    console.error(error)
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

/** 表格多选框选中数据 */
const handleSelectionChange = (selection) => {
  selectedProducts.value = selection
}

/** 获取同步状态文本 */
const getSyncStatusText = (status) => {
  switch (status) {
    case 'success': return '同步成功'
    case 'failed': return '同步失败'
    case 'syncing': return '同步中'
    case 'not_synced': return '未同步'
    default: return '未知状态'
  }
}

/** 获取同步状态类型（用于标签颜色） */
const getSyncStatusType = (status) => {
  switch (status) {
    case 'success': return 'success'
    case 'failed': return 'danger'
    case 'syncing': return 'warning'
    case 'not_synced': return 'info'
    default: return 'info'
  }
}

/** 同步按钮操作 */
const handleSync = (row) => {
  // 如果商品已经有关联的平台，直接同步到所有平台
  if (row.platforms && row.platforms.length > 0) {
    syncProduct(row.id, row.platforms.map(p => p.code))
  } else {
    // 否则打开平台选择弹窗
    currentProduct.value = row
    selectedPlatforms.value = []
    platformDialogVisible.value = true
  }
}

/** 平台选择按钮操作 */
const handlePlatformSelect = (row) => {
  currentProduct.value = row
  // 预选已关联的平台
  selectedPlatforms.value = row.platforms ? row.platforms.map(p => p.code) : []
  platformDialogVisible.value = true
}

/** 确认平台选择 */
const confirmPlatformSelect = () => {
  if (selectedPlatforms.value.length === 0) {
    message.warning('请至少选择一个平台')
    return
  }

  // 模拟保存平台关联关系，实际项目中应该调用API
  const productId = currentProduct.value.id
  const platforms = selectedPlatforms.value
  
  // 更新当前商品的平台列表
  const productIndex = list.value.findIndex(item => item.id === productId)
  if (productIndex !== -1) {
    const currentPlatforms = list.value[productIndex].platforms || []
    const existingPlatformCodes = currentPlatforms.map(p => p.code)
    
    // 添加新选择的平台
    platforms.forEach(code => {
      if (!existingPlatformCodes.includes(code)) {
        const platformOption = platformOptions.find(p => p.value === code)
        currentPlatforms.push({
          code,
          name: platformOption ? platformOption.label : code,
          syncStatus: 'not_synced',
          platformSku: '',
          platformProductId: '',
          shopName: '',
          price: 0,
          currencySymbol: '$',
          stock: 0,
          lastSyncTime: ''
        })
      }
    })
    
    // 移除取消选择的平台
    list.value[productIndex].platforms = currentPlatforms.filter(p => platforms.includes(p.code))
  }
  
  platformDialogVisible.value = false
  message.success('平台设置成功')
}

/** 查看平台详情按钮操作 */
const handleViewPlatformDetail = (row) => {
  if (!row.platforms || row.platforms.length === 0) {
    message.warning('该商品尚未关联任何平台')
    return
  }
  
  currentProductPlatforms.value = row.platforms
  activePlatformTab.value = row.platforms[0].code
  platformDetailVisible.value = true
}

/** 同步到指定平台 */
const handleSyncPlatform = (platformCode) => {
  const productId = currentProductPlatforms.value[0]?.productId || 0
  syncProduct(productId, [platformCode])
}

/** 编辑平台映射 */
const handleEditPlatformMapping = (platform) => {
  mappingForm.platformCode = platform.code
  mappingForm.platformSku = platform.platformSku || ''
  mappingForm.platformCategory = []
  mappingForm.price = platform.price || 0
  mappingForm.stockStrategy = 'sync'
  mappingForm.fixedStock = platform.stock || 100
  mappingForm.titleTemplate = '{{name}} - {{brand}}'
  
  mappingDialogVisible.value = true
}

/** 保存平台映射 */
const savePlatformMapping = () => {
  // 模拟保存映射，实际项目中应该调用API
  const platformCode = mappingForm.platformCode
  const platformIndex = currentProductPlatforms.value.findIndex(p => p.code === platformCode)
  
  if (platformIndex !== -1) {
    // 更新平台信息
    currentProductPlatforms.value[platformIndex].platformSku = mappingForm.platformSku
    currentProductPlatforms.value[platformIndex].price = mappingForm.price
    
    // 如果是固定库存策略，更新库存值
    if (mappingForm.stockStrategy === 'fixed') {
      currentProductPlatforms.value[platformIndex].stock = mappingForm.fixedStock
    }
  }
  
  mappingDialogVisible.value = false
  message.success('映射保存成功')
}

/** 移除平台 */
const handleRemovePlatform = async (platformCode) => {
  try {
    await message.confirm(`确认要移除${platformOptions.find(p => p.value === platformCode)?.label || '该平台'}吗？`)
    
    // 从当前产品的平台列表中移除
    currentProductPlatforms.value = currentProductPlatforms.value.filter(p => p.code !== platformCode)
    
    // 如果没有平台了，关闭详情弹窗
    if (currentProductPlatforms.value.length === 0) {
      platformDetailVisible.value = false
      message.success('平台已移除')
      return
    }
    
    // 切换到第一个平台标签
    activePlatformTab.value = currentProductPlatforms.value[0].code
    
    // 更新商品列表中的平台信息
    const productId = currentProduct.value.id
    const productIndex = list.value.findIndex(item => item.id === productId)
    if (productIndex !== -1) {
      list.value[productIndex].platforms = list.value[productIndex].platforms.filter(p => p.code !== platformCode)
    }
    
    message.success('平台已移除')
  } catch (error) {
    console.error(error)
  }
}

/** 批量同步按钮操作 */
const handleBatchSync = () => {
  if (selectedProducts.value.length === 0) {
    message.warning('请至少选择一个商品')
    return
  }
  
  batchSelectedPlatforms.value = []
  batchSyncDialogVisible.value = true
}

/** 确认批量同步 */
const confirmBatchSync = () => {
  if (batchSelectedPlatforms.value.length === 0) {
    message.warning('请至少选择一个目标平台')
    return
  }
  
  batchSyncLoading.value = true
  
  // 模拟批量同步操作，实际项目中应该调用API
  setTimeout(() => {
    const productIds = selectedProducts.value.map(item => item.id)
    const platforms = batchSelectedPlatforms.value
    
    // 更新同步状态
    list.value.forEach(item => {
      if (productIds.includes(item.id)) {
        item.platforms.forEach(platform => {
          if (platforms.includes(platform.code)) {
            platform.syncStatus = 'success'
            platform.lastSyncTime = new Date().toLocaleString()
          }
        })
      }
    })
    
    batchSyncLoading.value = false
    batchSyncDialogVisible.value = false
    message.success(`已成功同步${productIds.length}个商品到${platforms.length}个平台`)
  }, 2000)
}

/** 同步商品到指定平台 */
const syncProduct = (productId, platforms) => {
  if (!productId || !platforms || platforms.length === 0) {
    message.warning('同步参数错误')
    return
  }
  
  // 模拟同步操作，实际项目中应该调用API
  message.loading(`正在同步商品到${platforms.length}个平台...`)
  
  setTimeout(() => {
    // 更新同步状态
    const productIndex = list.value.findIndex(item => item.id === productId)
    if (productIndex !== -1) {
      platforms.forEach(platformCode => {
        const platformIndex = list.value[productIndex].platforms.findIndex(p => p.code === platformCode)
        if (platformIndex !== -1) {
          list.value[productIndex].platforms[platformIndex].syncStatus = 'success'
          list.value[productIndex].platforms[platformIndex].lastSyncTime = new Date().toLocaleString()
        }
      })
      
      // 更新最后同步时间
      list.value[productIndex].lastSyncTime = new Date().toLocaleString()
    }
    
    message.success(`同步成功`)
  }, 1500)
}

// 初始化
onMounted(async () => {
  // 获取商品列表
  getList()
})
</script>

<style scoped>
.platform-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.platform-status {
  display: flex;
  flex-wrap: wrap;
}
</style>