<!-- 商品发布 - 物流设置 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isDetail">
    <el-divider content-position="left">包裹信息</el-divider>


    <el-form-item label="单箱规格">
      <el-space :size="56">
        <div class="el-input-group-thir">
          <el-input v-model="formData!.boxDim!.length" placeholder="长" />
          <el-input v-model="formData!.boxDim!.width" placeholder="宽" />
          <el-input v-model="formData!.boxDim!.height" placeholder="高">
            <template #append>cm</template>
          </el-input>
        </div>
        <el-input v-model="formData!.boxDim!.weight" placeholder="重">
          <template #append>kg</template>
        </el-input>
      </el-space>
    </el-form-item>
    <el-form-item label="单箱数量">
      <el-input class="w-160!" v-model="formData.inboxnum" />
    </el-form-item>
    <el-divider content-position="left">物流信息</el-divider>
    <el-form-item label="附加费">
      <el-input class="w-160!" v-model="formData.additionalFee">

        <template #append>元</template>
      </el-input>
    </el-form-item>

    <el-form-item label="物流报关">
      <el-table>
        <el-table-column min-width="40">
          <template #header>
            <el-button :icon="Plus" text size="small" />
          </template>
        </el-table-column>
        <el-table-column label="国家" />
        <el-table-column label="海关编码" />
        <el-table-column label="中文名" />
        <el-table-column label="英文名" />
        <el-table-column label="报价" />
        <el-table-column label="材质(英文)" />
        <el-table-column label="材质(中文)" />
        <el-table-column label="用途" />
        <el-table-column label="税率" />
        <el-table-column>
          <template #header>
            <span> 操作 </span> <span> <el-button type="primary" link>清除</el-button> </span>
          </template>



        </el-table-column>



      </el-table>



    </el-form-item>




  </el-form>
</template>
<script lang="ts" setup>
import { PropType } from 'vue'
import { copyValueToTarget } from '@/utils'
import { propTypes } from '@/utils/propTypes'
import type { Spu, Sku } from '@/app/erplus/api/product/spu'

import { Plus } from '@element-plus/icons-vue'


defineOptions({ name: 'ProductDeliveryForm' })

const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => { }
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})
const formRef = ref() // 表单 Ref
const formData = reactive<Spu>({
  specType: false,
  pkgDim: {},
  itemDim: {},
  boxDim: {},
  inboxnum: undefined
})




const SkuDimList = ref<Sku[]>([])


const rules = reactive({
  deliveryTypes: [required],
  deliveryTemplateId: [required]
})

/** 将传进来的值赋值给 formData */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) {
      return
    }
    copyValueToTarget(formData, data)
    console.log(formData)

  },
  {
    immediate: true
  }
)

/** 表单校验 */
const emit = defineEmits(['update:activeName'])
const validate = async () => {
  if (!formRef) return
  try {
    await unref(formRef)?.validate()
    // 校验通过更新数据
    Object.assign(props.propFormData, formData)
  } catch (e) {
    message.error('【物流设置】不完善，请填写相关信息')
    emit('update:activeName', 'delivery')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })


onMounted(async () => {

})
</script>