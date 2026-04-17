<template>
  <ContentWrap>
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="耗材名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入耗材名称"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="耗材编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入内部编码"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="耗材类型" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择类型" clearable class="!w-240px">
          <el-option label="包装材料" value="packaging" />
          <el-option label="填充材料" value="filling" />
          <el-option label="标签贴纸" value="label" />
          <el-option label="配件/零件" value="accessory" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" @click="openForm('create')">
          <Icon icon="ep:plus" class="mr-5px" /> 新增耗材
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="编号" align="center" prop="id" width="80" />
      <el-table-column label="耗材名称" align="center" prop="name" min-width="150" show-overflow-tooltip />
      <el-table-column label="耗材编码" align="center" prop="code" min-width="120" />
      <el-table-column label="条码" align="center" prop="barcode" min-width="120" />
      <el-table-column label="分类" align="center" prop="category" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.category === 'packaging'">包装材料</el-tag>
          <el-tag v-else-if="scope.row.category === 'filling'" type="success">填充材料</el-tag>
          <el-tag v-else-if="scope.row.category === 'label'" type="info">标签贴纸</el-tag>
          <el-tag v-else-if="scope.row.category === 'accessory'" type="warning">配件/零件</el-tag>
          <el-tag v-else-if="scope.row.category === 'other'" type="info">其他</el-tag>
          <el-tag v-else-if="scope.row.category" size="small">{{ scope.row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="单位" align="center" prop="unit">
        <template #default="scope">
          {{ unitMap[scope.row.unit] || scope.row.unit || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="重量(g)" align="center" prop="weight" />
      <el-table-column label="体积(cm³)" align="center" prop="volume" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
            {{ scope.row.status === 0 ? '可用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总库存量" align="center" prop="totalCount" width="100">
        <template #default="scope">
          <span :class="{ 'text-danger': scope.row.totalCount < 10 }">
            {{ scope.row.totalCount || 0 }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)"> 编辑 </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)"> 删除 </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 表单弹窗 -->
    <MaterialForm ref="formRef" @success="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import { MaterialApi, MaterialVO } from '@/app/erplus/api/material'
import { ProductUnitApi } from '@/app/erplus/api/product/unit'
import MaterialForm from './components/MaterialForm.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

defineOptions({ name: 'StockMaterial' })

const loading = ref(true)
const total = ref(0)
const list = ref<MaterialVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  name: '',
  code: '',
  category: '',
  unit: undefined
})
const unitList = ref<any[]>([])
const unitMap = ref<Record<number, string>>({})
const queryFormRef = ref()
const formRef = ref()

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await MaterialApi.getMaterialPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取单位列表 */
const getUnitList = async () => {
  const data = await ProductUnitApi.getProductUnitSimpleList()
  unitList.value = data
  unitMap.value = data.reduce((map: any, unit: any) => {
    map[unit.id] = unit.name
    return map
  }, {})
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 表单操作 */
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除操作 */
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('是否确认删除该耗材档案？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await MaterialApi.deleteMaterial(id)
    ElMessage.success('删除成功')
    getList()
  } catch {}
}

onMounted(() => {
  getList()
  getUnitList()
})
</script>
