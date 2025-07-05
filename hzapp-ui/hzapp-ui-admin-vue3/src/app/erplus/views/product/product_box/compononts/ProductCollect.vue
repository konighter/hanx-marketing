<!-- 跨境电商ERP - 商品收集 -->
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
      <el-form-item label="平台" prop="platformType">
        <el-select v-model="queryParams.platformType" placeholder="请选择平台" clearable class="!w-240px">
          <el-option v-for="item in platformOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入关键词"
          clearable
          @keyup.enter="handleSearch"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleSearch"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" @click="handleCollect" :loading="collectLoading">
          <Icon icon="ep:download" class="mr-5px" /> 采集
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品图片" align="center" width="100">
        <template #default="scope">
          <el-image 
            style="width: 60px; height: 60px" 
            :src="scope.row.imageUrl" 
            :preview-src-list="[scope.row.imageUrl]"
            fit="cover"
            :preview-teleported=true
          />
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="left" prop="name" min-width="200" show-overflow-tooltip />
      <el-table-column label="平台" align="center" prop="platformType" width="100">
        <template #default="scope">
          <el-tag>{{ getPlatformName(scope.row.platformType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="price" width="100" />
      <el-table-column label="销量" align="center" prop="sales" width="100" />
      <el-table-column label="评分" align="center" prop="rating" width="100" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)">
            查看
          </el-button>
          <el-button link type="success" @click="handleImport(scope.row)">
            导入
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

  <!-- 商品详情弹窗 -->
  <el-dialog v-model="detailVisible" title="商品详情" width="800px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="商品名称">{{ detailData.name }}</el-descriptions-item>
      <el-descriptions-item label="商品图片">
        <el-image 
          style="width: 100px; height: 100px" 
          :src="detailData.imageUrl" 
          :preview-src-list="[detailData.imageUrl]"
          fit="cover"
        />
      </el-descriptions-item>
      <el-descriptions-item label="平台">{{ getPlatformName(detailData.platformType) }}</el-descriptions-item>
      <el-descriptions-item label="价格">{{ detailData.price }}</el-descriptions-item>
      <el-descriptions-item label="销量">{{ detailData.sales }}</el-descriptions-item>
      <el-descriptions-item label="评分">{{ detailData.rating }}</el-descriptions-item>
      <el-descriptions-item label="商品链接">
        <el-link :href="detailData.url" target="_blank" type="primary">{{ detailData.url }}</el-link>
      </el-descriptions-item>
      <el-descriptions-item label="商品描述">
        <div v-html="detailData.description"></div>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleImport(detailData)">导入到商品库</el-button>
    </template>
  </el-dialog>

  <!-- 导入确认弹窗 -->
  <el-dialog v-model="importVisible" title="导入商品" width="600px">
    <el-form :model="importForm" label-width="100px" :rules="importRules" ref="importFormRef">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="importForm.name" placeholder="请输入商品名称" />
      </el-form-item>
      <el-form-item label="商品分类" prop="categoryId">
        <el-tree-select
          v-model="importForm.categoryId"
          :data="categoryList"
          :props="defaultProps"
          check-strictly
          default-expand-all
          placeholder="请选择分类"
          class="w-full"
        />
      </el-form-item>
      <el-form-item label="采购价格" prop="purchasePrice">
        <el-input-number
          v-model="importForm.purchasePrice"
          :precision="2"
          :step="0.1"
          :min="0"
          class="w-full"
        />
      </el-form-item>
      <el-form-item label="销售价格" prop="salePrice">
        <el-input-number
          v-model="importForm.salePrice"
          :precision="2"
          :step="0.1"
          :min="0"
          class="w-full"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="importVisible = false">取消</el-button>
      <el-button type="primary" @click="submitImport" :loading="importLoading">确认导入</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { defaultProps, handleTree } from '@/utils/tree'

/** 跨境电商ERP - 商品收集 */
defineOptions({ name: 'ProductCollect' })

const message = useMessage() // 消息弹窗

// 定义类型
interface Product {
  id: number
  name: string
  imageUrl: string
  platformType: number
  price: string
  sales: number
  rating: number
  url: string
  description: string
}

// 平台选项
const platformOptions = [
  { label: 'Amazon', value: 1 },
  { label: 'eBay', value: 2 },
  { label: 'Shopee', value: 3 },
  { label: 'Lazada', value: 4 },
  { label: 'Wish', value: 5 },
  { label: 'AliExpress', value: 6 }
]

// 获取平台名称
const getPlatformName = (type) => {
  const platform = platformOptions.find(item => item.value === type)
  return platform ? platform.label : '未知平台'
}

// 列表相关
const loading = ref(false)
const list = ref<Product[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  platformType: undefined as number | undefined,
  keyword: undefined as string | undefined
})
const queryFormRef = ref()

// 采集相关
const collectLoading = ref(false)

// 详情弹窗相关
const detailVisible = ref(false)
const detailData = ref<Partial<Product>>({})

// 导入弹窗相关
const importVisible = ref(false)
const importLoading = ref(false)
const importForm = reactive({
  name: '',
  categoryId: undefined as number | undefined,
  purchasePrice: 0,
  salePrice: 0,
  platformType: undefined as number | undefined,
  platformProductId: '',
  imageUrl: '',
  description: '',
  url: ''
})
const importRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  purchasePrice: [{ required: true, message: '请输入采购价格', trigger: 'blur' }],
  salePrice: [{ required: true, message: '请输入销售价格', trigger: 'blur' }]
}
const importFormRef = ref()
const categoryList = ref([])

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    // 模拟数据，实际项目中应该调用API
    setTimeout(() => {
      list.value = [
        {
          id: 1,
          name: 'Apple iPhone 13 Pro Max 256GB 石墨色 智能手机',
          imageUrl: 'https://m.media-amazon.com/images/I/61i8Vjb17SL._AC_SX679_.jpg',
          platformType: 1,
          price: '8999.00',
          sales: 5000,
          rating: 4.8,
          url: 'https://www.amazon.com/product/123456',
          description: '<p>iPhone 13 Pro Max采用超视网膜XDR显示屏，搭载A15仿生芯片，拥有出色的电池续航能力。</p>'
        },
        {
          id: 2,
          name: 'Samsung Galaxy S21 Ultra 5G 256GB 幻影黑 智能手机',
          imageUrl: 'https://m.media-amazon.com/images/I/61bLefD79-L._AC_SX679_.jpg',
          platformType: 1,
          price: '7999.00',
          sales: 3800,
          rating: 4.7,
          url: 'https://www.amazon.com/product/234567',
          description: '<p>Galaxy S21 Ultra配备108MP主摄像头，支持100倍空间变焦，拥有6.8英寸动态AMOLED 2X显示屏。</p>'
        },
        {
          id: 3,
          name: 'Sony WH-1000XM4 无线降噪耳机 黑色',
          imageUrl: 'https://m.media-amazon.com/images/I/71o8Q5XJS5L._AC_SX679_.jpg',
          platformType: 2,
          price: '1999.00',
          sales: 12000,
          rating: 4.9,
          url: 'https://www.ebay.com/itm/123456',
          description: '<p>Sony WH-1000XM4提供行业领先的降噪性能，30小时电池续航，支持多点连接。</p>'
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
const handleSearch = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleSearch()
}

/** 采集按钮操作 */
const handleCollect = async () => {
  if (!queryParams.keyword) {
    message.warning('请输入关键词')
    return
  }
  
  collectLoading.value = true
  try {
    // 模拟采集操作，实际项目中应该调用API
    setTimeout(() => {
      message.success('商品采集成功')
      getList()
      collectLoading.value = false
    }, 1000)
  } catch (error) {
    console.error(error)
    collectLoading.value = false
  }
}

/** 查看商品详情 */
const handleView = (row) => {
  detailData.value = row
  detailVisible.value = true
}

/** 导入商品 */
const handleImport = (row) => {
  // 填充导入表单
  importForm.name = row.name
  importForm.purchasePrice = parseFloat(row.price) * 0.7 // 采购价默认为售价的70%
  importForm.salePrice = parseFloat(row.price)
  importForm.platformType = row.platformType
  importForm.platformProductId = row.id.toString()
  importForm.imageUrl = row.imageUrl
  importForm.description = row.description
  importForm.url = row.url
  
  // 显示导入弹窗
  importVisible.value = true
  // 关闭详情弹窗
  detailVisible.value = false
}

/** 提交导入 */
const submitImport = async () => {
  // 表单验证
  await importFormRef.value.validate()
  
  importLoading.value = true
  try {
    // 模拟导入操作，实际项目中应该调用API
    setTimeout(() => {
      message.success('商品导入成功')
      importVisible.value = false
      importLoading.value = false
    }, 1000)
  } catch (error) {
    console.error(error)
    importLoading.value = false
  }
}

// 初始化
onMounted(async () => {
  // 获取商品分类列表，实际项目中应该调用API
  categoryList.value = [
    {
      "id": 1,
      "label": '电子产品',
      "children": [
        { id: 11, label: '手机' },
        { id: 12, label: '电脑' },
        { id: 13, label: '配件' }
      ]
    },
    {
      id: 2,
      label: '服装',
      children: [
        { id: 21, label: '男装' },
        { id: 22, label: '女装' },
        { id: 23, label: '童装' }
      ]
    }
  ]
  
  // 获取商品列表
  getList()
})
</script>

<style scoped>
.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.detail-image {
  width: 100%;
  max-height: 300px;
  object-fit: contain;
}

.platform-tag {
  margin-right: 5px;
}

.rating-box {
  display: flex;
  align-items: center;
}

.rating-value {
  margin-left: 5px;
  font-weight: bold;
}
</style>