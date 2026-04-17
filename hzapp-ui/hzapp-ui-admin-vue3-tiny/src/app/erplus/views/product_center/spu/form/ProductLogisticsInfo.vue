<template>
  <div class="product-logistics-info">
    <div v-if="modelValue.attributes?.customs" class="px-8 pb-10 pt-6">
      <!-- 基本信息 (物流相关) -->
      <div class="section-header mb-5 mt-6">报关基础信息</div>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="中文报关名" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.declarationNameCn" placeholder="申报品名(中)" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="英文报关名" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.declarationNameEn" placeholder="Declaration Name" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="中文材质" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.materialCn" placeholder="主要材质(中)" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="英文材质" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.materialEn" placeholder="Material (EN)" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="中文用途" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.usageCn" placeholder="使用场景/功能" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="英文用途" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.usageEn" placeholder="Usage (EN)" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="品牌类型" label-width="100px">
            <el-select v-model="modelValue.attributes.customs.brandType" class="!w-full">
              <el-option label="自有品牌" value="own" />
              <el-option label="授权品牌" value="authorized" />
              <el-option label="无品牌" value="none" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="出口享惠" label-width="100px">
            <el-select v-model="modelValue.attributes.customs.exportBenefit" class="!w-full">
              <el-option label="不享受" value="no" />
              <el-option label="享受" value="yes" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="内部编码" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.internalCode" placeholder="ERP 代码" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="产品属性" class="mt-2">
        <el-checkbox-group v-model="modelValue.attributes.customs.nature" class="nature-group">
          <el-checkbox label="普货" border size="small" />
          <el-checkbox label="含电" border size="small" />
          <el-checkbox label="纯电" border size="small" />
          <el-checkbox label="液体" border size="small" />
          <el-checkbox label="粉末" border size="small" />
          <el-checkbox label="膏体" border size="small" />
          <el-checkbox label="磁吸" border size="small" />
          <el-checkbox label="纺织品" border size="small" />
        </el-checkbox-group>
      </el-form-item>

      <!-- 报关详尽信息 -->
      <div class="section-header mb-5 mt-10">报关详尽信息</div>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="报关单价" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.declarationPrice" placeholder="单价">
              <template #append>
                <el-select v-model="modelValue.attributes.customs.declarationCurrency" style="width: 70px">
                  <el-option label="USD" value="USD" />
                  <el-option label="CNY" value="CNY" />
                  <el-option label="EUR" value="EUR" />
                </el-select>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="报关 HSCODE" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.declarationHsCode" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="报关型号" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.declarationModel" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="原产地" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.originCountry" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="境内货源地" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.domesticSource" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="报关单位" label-width="100px">
            <el-input v-model="modelValue.attributes.customs.declarationUnit" placeholder="如: 件" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 清关信息 -->
      <div class="section-header mb-5 mt-10">目的国清关信息</div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="清关型号">
            <el-input v-model="modelValue.attributes.customs.clearanceModel" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="配料备注">
            <el-input v-model="modelValue.attributes.customs.ingredientRemark" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="织造方式">
            <el-select v-model="modelValue.attributes.customs.weavingMethod" class="!w-full">
              <el-option label="机织" value="woven" />
              <el-option label="针织" value="knitting" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="清关图片">
             <UploadImg v-model="modelValue.attributes.customs.clearancePicUrl" height="60px" width="60px" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 清关费用 Table -->
      <div class="flex items-center justify-between mt-10 mb-4">
        <div class="section-header">目的国清关费用</div>
        <el-button type="primary" link @click="addClearanceFee">
          <Icon icon="ep:plus" class="mr-1" /> 添加国家
        </el-button>
      </div>
      <el-table 
        v-if="modelValue.attributes.customs.clearanceFees"
        :data="modelValue.attributes.customs.clearanceFees" 
        border 
        size="small" 
        class="custom-table"
      >
        <el-table-column label="国家" width="120">
          <template #default="{ row }">
            <el-input v-model="row.country" placeholder="如: 美国" />
          </template>
        </el-table-column>
        <el-table-column label="默认头程(含税)" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.firstLegFee" :controls="false" class="!w-full" />
          </template>
        </el-table-column>
        <el-table-column label="清关 HS CODE" width="150">
          <template #default="{ row }">
            <el-input v-model="row.hsCode" />
          </template>
        </el-table-column>
        <el-table-column label="清关单价" min-width="150">
           <template #default="{ row }">
            <el-input v-model="row.unitPrice">
               <template #append>
                <el-select v-model="row.currency" style="width: 70px">
                  <el-option label="USD" value="USD" />
                  <el-option label="EUR" value="EUR" />
                </el-select>
              </template>
            </el-input>
          </template>
        </el-table-column>
        <el-table-column label="清关税率 %" width="110">
          <template #default="{ row }">
            <el-input-number v-model="row.taxRate" :controls="false" class="!w-full" />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark">
          <template #default="{ row }">
            <el-input v-model="row.remark" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link @click="removeClearanceFee($index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 税务信息 Table -->
      <div class="flex items-center justify-between mt-10 mb-4">
        <div class="section-header">税务申报信息</div>
        <el-button type="primary" link @click="addTaxInfo">
          <Icon icon="ep:plus" class="mr-1" /> 添加申报主体
        </el-button>
      </div>
      <el-table 
        v-if="modelValue.attributes.customs.taxInfos"
        :data="modelValue.attributes.customs.taxInfos" 
        border 
        size="small" 
        class="custom-table"
      >
        <el-table-column label="公司名称" min-width="150">
           <template #default="{ row }">
            <el-input v-model="row.companyName" />
          </template>
        </el-table-column>
        <el-table-column label="CNPJ" width="120">
          <template #default="{ row }">
            <el-input v-model="row.cnpj" />
          </template>
        </el-table-column>
        <el-table-column label="NCM" width="120">
          <template #default="{ row }">
            <el-input v-model="row.ncm" />
          </template>
        </el-table-column>
        <el-table-column label="单位" width="100">
          <template #default="{ row }">
            <el-input v-model="row.unit" />
          </template>
        </el-table-column>
        <el-table-column label="原产地" width="120">
          <template #default="{ row }">
            <el-input v-model="row.origin" />
          </template>
        </el-table-column>
        <el-table-column label="税种" width="120">
           <template #default="{ row }">
             <el-select v-model="row.taxType">
                <el-option label="默认税种" value="default" />
                <el-option label="免税" value="exempt" />
             </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="{ $index }">
            <el-button type="danger" link @click="removeTaxInfo($index)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">

const modelValue = defineModel<any>({ required: true })

const addClearanceFee = () => {
  modelValue.value.attributes.customs.clearanceFees.push({ 
    country: '', firstLegFee: undefined, hsCode: '', unitPrice: undefined, currency: 'USD', taxRate: undefined, remark: '' 
  })
}

const removeClearanceFee = (index: number) => {
  modelValue.value.attributes.customs.clearanceFees.splice(index, 1)
}

const addTaxInfo = () => {
  modelValue.value.attributes.customs.taxInfos.push({
    companyName: '', cnpj: '', ncm: '', unit: '', origin: '', taxType: ''
  })
}

const removeTaxInfo = (index: number) => {
  modelValue.value.attributes.customs.taxInfos.splice(index, 1)
}
</script>

<style scoped lang="scss">
.product-logistics-info {
  :deep(.el-form-item) {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    
    .el-form-item__label {
      margin-bottom: 0 !important;
      line-height: 32px;
      display: flex;
      align-items: center;
      justify-content: flex-start;
    }
    
    .el-form-item__content {
      flex: 1;
      margin-left: 0 !important;
    }
  }
}

.section-header {
  font-size: 14px;
  font-weight: 700;
  color: #1a202c;
  position: relative;
  padding-left: 10px;
  display: flex;
  align-items: center;
  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 2px;
    bottom: 2px;
    width: 3px;
    background: #3b82f6;
    border-radius: 2px;
  }
}

.nature-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  :deep(.el-checkbox) {
    margin-right: 0;
    height: 32px;
    padding: 0 12px;
    background: white;
    &.is-checked {
      background: #eff6ff;
      border-color: #3b82f6;
    }
  }
}

.custom-table {
  :deep(.el-input__wrapper), :deep(.el-input-number__wrapper) {
    padding: 0 4px !important;
    font-size: 12px;
  }
}
</style>
