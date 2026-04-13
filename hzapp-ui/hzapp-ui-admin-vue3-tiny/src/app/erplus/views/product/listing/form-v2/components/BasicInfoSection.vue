<template>
  <div class="basic-info-segment">
    <el-form 
      ref="sectionFormRef"
      :model="modelValue" 
      :rules="rules"
      label-position="right" 
      label-width="180px" 
      hide-required-asterisk 
      style="max-width: 850px"
    >
        <!-- 1. 商品名称 (Title) -->
        <el-form-item prop="title">
          <template #label>
            <div class="custom-label-box">
              <div class="label-cn">
                <span class="required-star">*</span>商品名称
              </div>
              <div class="label-en">Title</div>
            </div>
          </template>
          <div class="input-with-ai">
            <el-input 
              v-model="modelValue.title" 
              placeholder="请输入商品刊登标题" 
              maxlength="200" 
              show-word-limit
            />
            <el-button 
              type="primary" 
              plain 
              :loading="aiLoading.title"
              @click="generateAiTitle"
              class="ai-btn"
            >
              <Icon icon="ep:magic-stick" class="mr-1" />
              AI 优化
            </el-button>
          </div>
        </el-form-item>

        <!-- 2. 品牌名 (Brand) -->
        <el-form-item prop="brand">
          <template #label>
            <div class="custom-label-box">
              <div class="label-cn">
                <span v-if="!modelValue.noBrand" class="required-star">*</span>品牌名
              </div>
              <div class="label-en">Brand</div>
            </div>
          </template>
          <div class="brand-input-wrapper">
            <el-input 
              v-model="modelValue.brand" 
              placeholder="请输入品牌名称" 
              :disabled="modelValue.noBrand"
              class="brand-input"
            />
            <el-checkbox v-model="modelValue.noBrand" label="无品牌" />
          </div>
        </el-form-item>

        <!-- 3. 搜索关键字 (Search Terms) -->
        <el-form-item prop="searchTerms">
          <template #label>
            <div class="custom-label-box">
              <div class="label-cn">
                <span class="required-star">*</span>搜索关键字
              </div>
              <div class="label-en">Keywords</div>
            </div>
          </template>
          <el-input 
            v-model="modelValue.searchTerms" 
            type="textarea" 
            :rows="2" 
            placeholder="请输入搜索关键字，多个以空格分隔" 
          />
        </el-form-item>

        <!-- 4. 五点描述 (Bullet Points) -->
        <el-form-item prop="bulletPoints">
            <template #label>
              <div class="custom-label-box">
                <div class="label-cn">
                  <span class="required-star">*</span>五点描述
                </div>
                <div class="label-en">Bullet Points</div>
                <el-button 
                  size="small" 
                  type="primary" 
                  link 
                  :loading="aiLoading.bullet"
                  @click="generateAiBullet"
                  class="label-ai-btn"
                >
                  <Icon icon="ep:magic-stick" /> AI 优化
                </el-button>
              </div>
            </template>
            <div class="bullet-points-container">
              <div 
                v-for="(point, index) in modelValue.bulletPoints" 
                :key="index" 
                class="bullet-row"
              >
                <el-input 
                  v-model="modelValue.bulletPoints[index]" 
                  placeholder="请输入描述要点"
                >
                  <template #suffix>
                    <span class="bullet-index-inner">#{{ index + 1 }}</span>
                  </template>
                </el-input>
                <div class="bullet-actions">
                  <!-- Delete button (for all rows except the last one) -->
                  <el-button 
                    v-if="index < modelValue.bulletPoints.length - 1"
                    type="danger" 
                    link 
                    @click="removeBulletPoint(index)"
                  >
                    删除
                  </el-button>
                  <!-- Add button (only for the last row) -->
                  <el-button 
                    v-if="index === modelValue.bulletPoints.length - 1"
                    type="primary" 
                    link 
                    @click="addBulletPoint"
                  >
                    添加
                  </el-button>
                </div>
              </div>
            </div>
          </el-form-item>

        <!-- Description with AI (Step 5) -->
        <el-form-item prop="description">
          <template #label>
            <div class="custom-label-box">
              <div class="label-cn">
                <span class="required-star">*</span>商品描述
              </div>
              <div class="label-en">Description</div>
            </div>
          </template>
          <div class="input-with-ai">
            <!-- ... input ... -->
            <el-input 
              v-model="modelValue.description" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入详细的商品描述" 
            />
            <div class="ai-overlay-tools">
                <el-button 
                size="small" 
                type="primary" 
                link 
                :loading="aiLoading.desc"
                @click="generateAiDesc"
              >
                <Icon icon="ep:magic-stick" /> AI 生成
              </el-button>
            </div>
          </div>
        </el-form-item>

        <!-- Seller SKU (Moved to bottom) -->
        <!-- <div class="sub-divider" style="margin: 12px 0"></div> -->

        <el-form-item prop="sellerSku">
          <template #label>
            <div class="custom-label-box">
              <div class="label-cn">
                <span class="required-star">*</span>Seller SKU
              </div>
              <div class="label-en">sku</div>
            </div>
          </template>
          <el-input v-model="modelValue.sellerSku" placeholder="例如: B08-SKU123" />
        </el-form-item>
      </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';

const modelValue = defineModel<{
  title: string;
  description: string;
  sellerSku: string;
  brand: string;
  noBrand: boolean;
  searchTerms: string;
  bulletPoints: string[];
}>({ required: true });

const aiLoading = reactive({
  title: false,
  desc: false,
  bullet: false
});

const rules = {
  title: [{ required: true, message: '商品名称不能为空', trigger: 'blur' }],
  brand: [{ 
    validator: (rule: any, value: any, callback: any) => {
      if (!modelValue.value.noBrand && !value) {
        callback(new Error('品牌名不能为空'));
      } else {
        callback();
      }
    }, 
    trigger: ['blur', 'change'] 
  }],
  searchTerms: [{ required: true, message: '搜索关键字不能为空', trigger: 'blur' }],
  description: [{ required: true, message: '商品描述不能为空', trigger: 'blur' }],
  sellerSku: [{ required: true, message: 'Seller SKU 不能为空', trigger: 'blur' }],
  bulletPoints: [{ 
    validator: (rule: any, value: any, callback: any) => {
      if (!value || value.length === 0 || value.every((v: string) => !v)) {
        callback(new Error('请至少填写一条五点描述'));
      } else {
        callback();
      }
    }, 
    trigger: ['blur', 'change'] 
  }]
};

const sectionFormRef = ref();
const validate = () => sectionFormRef.value?.validate();
defineExpose({ validate });

const addBulletPoint = () => {
  modelValue.value.bulletPoints.push('');
};

const removeBulletPoint = (index: number) => {
  if (modelValue.value.bulletPoints.length > 1) {
    modelValue.value.bulletPoints.splice(index, 1);
  }
};

const generateAiTitle = () => {
  aiLoading.title = true;
  setTimeout(() => {
    aiLoading.title = false;
    ElMessage.success('AI 标题优化完成');
  }, 1000);
};

const generateAiBullet = () => {
  aiLoading.bullet = true;
  setTimeout(() => {
    aiLoading.bullet = false;
    ElMessage.success('AI 五点描述优化完成');
    modelValue.value.bulletPoints = [
      'High performance material with durable build.',
      'Easy to install and maintain for daily use.',
      'Modern design fits any workspace aesthetic.',
      'Eco-friendly packaging and production.',
      '12-month satisfaction guarantee included.'
    ];
  }, 1200);
};

const generateAiDesc = () => {
  aiLoading.desc = true;
  setTimeout(() => {
    aiLoading.desc = false;
    ElMessage.success('AI 描述已生成');
  }, 1000);
};
</script>

<style scoped lang="scss">
.basic-info-segment {
  padding: 0;
  width: 100%;
}

.input-with-ai {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  position: relative;
  width: 100%;
}

.ai-btn {
  flex-shrink: 0;
}

.brand-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;

  .brand-input {
    flex: 1;
  }
}

.bullet-points-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;

  .bullet-row {
    display: flex;
    align-items: center;
    gap: 12px;

    .bullet-index-inner {
      font-size: 11px;
      font-weight: 400;
      color: #909399;
      line-height: 32px;
      padding-right: 8px;
      opacity: 0.6;
    }

    .bullet-actions {
      display: flex;
      align-items: center;
      gap: 4px;
      min-width: 50px;
      
      .el-button {
        font-size: 13px;
        padding: 0;
        height: auto;
      }
    }
  }
}

.custom-label-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  line-height: 1.2;
  justify-content: flex-start; /* Align to top */
  height: 100%;
  width: 100%;
  padding-right: 4px;
  padding-top: 6px; /* Fine-tune to match input top */
  
  .label-cn {
    font-size: 12px;
    color: #303133;
    font-weight: 500;
    position: relative;
    
    .required-star {
      color: #f56c6c;
      font-size: 12px;
      margin-right: 2px;
    }
  }
  
  .label-en {
    font-size: 10px;
    color: #909399;
    font-weight: 400;
  }

  .label-ai-btn {
    margin-right: -4px;
    margin-top: 2px;
  }
}

.label-with-tools {
  // Deprecated, using .custom-label-box
}

.ai-overlay-tools {
  position: absolute;
  right: 12px;
  bottom: 8px;
  z-index: 10;
  background: rgba(255,255,255,0.8);
  padding: 2px 8px;
  border-radius: 4px;
}

:deep(.el-input), 
:deep(.el-textarea),
:deep(.el-form-item) {
  width: 100%;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

.sub-divider {
  border-top: 1px solid #ebeef5;
}
</style>
