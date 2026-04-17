<!-- 商品发布 - 物流设置 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isDetail">
    <!-- 净品信息 -->
    <el-divider content-position="left">净品规格</el-divider>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="尺寸">
          <div class="flex items-center gap-2">
            <el-input v-model="formData.itemDim!.length" class="flex-1" placeholder="长度" />
            <span class="text-gray-400">×</span>
            <el-input v-model="formData.itemDim!.width" class="flex-1" placeholder="宽度" />
            <span class="text-gray-400">×</span>
            <el-input v-model="formData.itemDim!.height" class="flex-1" placeholder="高度">
              <template #append>
                <el-select v-model="formData.itemDim!.unit" class="w-16" style="width: 80px;">
                  <el-option label="厘米" value="CM" />
                  <el-option label="英寸" value="INCH" />
                  <el-option label="毫米" value="MM" />
                </el-select>
              </template>
            </el-input>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="重量" prop="itemDim.weight">
          <el-input v-model="formData.itemDim!.weight" class="w-full" placeholder="请输入重量">
            <template #append>
              <el-select v-model="formData.itemDim!.weightUnit" class="w-16" style="width: 80px;">
                <el-option label="千克" value="KG" />
                <el-option label="克" value="G" />
                <el-option label="磅" value="LB" />
                <el-option label="盎司" value="OZ" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 包裹信息 -->
    <el-divider content-position="left">包裹规格</el-divider>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="尺寸">
          <div class="flex items-center gap-2">
            <el-input v-model="formData.pkgDim!.length" class="flex-1" placeholder="长度" />
            <span class="text-gray-400">×</span>
            <el-input v-model="formData.pkgDim!.width" class="flex-1" placeholder="宽度" />
            <span class="text-gray-400">×</span>
            <el-input v-model="formData.pkgDim!.height" class="flex-1" placeholder="高度">
              <template #append>
                <el-select v-model="formData.pkgDim!.unit" class="w-16" style="width: 80px;">
                  <el-option label="厘米" value="CM" />
                  <el-option label="英寸" value="INCH" />
                  <el-option label="毫米" value="MM" />
                </el-select>
              </template>
            </el-input>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="重量" prop="pkgDim.weight">
          <el-input v-model="formData.pkgDim!.weight" class="w-full" placeholder="请输入重量">
            <template #append>
              <el-select v-model="formData.pkgDim!.weightUnit" class="w-16" style="width: 80px;">
                <el-option label="千克" value="KG" />
                <el-option label="克" value="G" />
                <el-option label="磅" value="LB" />
                <el-option label="盎司" value="OZ" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 单箱信息 -->
    <el-divider content-position="left">单箱规格</el-divider>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="尺寸">
          <div class="flex items-center gap-2">
            <el-input v-model="formData.boxDim!.length" class="flex-1" placeholder="长度" />
            <span class="text-gray-400">×</span>
            <el-input v-model="formData.boxDim!.width" class="flex-1" placeholder="宽度" />
            <span class="text-gray-400">×</span>
            <el-input v-model="formData.boxDim!.height" class="flex-1" placeholder="高度">
              <template #append>
                <el-select v-model="formData.boxDim!.unit" class="w-16" style="width: 80px;">
                  <el-option label="厘米" value="CM" />
                  <el-option label="英寸" value="INCH" />
                  <el-option label="毫米" value="MM" />
                </el-select>
              </template>
            </el-input>
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="重量" prop="boxDim.weight">
          <el-input v-model="formData.boxDim!.weight" class="w-full" placeholder="请输入重量">
            <template #append>
              <el-select v-model="formData.boxDim!.weightUnit" class="w-16" style="width: 80px;">
                <el-option label="千克" value="KG" />
                <el-option label="克" value="G" />
                <el-option label="磅" value="LB" />
                <el-option label="盎司" value="OZ" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="单箱数量" prop="inboxnum">
          <el-input v-model="formData.inboxnum" class="w-full" placeholder="请输入单箱数量" />
        </el-form-item>
      </el-col>
    </el-row>
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
  itemDim: {
    length: undefined,
    width: undefined,
    height: undefined,
    unit: 'CM',
    weight: undefined,
    weightUnit: 'KG'
  },
  pkgDim: {
    length: undefined,
    width: undefined,
    height: undefined,
    unit: 'CM',
    weight: undefined,
    weightUnit: 'KG'
  },
  boxDim: {
    length: undefined,
    width: undefined,
    height: undefined,
    unit: 'CM',
    weight: undefined,
    weightUnit: 'KG'
  },
  inboxnum: undefined
})

const defaultDim = () => ({
  length: undefined,
  width: undefined,
  height: undefined,
  unit: 'CM',
  weight: undefined,
  weightUnit: 'KG'
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
    // 确保存在
    if (!formData.itemDim) formData.itemDim = defaultDim()
    if (!formData.pkgDim) formData.pkgDim = defaultDim()
    if (!formData.boxDim) formData.boxDim = defaultDim()

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