<template>
  <div class="amz-dynamic-form-container">
    <!-- Section: Search & Filter Toolbar -->
    <!-- Toolbar managed by parent section header -->

    <!-- Main Form Content -->
    <el-form 
      ref="listingFormRef" 
      :model="formData" 
      label-position="right"
      label-width="180px"
      class="amz-form"
      style="max-width: 850px"
    >
      <div 
        v-for="group in filteredGroups" 
        :key="group.name" 
        :id="'group-' + group.name"
        class="group-section"
      >
        <!-- Only show inner group header if there are multiple groups, to avoid redundancy with the section header -->
        <div v-if="filteredGroups.length > 1" class="group-header">
          <span class="header-dot"></span>
          <h3 class="group-title">{{ group.name }}</h3>
        </div>
        
        <div class="group-fields">
          <AttributeRenderer
            v-for="field in getVisibleFieldsInGroup(group)"
            :key="field.id"
            :field="field"
            v-model="formData[field.id]"
            :dynamic-required="requirementMap[field.id]"
          />
        </div>
      </div>
      
      <!-- Empty State removed per request -->
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, provide } from 'vue';
import { getListingFormConfigV2 } from '@/app/erplus/api/product/listing';
import AttributeRenderer from './AttributeRenderer.vue';
import { useAmzLinkages } from './useAmzLinkages';

interface Field {
  id: string;
  title: string;
  groupName: string;
  required?: boolean;
  [key: string]: any;
}

const props = defineProps<{
  productType: string;
  initialData?: any;
  targetGroups?: string[]; // If provided, only render these specific groups
  excludeFields?: string[]; // IDs of fields to ignore (e.g. handled by hardcoded sections)
  requiredOnly?: boolean;
  searchQuery?: string;
  fields?: Field[]; // Externally provided schema fields
}>();

const formData = ref<any>(props.initialData || {});
const allFields = ref<Field[]>([]);

// Linkage Engine Integration
const currentFields = computed(() => props.fields || allFields.value);
const { visibilityMap, requirementMap } = useAmzLinkages(currentFields, formData);

// Provide maps and filter state to all nested renderers (including composite children)
provide('amzLinkageVisibility', visibilityMap);
provide('amzLinkageRequirement', requirementMap);
provide('amzRequiredOnly', computed(() => props.requiredOnly));


const loadSchema = async () => {
  if (!props.productType || props.fields) return; // Skip if fields are provided externally
  
  try {
    const res = await getListingFormConfigV2(props.productType);
    allFields.value = res.fields || [];
  } catch (e) {
    console.error('Failed to load listing schema', e);
  }
};

watch(() => props.productType, () => {
  loadSchema();
}, { immediate: true });

// Filter logic

const filteredGroups = computed(() => {
  const groupsMapped: Record<string, { name: string; fields: Field[] }> = {};
  
  currentFields.value.forEach(f => {
    // 1. Exclude list
    if (props.excludeFields && props.excludeFields.includes(f.id)) return;

    // 2. Group filter
    if (props.targetGroups && !props.targetGroups.includes(f.groupName)) return;
    
    // 3. Search filter
    if (props.searchQuery && !f.title.toLowerCase().includes(props.searchQuery.toLowerCase()) && !f.id.includes(props.searchQuery)) return;

    // 4. Required filter
    const isRequired = f.required || requirementMap.value[f.id] === true;
    if (props.requiredOnly && !isRequired) return;


    if (!groupsMapped[f.groupName]) {
      groupsMapped[f.groupName] = { name: f.groupName, fields: [] };
    }
    groupsMapped[f.groupName].fields.push(f);
  });

  return Object.values(groupsMapped).sort((a, b) => {
    // Keep standard groups first if they exist in the set
    const priority = ['基本信息', '报价', '描述', '更多属性'];
    const idxA = priority.indexOf(a.name);
    const idxB = priority.indexOf(b.name);
    if (idxA !== -1 && idxB !== -1) return idxA - idxB;
    if (idxA !== -1) return -1;
    if (idxB !== -1) return 1;
    return a.name.localeCompare(b.name);
  });
});

const getVisibleFieldsInGroup = (group: any) => group.fields;

// Validation Logic
const listingFormRef = ref();
const validate = async () => {
  if (listingFormRef.value) {
    return await listingFormRef.value.validate();
  }
  return true;
};

defineExpose({ validate });

// onMounted removed as watcher with immediate:true handles initial load
</script>

<style scoped>
.amz-dynamic-form-container {
  background-color: #fff;
  width: 100%;
  display: flex;
  position: relative;
  align-items: flex-start;
}

.form-toolbar {
  position: absolute;
  right: -320px; /* Offset to the empty right space */
  top: 0;
  width: 300px;
  max-width: 300px;
  padding: 12px;
  background-color: #f8f9fb;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 100;
}

.search-input {
  width: 100%;
}

.amz-form {
  padding: 0;
  width: 100%;
}

.group-section {
  margin-bottom: 12px;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  padding-top: 12px;
}

.header-dot {
  width: 6px;
  height: 6px;
  background-color: #409eff;
  border-radius: 50%;
}

.group-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.group-fields {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.empty-state {
  padding: 80px 0;
  border: 1px dashed #ebeef5;
  border-radius: 8px;
}

/* Deep adjustments for Element Plus form alignment */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

/* Ensure inputs take up available space in the right column */
:deep(.el-input), 
:deep(.el-select), 
:deep(.el-cascader),
:deep(.el-textarea) {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}
</style>
