<!-- 商品发布 - 安全合规 -->
<template>
  <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" :disabled="isDetail">
    <!-- 安全认证 -->
    <el-card class="mb-4" header="安全认证">
      <el-form-item label="产品安全标准">
        <el-checkbox-group v-model="formData.safetyStandards">
          <el-checkbox label="CE">CE认证</el-checkbox>
          <el-checkbox label="FCC">FCC认证</el-checkbox>
          <el-checkbox label="UL">UL认证</el-checkbox>
          <el-checkbox label="RoHS">RoHS认证</el-checkbox>
          <el-checkbox label="REACH">REACH认证</el-checkbox>
          <el-checkbox label="CCC">CCC认证</el-checkbox>
          <el-checkbox label="PSE">PSE认证</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item label="安全警告标识">
        <el-checkbox-group v-model="formData.safetyWarnings">
          <el-checkbox label="CHOKING_HAZARD">窒息危险</el-checkbox>
          <el-checkbox label="SMALL_PARTS">小零件警告</el-checkbox>
          <el-checkbox label="AGE_RESTRICTION">年龄限制</el-checkbox>
          <el-checkbox label="ELECTRICAL_HAZARD">电气危险</el-checkbox>
          <el-checkbox label="CHEMICAL_HAZARD">化学危险</el-checkbox>
          <el-checkbox label="FIRE_HAZARD">火灾危险</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item label="适用年龄" prop="ageRange">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-input-number
              v-model="formData.ageRange.min"
              placeholder="最小年龄"
              :min="0"
              :max="100"
              class="w-full"
            />
            <div class="text-xs text-gray-500 mt-1">最小年龄 (岁)</div>
          </el-col>
          <el-col :span="8">
            <el-input-number
              v-model="formData.ageRange.max"
              placeholder="最大年龄"
              :min="0"
              :max="100"
              class="w-full"
            />
            <div class="text-xs text-gray-500 mt-1">最大年龄 (岁)</div>
          </el-col>
          <el-col :span="8">
            <el-select v-model="formData.ageRange.unit" placeholder="单位" class="w-full">
              <el-option label="岁" value="YEARS" />
              <el-option label="月" value="MONTHS" />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
    </el-card>
    
    <!-- 材料合规 -->
    <el-card class="mb-4" header="材料合规">
      <el-form-item label="主要材料">
        <div v-for="(material, index) in formData.materials" :key="index" class="mb-4 p-4 border border-gray-200 rounded">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input v-model="material.name" placeholder="材料名称" />
            </el-col>
            <el-col :span="6">
              <el-input-number
                v-model="material.percentage"
                placeholder="含量百分比"
                :min="0"
                :max="100"
                :precision="2"
                class="w-full"
              />
            </el-col>
            <el-col :span="6">
              <el-select v-model="material.type" placeholder="材料类型" class="w-full">
                <el-option label="塑料" value="PLASTIC" />
                <el-option label="金属" value="METAL" />
                <el-option label="纺织品" value="TEXTILE" />
                <el-option label="橡胶" value="RUBBER" />
                <el-option label="玻璃" value="GLASS" />
                <el-option label="陶瓷" value="CERAMIC" />
                <el-option label="木材" value="WOOD" />
                <el-option label="其他" value="OTHER" />
              </el-select>
            </el-col>
            <el-col :span="2">
              <el-button @click="removeMaterial(index)" type="danger" size="small" circle>
                <Icon icon="ep:delete" />
              </el-button>
            </el-col>
          </el-row>
        </div>
        <el-button @click="addMaterial" type="primary" plain>
          + 添加材料
        </el-button>
      </el-form-item>
      
      <el-form-item label="有害物质检测">
        <el-checkbox-group v-model="formData.hazardousSubstances">
          <el-checkbox label="LEAD_FREE">无铅</el-checkbox>
          <el-checkbox label="MERCURY_FREE">无汞</el-checkbox>
          <el-checkbox label="CADMIUM_FREE">无镉</el-checkbox>
          <el-checkbox label="PHTHALATE_FREE">无邻苯二甲酸酯</el-checkbox>
          <el-checkbox label="BPA_FREE">无双酚A</el-checkbox>
          <el-checkbox label="FORMALDEHYDE_FREE">无甲醛</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-card>
    
    <!-- 环保合规 -->
    <el-card class="mb-4" header="环保合规">
      <el-form-item label="环保认证">
        <el-checkbox-group v-model="formData.environmentalCertifications">
          <el-checkbox label="ENERGY_STAR">Energy Star</el-checkbox>
          <el-checkbox label="EPEAT">EPEAT认证</el-checkbox>
          <el-checkbox label="GREEN_SEAL">Green Seal</el-checkbox>
          <el-checkbox label="FOREST_STEWARDSHIP">FSC森林认证</el-checkbox>
          <el-checkbox label="RECYCLABLE">可回收</el-checkbox>
          <el-checkbox label="BIODEGRADABLE">可生物降解</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item label="包装材料">
        <el-checkbox-group v-model="formData.packagingMaterials">
          <el-checkbox label="RECYCLABLE_CARDBOARD">可回收纸板</el-checkbox>
          <el-checkbox label="BIODEGRADABLE_PLASTIC">可降解塑料</el-checkbox>
          <el-checkbox label="MINIMAL_PACKAGING">最小化包装</el-checkbox>
          <el-checkbox label="REUSABLE_PACKAGING">可重复使用包装</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item label="碳足迹信息">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input-number
              v-model="formData.carbonFootprint.value"
              placeholder="碳足迹值"
              :min="0"
              :precision="2"
              class="w-full"
            />
            <div class="text-xs text-gray-500 mt-1">碳足迹值</div>
          </el-col>
          <el-col :span="12">
            <el-select v-model="formData.carbonFootprint.unit" placeholder="单位" class="w-full">
              <el-option label="kg CO2e" value="KG_CO2E" />
              <el-option label="g CO2e" value="G_CO2E" />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
    </el-card>
    
    <!-- 法规合规 -->
    <el-card class="mb-4" header="法规合规">
      <el-form-item label="适用法规">
        <el-checkbox-group v-model="formData.applicableRegulations">
          <el-checkbox label="CPSIA">CPSIA (美国消费品安全法)</el-checkbox>
          <el-checkbox label="EN71">EN71 (欧盟玩具安全标准)</el-checkbox>
          <el-checkbox label="ASTM">ASTM标准</el-checkbox>
          <el-checkbox label="ISO">ISO标准</el-checkbox>
          <el-checkbox label="JIS">JIS (日本工业标准)</el-checkbox>
          <el-checkbox label="GB">GB (中国国家标准)</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      
      <el-form-item label="禁售地区">
        <el-select v-model="formData.restrictedRegions" multiple placeholder="选择禁售地区" class="w-80">
          <el-option label="加利福尼亚州" value="CA_US" />
          <el-option label="纽约州" value="NY_US" />
          <el-option label="德国" value="DE" />
          <el-option label="法国" value="FR" />
          <el-option label="英国" value="UK" />
          <el-option label="澳大利亚" value="AU" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="特殊许可证">
        <div v-for="(license, index) in formData.specialLicenses" :key="index" class="mb-4 p-4 border border-gray-200 rounded">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input v-model="license.name" placeholder="许可证名称" />
            </el-col>
            <el-col :span="8">
              <el-input v-model="license.number" placeholder="许可证编号" />
            </el-col>
            <el-col :span="6">
              <el-date-picker
                v-model="license.expiryDate"
                type="date"
                placeholder="有效期"
                class="w-full"
              />
            </el-col>
            <el-col :span="2">
              <el-button @click="removeLicense(index)" type="danger" size="small" circle>
                <Icon icon="ep:delete" />
              </el-button>
            </el-col>
          </el-row>
        </div>
        <el-button @click="addLicense" type="primary" plain>
          + 添加许可证
        </el-button>
      </el-form-item>
    </el-card>
    
    <!-- 质量保证 -->
    <el-card class="mb-4" header="质量保证">
      <el-form-item label="质量检测报告">
        <el-upload
          v-model:file-list="formData.qualityReports"
          action="#"
          :auto-upload="false"
          multiple
          :limit="5"
        >
          <el-button type="primary">上传检测报告</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持 PDF、DOC、DOCX 格式，最多上传5个文件
            </div>
          </template>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="保修信息">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-input-number
              v-model="formData.warranty.period"
              placeholder="保修期"
              :min="0"
              class="w-full"
            />
            <div class="text-xs text-gray-500 mt-1">保修期</div>
          </el-col>
          <el-col :span="8">
            <el-select v-model="formData.warranty.unit" placeholder="单位" class="w-full">
              <el-option label="天" value="DAYS" />
              <el-option label="月" value="MONTHS" />
              <el-option label="年" value="YEARS" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-select v-model="formData.warranty.type" placeholder="保修类型" class="w-full">
              <el-option label="免费维修" value="FREE_REPAIR" />
              <el-option label="免费更换" value="FREE_REPLACEMENT" />
              <el-option label="退款" value="REFUND" />
              <el-option label="有限保修" value="LIMITED_WARRANTY" />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
      
      <el-form-item label="质量承诺">
        <el-input
          v-model="formData.qualityCommitment"
          type="textarea"
          placeholder="请输入质量承诺内容"
          :autosize="{ minRows: 3, maxRows: 6 }"
          class="w-80"
        />
      </el-form-item>
    </el-card>
  </el-form>
</template>

<script lang="ts" setup>
import type { Spu } from '@/api/mall/product/spu'
import { PropType } from 'vue'
import { propTypes } from '@/utils/propTypes'
import { copyValueToTarget } from '@/utils'

defineOptions({ name: 'ProductComplianceForm' })

const message = useMessage() // 消息弹窗

const props = defineProps({
  propFormData: {
    type: Object as PropType<Spu>,
    default: () => {}
  },
  isDetail: propTypes.bool.def(false) // 是否作为详情组件
})

const formRef = ref() // 表单Ref

// 表单数据
const formData = computed(() => {
  return props.propFormData || {
    safetyStandards: [] as string[],
    safetyWarnings: [] as string[],
    ageRange: {
      min: 0,
      max: 0,
      unit: 'YEARS'
    },
    materials: [] as Array<{
      name: string
      percentage: number
      type: string
    }>,
    hazardousSubstances: [] as string[],
    environmentalCertifications: [] as string[],
    packagingMaterials: [] as string[],
    carbonFootprint: {
      value: 0,
      unit: 'KG_CO2E'
    },
    applicableRegulations: [] as string[],
    restrictedRegions: [] as string[],
    specialLicenses: [] as Array<{
      name: string
      number: string
      expiryDate: Date | null
    }>,
    qualityReports: [] as any[],
    warranty: {
      period: 0,
      unit: 'YEARS',
      type: 'FREE_REPAIR'
    },
    qualityCommitment: ''
  }
})

// 表单规则
const rules = reactive({
  // 可以根据需要添加必填项验证
})

// 添加材料
const addMaterial = () => {
  formData.value.materials.push({
    name: '',
    percentage: 0,
    type: ''
  })
}

// 移除材料
const removeMaterial = (index: number) => {
  formData.value.materials.splice(index, 1)
}

// 添加许可证
const addLicense = () => {
  formData.value.specialLicenses.push({
    name: '',
    number: '',
    expiryDate: null
  })
}

// 移除许可证
const removeLicense = (index: number) => {
  formData.value.specialLicenses.splice(index, 1)
}

/** 初始化默认值 */
watch(
  () => props.propFormData,
  (data) => {
    if (!data) {
      return
    }
    // 确保所有必要的属性都存在
    const defaultValues = {
      safetyStandards: [],
      safetyWarnings: [],
      ageRange: { min: 0, max: 0, unit: 'YEARS' },
      materials: [],
      hazardousSubstances: [],
      environmentalCertifications: [],
      packagingMaterials: [],
      carbonFootprint: { value: 0, unit: 'KG_CO2E' },
      applicableRegulations: [],
      restrictedRegions: [],
      specialLicenses: [],
      qualityReports: [],
      warranty: { period: 0, unit: 'YEARS', type: 'FREE_REPAIR' },
      qualityCommitment: ''
    }
    
    Object.keys(defaultValues).forEach(key => {
      if (!data[key]) {
        data[key] = defaultValues[key]
      }
    })
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
    // 校验通过，数据已经直接在propFormData中
  } catch (e) {
    message.error('【安全合规】不完善，请填写相关信息')
    emit('update:activeName', 'compliance')
    throw e // 目的截断之后的校验
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.el-card {
  :deep(.el-card__header) {
    background-color: #f5f7fa;
    font-weight: 600;
  }
}

.el-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  
  .el-checkbox {
    margin-right: 0;
  }
}
</style>