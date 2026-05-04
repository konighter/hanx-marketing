<template>
  <div class="ad-manager">
    <div class="flex justify-between items-center mb-10px w-full">
      <div class="flex items-center">
        <span class="text-14px font-bold mr-10px">广告列表:</span>
      </div>
      <el-button 
        size="small" 
        @click="addAd"
        class="add-btn"
      >
        <el-icon><Plus /></el-icon>
        <span class="ml-4px">添加商品广告</span>
      </el-button>
    </div>
 
    <el-dialog v-model="createDialogVisible" title="添加商品广告" width="800px" append-to-body>
      <div class="flex flex-col gap-12px">
        <div class="flex gap-10px">
          <el-input
            v-model="searchText"
            placeholder="输入 ASIN / SKU / 关键词搜索产品"
            class="flex-1"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch" :loading="searching">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>

        <el-table 
          v-loading="searching"
          :data="searchResults" 
          border 
          size="small" 
          height="400px"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="40" align="center" />
          <el-table-column label="图片" width="70" align="center">
            <template #default="{ row }">
              <el-image 
                v-if="row.mainImage?.url"
                :src="row.mainImage.url" 
                class="w-40px h-40px rounded-4px"
                fit="cover"
                preview-teleported
              />
              <div v-else class="w-40px h-40px bg-gray-100 rounded-4px flex items-center justify-center text-gray-400">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="商品信息" min-width="200">
            <template #default="{ row }">
              <div class="flex flex-col">
                <div class="text-12px font-medium line-clamp-2 mb-4px" :title="row.title">{{ row.title }}</div>
                <div class="text-11px text-gray-500">
                  <span class="mr-8px">ASIN: {{ row.platformProductCode }}</span>
                  <span v-if="row.sellerProductCode">SKU: {{ row.sellerProductCode }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="价格" width="100" align="right">
            <template #default="{ row }">
              <span v-if="row.price && row.price.length > 0">
                {{ (row.price[0].salePrice / 100).toFixed(2) }} {{ row.price[0].currency }}
              </span>
              <span v-else class="text-gray-300">--</span>
            </template>
          </el-table-column>
          <el-table-column label="库存" width="80" align="center">
            <template #default="{ row }">
              <span v-if="row.inventory">{{ row.inventory.fulfillableQuantity }}</span>
              <span v-else class="text-gray-300">--</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="flex justify-between items-center text-12px text-gray-500">
          <span>已选择 {{ selectedRows.length }} 个商品</span>
          <el-pagination
            v-if="total > 0"
            v-model:current-page="pageNo"
            v-model:page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            small
            @current-change="handleSearch"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="createDialogVisible = false" size="small">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdd" size="small" :disabled="selectedRows.length === 0">添加到列表</el-button>
      </template>
    </el-dialog>

    <el-table :data="ads" border size="small" style="width: 100%">
      <el-table-column label="图片" width="70" align="center">
        <template #default="{ row }">
          <el-image 
            v-if="row.image"
            :src="row.image" 
            class="w-40px h-40px rounded-4px"
            :preview-src-list="[row.image]"
            preview-teleported
          />
          <div v-else class="w-40px h-40px bg-gray-100 rounded-4px flex items-center justify-center text-gray-400">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" min-width="240">
        <template #default="{ row }">
          <div class="flex flex-col py-4px">
            <div class="text-13px font-medium mb-4px" v-if="row.name">{{ row.name }}</div>
            <div class="text-12px text-gray-500">
              <span v-if="row.asin" class="mr-8px">ASIN: {{ row.asin }}</span>
              <span v-if="row.sku">SKU: {{ row.sku }}</span>
              <span v-if="!row.asin && !row.sku" class="text-gray-300">-</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default>
          <el-tag type="info" size="mini">NEW</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ $index }">
          <el-button 
            type="danger" 
            link 
            size="small" 
            @click="removeAd($index)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, unref } from 'vue'
import { Picture, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { queryCrossProductListingPage } from '@/app/erplus/api/product/listing'

const ads = defineModel<any[]>({ default: () => [] })
const shopId = inject<any>('shopId')

const createDialogVisible = ref(false)
const searchText = ref('')
const searching = ref(false)
const searchResults = ref<any[]>([])
const selectedRows = ref<any[]>([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const addAd = () => {
  searchText.value = ''
  searchResults.value = []
  selectedRows.value = []
  pageNo.value = 1
  createDialogVisible.value = true
}

const handleSearch = async () => {
  const currentShopId = unref(shopId)
  if (!currentShopId) {
    ElMessage.warning('未能获取店铺 ID')
    return
  }

  searching.value = true
  try {
    const res = await queryCrossProductListingPage({
      shopId: currentShopId,
      keyword: searchText.value.trim() || undefined,
      pageNo: pageNo.value,
      pageSize: pageSize.value
    })
    
    searchResults.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
    ElMessage.error('搜索产品失败')
  } finally {
    searching.value = false
  }
}

const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
}

const handleConfirmAdd = () => {
  if (selectedRows.value.length === 0) return

  const newAds = selectedRows.value.map(item => ({
    asin: item.platformProductCode,
    sku: item.sellerProductCode,
    name: item.title,
    image: item.mainImage?.url,
    status: 'ENABLED',
    attributes: {
      asin: item.platformProductCode,
      sku: item.sellerProductCode
    }
  }))

  // 过滤掉已经存在的 (按 ASIN 去重)
  const existingAsins = new Set(ads.value.map(a => a.asin))
  const filteredNewAds = newAds.filter(a => a.asin && !existingAsins.has(a.asin))
  
  if (filteredNewAds.length < newAds.length) {
    ElMessage.info(`已自动过滤 ${newAds.length - filteredNewAds.length} 个已添加的商品`)
  }

  ads.value = [...(ads.value || []), ...filteredNewAds]
  createDialogVisible.value = false
}

const removeAd = (index: number) => {
  ads.value = ads.value.filter((_, i) => i !== index)
}
</script>


<style scoped>
.ad-manager :deep(.el-table .cell) {
  padding: 4px 8px;
}

.add-btn {
  border: 1px solid var(--el-border-color);
  background: #fff;
  color: var(--el-text-color-regular);
  height: 24px;
  padding: 0 8px;
  font-size: 12px;
}
.add-btn:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
</style>
