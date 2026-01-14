<template>
  <div class="box-packing">
    <!-- Step 1 & 2: Packing Options Selection -->
    <div v-if="!packingOptionConfirmed">
      <div class="mb-20px flex justify-between items-center">
        <h3>装箱方案确认</h3>
        <el-button type="primary" :loading="loading" @click="getPackingOptions">生成/刷新方案</el-button>
      </div>

      <el-skeleton :loading="loading" animated>
        <template #template>
          <el-skeleton-item variant="p" style="width: 100%" />
          <el-skeleton-item variant="p" style="width: 80%" />
        </template>
        <div v-for="option in packingOptions" :key="option.packingOptionId"
          class="option-card mb-15px p-15px border rounded hover:bg-gray-50 transition-colors">
          <div class="flex justify-between items-center">
            <div>
              <span class="font-bold">方案 ID: {{ option.packingOptionId }}</span>
              <!-- <el-tag type="info" class="ml-10px">箱子总数: {{ option.boxCount }}</el-tag> -->
            </div>
            <el-button type="success" size="small" @click="confirmPacking(option)">确认此方案</el-button>
          </div>
          <div class="mt-10px text-gray-500 text-sm">
            预期费用: <span class="text-orange-500 font-bold">{{ option.fees || '暂无数据' }}</span>
          </div>
        </div>
      </el-skeleton>

      <div v-if="packingOptions.length === 0 && !loading"
        class="text-center p-40px text-gray-400 border-dashed border-2 rounded">
        暂无方案，请点击“生成/刷新方案”
      </div>

      <div class="flex justify-center mt-30px">
        <el-button @click="$emit('back')">上一步</el-button>
      </div>
    </div>

    <!-- Step 3: Perfect Packing Details -->
    <div v-else>
      <div class="mb-20px">
        <div class="flex justify-between items-center mb-10px">
          <h3>完善装箱明细</h3>
          <el-button link type="primary" @click="packingOptionConfirmed = false">返回重新选择方案</el-button>
        </div>
        <el-alert title="请根据选定的方案填写箱子重量与尺寸" type="info" :closable="false" show-icon />
      </div>

      <el-form label-width="120px" class="mt-20px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="包装方式">
              <el-select v-model="formData.packType" placeholder="请选择包装方式" class="w-full">
                <el-option label="散件" value="loose" />
                <el-option label="整箱" value="case" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="箱子数量">
              <el-input-number v-model="formData.quantity" :min="1" class="!w-70" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细信息">
          <!--  这里保留原有的装箱明细填写逻辑，但可能需要根据选定的 OptionId 调整 -->

          <!-- {{ Object.keys(selectedPackingOption.groupItems) }} -->

          <template v-for="key in Object.keys(selectedPackingOption.groupItems)" :key="key">
            <el-table :data="selectedPackingOption.groupItems[key]" border class="w-full">
              <el-table-column label="ASIN" width="120" prop="asin" />
              <el-table-column label="SKU" width="180" prop="msku" />
              <el-table-column label="已装箱/总数量" width="120" align="center">

                <template #default="{ row }">
                  {{ row.quantityInBox * row.boxQuantity }}/{{ row.quantity }}
                </template>

              </el-table-column>
              <el-table-column label="单箱件数" width="150">

                <template #default="{ row }">
                  <el-form-item class="mb-0!">
                    <el-input-number v-model="row.quantityInBox" :min="1" :max="row.quantity" class="w-100%" @change="row.boxQuantity = row.quantityInBox <= 0 ? 0 : Math.round(row.quantity / row.quantityInBox) + (row.quantity %
                      row.quantityInBox > 0 ? 1 : 0)" />
                  </el-form-item>
                </template>

              </el-table-column>

              <el-table-column label="箱子数量" width="150">
                <template #default="{ row }">
                  {{ row.boxQuantity }}
                </template>
              </el-table-column>

              <el-table-column label="单箱重量(kg)" width="150">
                <template #default="{ row }">
                  <el-form-item class="mb-0!">
                    <el-input-number v-model="row.boxWeight" :min="0.1" :step="0.1" class="w-100%" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="长(cm)" width="100">
                <template #default="{ row }">
                  <el-form-item class="mb-0!">
                    <el-input v-model="row.boxLength" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="宽(cm)" width="100">
                <template #default="{ row }">
                  <el-form-item class="mb-0!">
                    <el-input v-model="row.boxWidth" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="高(cm)" width="100">
                <template #default="{ row }">
                  <el-form-item class="mb-0!">
                    <el-input v-model="row.boxHeight" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </template>





        </el-form-item>
      </el-form>

      <div class="flex justify-center gap-4 mt-30px">
        <el-button @click="packingOptionConfirmed = false">返回</el-button>
        <el-button type="primary" :loading="loading" @click="submitPacking">提交装箱明细并入库</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { AmzInboundApi } from '@/app/erplus/api/stock/amzInbound'
import { ElMessage } from 'element-plus'

const props = defineProps({
  planId: {
    type: String,
    required: true
  },
  active: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['back', 'next'])

const loading = ref(false)
const packingOptionConfirmed = ref(false)
const packingOptions = ref<any[]>([])
const selectedOptionId = ref('')
const selectedPackingOption = ref({})

const formData = ref({
  packType: 'loose',
  quantity: 1
})

const boxInfo = ref({
  quantity: 1,
  weight: 10.0,
  length: '50',
  width: '40',
  height: '30'
})

const getPackingOptions = async () => {
  loading.value = true
  try {
    // 实际流程可能是调用 generatePackingOptions 然后轮询或直接 listPackingOptions
    // 这里先尝试获取，如果为空则可能需要调用生成
    let res = await AmzInboundApi.listPackingOptions(props.planId)
    console.log('listPackingOptions', res)
    if (!res || res.length === 0) {
      packingOptions.value = await AmzInboundApi.generatePackingOptions({ planId: props.planId })
      return
    }
    packingOptions.value = res
  } catch (error) {
    ElMessage.error('获取装箱方案失败')
  } finally {
    loading.value = false
  }
}

const confirmPacking = async (option: any) => {
  loading.value = true
  try {
    selectedPackingOption.value = option
    await AmzInboundApi.confirmPackingOption({
      planId: props.planId,
      packingOptionId: option.packingOptionId
    })
    selectedOptionId.value = option.packingOptionId
    packingOptionConfirmed.value = true
    ElMessage.success('方案已确认')
  } catch (error) {
    ElMessage.error('确认装箱方案失败')
  } finally {
    loading.value = false
  }
}

const submitPacking = async () => {
  loading.value = true
  try {
    console.log('submitPacking: ', selectedPackingOption.value)
    await AmzInboundApi.setPackingInformation({
      planId: props.planId,
      option: selectedPackingOption.value
    })
    ElMessage.success('装箱明细提交成功')
    emit('next')
  } catch (error) {
    console.error('Failed to set packing info', error)
    ElMessage.error('提交失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (props.active) {
    getPackingOptions()
  }
})

watch(() => props.active, (val) => {
  if (val && packingOptions.value.length === 0) {
    getPackingOptions()
  }
})
</script>

<style scoped>
.option-card {
  cursor: pointer;
}
</style>