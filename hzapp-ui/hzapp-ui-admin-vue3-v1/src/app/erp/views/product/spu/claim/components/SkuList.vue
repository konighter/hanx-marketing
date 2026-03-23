<template>
  <!-- 情况一：添加/修改 -->

  <!-- 情况二：详情 -->

  <!-- 情况三：作为活动组件 -->
  <el-table
    :data="formData!.skus!"
    border
    max-height="500"
    size="small"
    style="width: 99%"
  >
    <el-table-column v-if="isComponent" type="selection" width="45" />
    <el-table-column align="center" label="图片" min-width="50">
      <template #default="{ row }">
        <el-image :src="row.picUrl" class="h-30px w-30px" @click="imagePreview(row.picUrl)" />
      </template>
    </el-table-column>
    <template v-if="formData!.specType">
      <!--  根据商品属性动态添加 -->
      <el-table-column
        v-for="(item, index) in tableHeaders"
        :key="index"
        :label="item.label"
        align="center"
        min-width="80"
      >
        <template #default="{ row }">
          <span style="font-weight: bold; color: #40aaff">
            {{ row.properties[index]?.valueName }}
          </span>
        </template>
      </el-table-column>
    </template>
    <el-table-column align="center" label="商品条码" min-width="100">
      <template #default="{ row }">
        {{ row.barCode }}
      </template>
    </el-table-column>
    <el-table-column align="center" label="销售价(元)" min-width="80">
      <template #default="{ row }">
        {{ row.price }}
      </template>
    </el-table-column>
    <el-table-column align="center" label="市场价(元)" min-width="80">
      <template #default="{ row }">
        {{ row.marketPrice }}
      </template>
    </el-table-column>
    <el-table-column align="center" label="成本价(元)" min-width="80">
      <template #default="{ row }">
        {{ row.costPrice }}
      </template>
    </el-table-column>
    <el-table-column align="center" label="库存" min-width="80">
      <template #default="{ row }">
        {{ row.stock }}
      </template>
    </el-table-column>
    <!--  方便扩展每个活动配置的属性不一样  -->
    <slot name="extension"></slot>
  </el-table>
</template>
<script lang="ts" setup>
import { PropType, Ref } from 'vue'
import type {  Sku, Spu } from '@/app/erp/api/spu'
import { createImageViewer } from '@/components/ImageViewer'
import { ElTable } from 'element-plus'

defineOptions({ name: 'SkuList' })
const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => {}
  }
})
const formData: Ref<Spu | undefined> = ref<Spu>() // 表单数据
const skuList = ref<Sku[]>([
  {
    price: 0, // 商品价格
    marketPrice: 0, // 市场价
    costPrice: 0, // 成本价
    barCode: '', // 商品条码
    picUrl: '', // 图片地址
    stock: 0, // 库存
    weight: 0, // 商品重量
    volume: 0, // 商品体积
    firstBrokeragePrice: 0, // 一级分销的佣金
    secondBrokeragePrice: 0 // 二级分销的佣金
  }
]) // 批量添加时的临时数据

/** 商品图预览 */
const imagePreview = (imgUrl: string) => {
  createImageViewer({
    zIndex: 9999999,
    urlList: [imgUrl]
  })
}


const tableHeaders = ref<{ prop: string; label: string; id: number }[]>([]) // 多属性表头

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

    const sku = props.propFormData.skus[0]
    tableHeaders.value = sku.properties.map(p => {
         return {
           id: p.propertyId,
           label: p.propertyName,
         }
    })

  },
  {
    deep: true,
    immediate: true
  }
)




const activitySkuListRef = ref<InstanceType<typeof ElTable>>()

const getSkuTableRef = () => {
  return activitySkuListRef.value
}
// 暴露出生成 sku 方法，给添加属性成功时调用
defineExpose({ getSkuTableRef })
</script>
