<template>
  <!-- 情况一：添加/修改 -->
  <el-table v-if="!isDetail" :data="formData?.skus" border class="tabNumWidth" max-height="500" size="small">
    <el-table-column type="selection" width="45" />
    <el-table-column align="center" label="图片" min-width="65">

      <template #default="{ row }">
        <el-image style="width: 60px; height: 60px" :src="row.picUrl" :preview-src-list="[row.picUrl]" fit="cover"
          :preview-teleported=true />
      </template>

    </el-table-column>
    <el-table-column label="属性" prop="id" width="100" :show-overflow-tooltip="true">
      <template #default="{ row: sku }">
        <el-space spacer="-">
          <span v-for="p, i in sku.properties" :key="i"> {{ p.valueName }}</span>
        </el-space>

      </template>
    </el-table-column>



    <el-table-column v-for="attr in attributeList" :key="attr.attrCode" :label="attr.attrName" :prop="attr.attrCode"
      width="100" :show-overflow-tooltip="true">
      <template #label="{ row: attr }">
        <span>{{ attr.attrName }}</span>
      </template>
      <template #default="{ row: attr }">
        <span>
          {{
            attr.attrCode
          }}
        </span>
      </template>

    </el-table-column>



    <el-table-column align="center" label="商品编码" min-width="168">
      <template #default="{ row }">
        <el-input v-model="row.barCode" class="w-100%" />
      </template>
    </el-table-column>
    <el-table-column align="center" label="销售价(元)" min-width="168">
      <template #default="{ row }">
        <el-input-number v-model="row.price" :min="0" :precision="2" :step="0.1" class="w-100%"
          controls-position="right" />
      </template>
    </el-table-column>
    <el-table-column align="center" label="市场价(元)" min-width="168">
      <template #default="{ row }">
        <el-input-number v-model="row.marketPrice" :min="0" :precision="2" :step="0.1" class="w-100%"
          controls-position="right" />
      </template>
    </el-table-column>
    <el-table-column align="center" label="成本价(元)" min-width="168">
      <template #default="{ row }">
        <el-input-number v-model="row.costPrice" :min="0" :precision="2" :step="0.1" class="w-100%"
          controls-position="right" />
      </template>
    </el-table-column>
    <el-table-column align="center" label="库存" min-width="168">
      <template #default="{ row }">
        <el-input-number v-model="row.stock" :min="0" class="w-100%" controls-position="right" />
      </template>
    </el-table-column>


  </el-table>


</template>
<script lang="ts" setup>
import { PropType, Ref } from 'vue'
import { copyValueToTarget, formatToFraction } from '@/utils'
import { propTypes } from '@/utils/propTypes'
import { UploadImg } from '@/components/UploadFile'
import type { Property, Sku, Spu } from '@/app/erplus/api/product/spu'
import type { CategoryAttributeModel } from '@/app/erplus/api/product/category'
import { createImageViewer } from '@/components/ImageViewer'
import { RuleConfig } from '@/app/erplus/views/product/spu/components/index'

import { ElTable } from 'element-plus'
import { isEmpty } from '@/utils/is'

defineOptions({ name: 'SkuList' })
const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => { }
  },
  attributeList: {
    type: Array as PropType<CategoryAttributeModel[]>,
    default: () => []
  },

  isDetail: propTypes.bool.def(false), // 是否作为 sku 详情组件
})
const formData: Ref<Spu | undefined> = ref<Spu>() // 表单数据




const emit = defineEmits<{
  (e: 'selectionChange', value: Sku[]): void
}>()



/**
 * 将传进来的值赋值给 skuList
 */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) return
    formData.value = data
  },
  {
    deep: true,
    immediate: true
  }
)


</script>
