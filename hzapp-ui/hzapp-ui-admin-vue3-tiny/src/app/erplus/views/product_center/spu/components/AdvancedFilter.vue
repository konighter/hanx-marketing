<template>
  <el-dialog
    v-model="visible"
    title="更多筛选"
    width="800px"
    append-to-body
    class="advanced-filter-dialog"
  >
    <el-form :model="filterData" label-width="100px" label-position="top">
      <el-row :gutter="20">
        <!-- 第一行: 创建人、开发人、负责人 -->
        <el-col :span="8">
          <el-form-item label="创建人">
            <el-select v-model="filterData.creator" placeholder="请选择创建人" clearable class="w-full">
              <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="开发人">
            <el-select v-model="filterData.developer" placeholder="请选择开发人" clearable class="w-full">
              <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="负责人">
            <el-select v-model="filterData.personInCharge" placeholder="请选择负责人" clearable class="w-full">
              <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>

        <!-- 第二行: 采购员、供应商、Listing配对 -->
        <el-col :span="8">
          <el-form-item label="采购员">
            <el-select v-model="filterData.buyer" placeholder="请选择采购员" clearable class="w-full">
              <el-option v-for="item in userOptions" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="供应商">
            <el-select v-model="filterData.vendor" placeholder="请选择供应商" clearable class="w-full">
              <!-- 这里应该对接供应商列表 -->
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="Listing配对">
            <el-select v-model="filterData.listingMatched" placeholder="全部" clearable class="w-full">
              <el-option label="已配对" :value="true" />
              <el-option label="未配对" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>

        <!-- 第三行: 1688配对、关联辅料、产品图片 -->
        <el-col :span="8">
          <el-form-item label="1688配对">
            <el-select v-model="filterData.matched1688" placeholder="全部" clearable class="w-full">
              <el-option label="已配对" :value="true" />
              <el-option label="未配对" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="关联辅料">
            <el-select v-model="filterData.hasAccessories" placeholder="全部" clearable class="w-full">
              <el-option label="有" :value="true" />
              <el-option label="无" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产品图片">
            <el-select v-model="filterData.hasImage" placeholder="全部" clearable class="w-full">
              <el-option label="有" :value="true" />
              <el-option label="无" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>

        <!-- 第四行: 包装规格、单品毛重、采购成本 -->
        <el-col :span="8">
          <el-form-item label="包装规格">
            <el-select v-model="filterData.packageSpec" placeholder="全部" clearable class="w-full">
              <!-- 这里对接包装规格列表 -->
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="单品毛重">
            <div class="flex gap-2">
              <el-select v-model="filterData.weightOp" class="w-20">
                <el-option label=">" value="gt" />
                <el-option label="<" value="lt" />
                <el-option label="=" value="eq" />
              </el-select>
              <el-input-number v-model="filterData.weight" :controls="false" class="flex-1" />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="采购成本">
            <div class="flex gap-2">
              <el-select v-model="filterData.costOp" class="w-20">
                <el-option label=">" value="gt" />
                <el-option label="<" value="lt" />
                <el-option label="=" value="eq" />
              </el-select>
              <el-input-number v-model="filterData.cost" :controls="false" class="flex-1" />
            </div>
          </el-form-item>
        </el-col>

        <!-- 第五行: 默认头程费用、包含单品 -->
        <el-col :span="16">
          <el-form-item label="默认头程费用">
            <div class="flex gap-2">
              <el-select v-model="filterData.shippingRegion" class="w-24">
                <el-option label="US" value="US" />
                <el-option label="UK" value="UK" />
                <el-option label="DE" value="DE" />
              </el-select>
              <el-select v-model="filterData.shippingOp" class="w-20">
                <el-option label=">" value="gt" />
                <el-option label="<" value="lt" />
              </el-select>
              <el-input-number v-model="filterData.shippingCost" :controls="false" class="flex-1" />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="包含单品">
            <el-input v-model="filterData.includeSku" placeholder="请输入单品编码" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="flex justify-end gap-2">
        <el-button @click="reset">重置</el-button>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="confirm">搜索</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import * as UserApi from '@/api/system/user'

const visible = ref(false)
const filterData = reactive({
  creator: undefined,
  developer: undefined,
  personInCharge: undefined,
  buyer: undefined,
  vendor: undefined,
  listingMatched: undefined,
  matched1688: undefined,
  hasAccessories: undefined,
  hasImage: undefined,
  packageSpec: undefined,
  weightOp: 'gt',
  weight: undefined,
  costOp: 'gt',
  cost: undefined,
  shippingRegion: 'US',
  shippingOp: 'gt',
  shippingCost: undefined,
  includeSku: ''
})

const userOptions = ref<any[]>([])

const open = () => {
  visible.value = true
}

const reset = () => {
  Object.keys(filterData).forEach(key => {
    if (key.endsWith('Op')) return
    if (key === 'shippingRegion') return
    filterData[key] = undefined
  })
}

const emit = defineEmits(['confirm'])
const confirm = () => {
  emit('confirm', { ...filterData })
  visible.value = false
}

onMounted(async () => {
  userOptions.value = await UserApi.getSimpleUserList()
})

defineExpose({ open })
</script>

<style scoped lang="scss">
.advanced-filter-dialog {
  :deep(.el-dialog) {
    border-radius: 0 !important;
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  }
  
  :deep(.el-dialog__header) {
    margin-right: 0;
    padding: 12px 20px;
    border-bottom: 1px solid #f1f5f9;
  }

  :deep(.el-form-item__label) {
    font-size: 13px;
    color: #4b5563;
    margin-bottom: 2px;
    font-weight: 600;
  }

  :deep(.el-input__wrapper), 
  :deep(.el-select__wrapper),
  :deep(.el-input-number__wrapper) {
    border-radius: 0 !important;
    box-shadow: 0 0 0 1px #d1d5db inset !important;
  }

  :deep(.el-button) {
    border-radius: 0 !important;
  }
}

.w-full {
  width: 100%;
}
</style>
