<!-- 跨境电商ERP - 商品管理 -->
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
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-tree-select
          v-model="queryParams.categoryId"
          :data="categoryList"
          :props="defaultProps"
          check-strictly
          default-expand-all
          placeholder="请选择分类"
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
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button
          type="primary"
          plain
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button
          type="success"
          plain
          @click="handleExport"
          :loading="exportLoading"
        >
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
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
      <el-table-column label="分类" align="center" prop="categoryName" width="120" />
      <el-table-column label="采购价" align="center" prop="purchasePrice" width="100">
        <template #default="scope">
          {{ scope.row.purchasePrice ? '¥' + scope.row.purchasePrice : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="销售价" align="center" prop="salePrice" width="100">
        <template #default="scope">
          {{ scope.row.salePrice ? '¥' + scope.row.salePrice : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="库存" align="center" prop="stock" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已上架' : '未上架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="150" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)">
            编辑
          </el-button>
          <el-button link type="primary" @click="handleVariants(scope.row)">
            变体
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)">
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
  <el-dialog :title="formTitle" v-model="formVisible" width="700px">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入商品名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="SKU" prop="sku">
            <el-input v-model="formData.sku" placeholder="请输入SKU" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="分类" prop="categoryId">
            <el-tree-select
              v-model="formData.categoryId"
              :data="categoryList"
              :props="defaultProps"
              check-strictly
              default-expand-all
              placeholder="请选择分类"
              class="w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :label="1">已上架</el-radio>
              <el-radio :label="0">未上架</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="采购价" prop="purchasePrice">
            <el-input-number
              v-model="formData.purchasePrice"
              :precision="2"
              :step="0.1"
              :min="0"
              class="w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="销售价" prop="salePrice">
            <el-input-number
              v-model="formData.salePrice"
              :precision="2"
              :step="0.1"
              :min="0"
              class="w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="重量(kg)" prop="weight">
            <el-input-number
              v-model="formData.weight"
              :precision="2"
              :step="0.1"
              :min="0"
              class="w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库存" prop="stock">
            <el-input-number
              v-model="formData.stock"
              :precision="0"
              :step="1"
              :min="0"
              class="w-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="商品图片" prop="imageUrl">
            <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
            >
              <img v-if="formData.imageUrl" :src="formData.imageUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="商品描述" prop="description">
            <el-input type="textarea" v-model="formData.description" rows="4" placeholder="请输入商品描述" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="formVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <!-- 变体管理弹窗 -->
  <el-dialog title="变体管理" v-model="variantVisible" width="900px">
    <div class="mb-4 flex justify-between">
      <div>
        <span class="font-bold">商品：</span>{{ currentProduct.name }}
      </div>
      <el-button type="primary" size="small" @click="handleAddVariant">
        <Icon icon="ep:plus" class="mr-5px" /> 添加变体
      </el-button>
    </div>
    
    <el-table :data="variantList" border>
      <el-table-column label="变体图片" align="center" width="100">
        <template #default="scope">
          <el-image 
            style="width: 60px; height: 60px" 
            :src="scope.row.imageUrl || 'https://via.placeholder.com/60'"
            :preview-src-list="scope.row.imageUrl ? [scope.row.imageUrl] : []"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="SKU" prop="sku" width="120" />
      <el-table-column label="规格" prop="specs">
        <template #default="scope">
          <el-tag v-for="(value, key) in scope.row.specs" :key="key" class="mb-1 mr-1">
            {{ key }}: {{ value }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="价格" prop="price" width="100">
        <template #default="scope">
          {{ scope.row.price ? '¥' + scope.row.price : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="库存" prop="stock" width="80" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="handleEditVariant(scope.row)">
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDeleteVariant(scope.$index)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <template #footer>
      <el-button @click="saveVariants" type="primary">保存变体</el-button>
      <el-button @click="variantVisible = false">取 消</el-button>
    </template>
  </el-dialog>

  <!-- 变体编辑弹窗 -->
  <el-dialog :title="variantFormTitle" v-model="variantFormVisible" width="500px">
    <el-form ref="variantFormRef" :model="variantForm" label-width="100px">
      <el-form-item label="SKU" prop="sku">
        <el-input v-model="variantForm.sku" placeholder="请输入SKU" />
      </el-form-item>
      <el-form-item label="规格" prop="specs">
        <div v-for="(spec, index) in specList" :key="index" class="mb-2">
          <el-input v-model="spec.key" placeholder="规格名" style="width: 40%" class="mr-2" />
          <el-input v-model="spec.value" placeholder="规格值" style="width: 40%" />
          <el-button type="danger" icon="Delete" circle @click="removeSpec(index)" class="ml-2" />
        </div>
        <el-button type="primary" plain @click="addSpec">
          <Icon icon="ep:plus" class="mr-5px" /> 添加规格
        </el-button>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="variantForm.price" :precision="2" :step="0.1" :min="0" class="w-full" />
      </el-form-item>
      <el-form-item label="库存" prop="stock">
        <el-input-number v-model="variantForm.stock" :precision="0" :step="1" :min="0" class="w-full" />
      </el-form-item>
      <el-form-item label="变体图片" prop="imageUrl">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeVariantImageUpload"
          :http-request="handleVariantImageUpload"
        >
          <img v-if="variantForm.imageUrl" :src="variantForm.imageUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitVariantForm" type="primary">确 定</el-button>
      <el-button @click="variantFormVisible = false">取 消</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { defaultProps, handleTree } from '@/utils/tree'
import type { UploadRequestHandler } from 'element-plus'

/** 跨境电商ERP - 商品管理 */
defineOptions({ name: 'ProductManage' })

const message = useMessage() // 消息弹窗

// 定义类型
interface ProductItem {
  id: number
  name: string
  sku: string
  categoryId: number
  categoryName: string
  purchasePrice: number
  salePrice: number
  weight: number
  stock: number
  imageUrl: string
  status: number
  createTime: string
  description: string
}

interface ProductVariant {
  sku: string
  specs: Record<string, string>
  price: number
  stock: number
  imageUrl: string
}

interface SpecItem {
  key: string
  value: string
}

interface CategoryItem {
  id: number
  label: string
  children?: CategoryItem[]
}

// 列表相关
const loading = ref(false)
const list = ref<ProductItem[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined as string | undefined,
  categoryId: undefined as number | undefined,
  sku: undefined as string | undefined
})
const queryFormRef = ref()
const exportLoading = ref(false)

// 分类列表
const categoryList = ref<CategoryItem[]>([])

// 表单相关
const formVisible = ref(false)
const formTitle = ref('')
const formLoading = ref(false)
const formType = ref('')
const formData = reactive({
  id: undefined as number | undefined,
  name: '',
  sku: '',
  categoryId: undefined as number | undefined,
  status: 1,
  purchasePrice: 0,
  salePrice: 0,
  weight: 0,
  stock: 0,
  imageUrl: '',
  description: ''
})
const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  sku: [{ required: true, message: '请输入SKU', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}
const formRef = ref()

// 变体管理相关
const variantVisible = ref(false)
const currentProduct = ref<Partial<ProductItem>>({})
const variantList = ref<ProductVariant[]>([])

// 变体表单相关
const variantFormVisible = ref(false)
const variantFormTitle = ref('添加变体')
const variantForm = reactive({
  index: -1,
  sku: '',
  specs: {} as Record<string, string>,
  price: 0,
  stock: 0,
  imageUrl: ''
})
const variantFormRef = ref()
const specList = ref<SpecItem[]>([])

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
          categoryId: 11,
          categoryName: '手机',
          purchasePrice: 7999.00,
          salePrice: 8999.00,
          weight: 0.24,
          stock: 100,
          imageUrl: 'https://m.media-amazon.com/images/I/61i8Vjb17SL._AC_SX679_.jpg',
          status: 1,
          createTime: '2023-05-15 10:30:00',
          description: 'iPhone 13 Pro Max采用超视网膜XDR显示屏，搭载A15仿生芯片，拥有出色的电池续航能力。'
        },
        {
          id: 2,
          name: 'Samsung Galaxy S21 Ultra 5G',
          sku: 'SGS21U-256-BK',
          categoryId: 11,
          categoryName: '手机',
          purchasePrice: 6999.00,
          salePrice: 7999.00,
          weight: 0.22,
          stock: 85,
          imageUrl: 'https://m.media-amazon.com/images/I/61bLefD79-L._AC_SX679_.jpg',
          status: 1,
          createTime: '2023-05-16 14:20:00',
          description: 'Galaxy S21 Ultra配备108MP主摄像头，支持100倍空间变焦，拥有6.8英寸动态AMOLED 2X显示屏。'
        },
        {
          id: 3,
          name: 'Sony WH-1000XM4 无线降噪耳机',
          sku: 'SWXM4-BK',
          categoryId: 13,
          categoryName: '配件',
          purchasePrice: 1599.00,
          salePrice: 1999.00,
          weight: 0.25,
          stock: 200,
          imageUrl: 'https://m.media-amazon.com/images/I/71o8Q5XJS5L._AC_SX679_.jpg',
          status: 1,
          createTime: '2023-05-17 09:15:00',
          description: 'Sony WH-1000XM4提供行业领先的降噪性能，30小时电池续航，支持多点连接。'
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

/** 导出按钮操作 */
const handleExport = () => {
  exportLoading.value = true
  try {
    // 模拟导出操作，实际项目中应该调用API
    setTimeout(() => {
      message.success('导出成功')
      exportLoading.value = false
    }, 1000)
  } catch (error) {
    console.error(error)
    exportLoading.value = false
  }
}

/** 打开表单弹窗 */
const openForm = (type, id?) => {
  formVisible.value = true
  formType.value = type
  formTitle.value = type === 'create' ? '新增商品' : '编辑商品'
  resetForm()
  
  if (type === 'update' && id) {
    formLoading.value = true
    // 模拟获取详情，实际项目中应该调用API
    const product = list.value.find(item => item.id === id)
    if (product) {
      Object.assign(formData, product)
    }
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.id = undefined
  formData.name = ''
  formData.sku = ''
  formData.categoryId = undefined
  formData.status = 1
  formData.purchasePrice = 0
  formData.salePrice = 0
  formData.weight = 0
  formData.stock = 0
  formData.imageUrl = ''
  formData.description = ''
}

/** 提交表单 */
const submitForm = async () => {
  // 表单验证
  await formRef.value.validate()
  
  formLoading.value = true
  try {
    // 模拟提交操作，实际项目中应该调用API
    setTimeout(() => {
      message.success(formType.value === 'create' ? '新增成功' : '修改成功')
      formVisible.value = false
      formLoading.value = false
      getList()
    }, 1000)
  } catch (error) {
    console.error(error)
    formLoading.value = false
  }
}

/** 删除按钮操作 */
const handleDelete = async (id) => {
  try {
    await message.confirm('确认要删除该商品吗？')
    // 模拟删除操作，实际项目中应该调用API
    setTimeout(() => {
      message.success('删除成功')
      getList()
    }, 500)
  } catch (error) {
    console.error(error)
  }
}

/** 图片上传前的校验 */
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    message.error('上传头像图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    message.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

/** 处理图片上传 */
const handleAvatarUpload = (options) => {
  // 模拟上传，实际项目中应该调用上传API
  const reader = new FileReader()
  reader.readAsDataURL(options.file)
  reader.onload = () => {
    formData.imageUrl = reader.result as string
  }
}

/** 处理变体管理 */
const handleVariants = (row) => {
  currentProduct.value = row
  // 模拟获取变体列表，实际项目中应该调用API
  if (row.id === 1) {
    variantList.value = [
      {
        sku: 'IP13PM-256-GR',
        specs: { '颜色': '石墨色', '存储容量': '256GB' },
        price: 8999.00,
        stock: 50,
        imageUrl: 'https://m.media-amazon.com/images/I/61i8Vjb17SL._AC_SX679_.jpg'
      },
      {
        sku: 'IP13PM-256-SL',
        specs: { '颜色': '银色', '存储容量': '256GB' },
        price: 8999.00,
        stock: 30,
        imageUrl: 'https://m.media-amazon.com/images/I/61jLiCovxVL._AC_SX679_.jpg'
      },
      {
        sku: 'IP13PM-512-GR',
        specs: { '颜色': '石墨色', '存储容量': '512GB' },
        price: 9999.00,
        stock: 20,
        imageUrl: 'https://m.media-amazon.com/images/I/61i8Vjb17SL._AC_SX679_.jpg'
      }
    ]
  } else {
    variantList.value = []
  }
  variantVisible.value = true
}

/** 添加变体 */
const handleAddVariant = () => {
  variantFormTitle.value = '添加变体'
  variantForm.index = -1
  variantForm.sku = ''
  variantForm.specs = {}
  variantForm.price = currentProduct.value.salePrice || 0
  variantForm.stock = 0
  variantForm.imageUrl = ''
  specList.value = []
  variantFormVisible.value = true
}

/** 编辑变体 */
const handleEditVariant = (row) => {
  variantFormTitle.value = '编辑变体'
  const index = variantList.value.findIndex(item => item.sku === row.sku)
  variantForm.index = index
  variantForm.sku = row.sku
  variantForm.price = row.price
  variantForm.stock = row.stock
  variantForm.imageUrl = row.imageUrl
  
  // 转换规格对象为数组
  specList.value = Object.entries(row.specs).map(([key, value]) => ({ key, value }))
  
  variantFormVisible.value = true
}

/** 删除变体 */
const handleDeleteVariant = async (index) => {
  try {
    await message.confirm('确认要删除该变体吗？')
    variantList.value.splice(index, 1)
    message.success('删除成功')
  } catch (error) {
    console.error(error)
  }
}

/** 添加规格 */
const addSpec = () => {
  specList.value.push({ key: '', value: '' })
}

/** 移除规格 */
const removeSpec = (index) => {
  specList.value.splice(index, 1)
}

/** 提交变体表单 */
const submitVariantForm = () => {
  // 将规格数组转换为对象
  const specs = {}
  specList.value.forEach(spec => {
    if (spec.key && spec.value) {
      specs[spec.key] = spec.value
    }
  })
  variantForm.specs = specs
  
  if (variantForm.index === -1) {
    // 新增变体
    variantList.value.push({
      sku: variantForm.sku,
      specs: variantForm.specs,
      price: variantForm.price,
      stock: variantForm.stock,
      imageUrl: variantForm.imageUrl
    })
  } else {
    // 更新变体
    Object.assign(variantList.value[variantForm.index], {
      sku: variantForm.sku,
      specs: variantForm.specs,
      price: variantForm.price,
      stock: variantForm.stock,
      imageUrl: variantForm.imageUrl
    })
  }
  
  variantFormVisible.value = false
  message.success('保存成功')
}

/** 保存所有变体 */
const saveVariants = () => {
  // 模拟保存操作，实际项目中应该调用API
  setTimeout(() => {
    message.success('变体保存成功')
    variantVisible.value = false
  }, 500)
}

/** 变体图片上传前的校验 */
const beforeVariantImageUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    message.error('上传图片只能是 JPG 或 PNG 格式!')
  }
  if (!isLt2M) {
    message.error('上传图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

/** 处理变体图片上传 */
const handleVariantImageUpload = (options) => {
  // 模拟上传，实际项目中应该调用上传API
  const reader = new FileReader()
  reader.readAsDataURL(options.file)
  reader.onload = () => {
    variantForm.imageUrl = reader.result as string
  }
}

// 初始化
onMounted(async () => {
  // 获取商品分类列表，实际项目中应该调用API
  categoryList.value = [
    {
      id: 1,
      label: '电子产品',
      children: [
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

.form-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.upload-box {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100px;
  height: 100px;
  cursor: pointer;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
}

.upload-box:hover {
  border-color: #409eff;
}
</style>