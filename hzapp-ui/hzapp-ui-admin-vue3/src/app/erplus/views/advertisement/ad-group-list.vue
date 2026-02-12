<template>
  <div class="app-container">
    <el-card class="box-card" shadow="never">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true">
        <el-form-item label="店铺" prop="shopId">
          <el-input
            v-model="queryParams.shopId"
            placeholder="请输入店铺ID"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="广告组名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入广告组名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="活动ID" prop="campaignId">
          <el-input
            v-model="queryParams.campaignId"
            placeholder="请输入活动ID"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="box-card" shadow="never" style="margin-top: 10px;">
      <template #header>
        <div class="clearfix">
          <span>广告组列表</span>
          <div class="tools">
            <el-button type="primary" icon="el-icon-plus" size="small" @click="handleCreate">新增</el-button>
          </div>
        </div>
      </template>
      
      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="adGroupList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="100" />
        <el-table-column label="店铺ID" align="center" prop="shopId" width="120" />
        <el-table-column label="广告组名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="广告活动ID" align="center" prop="campaignId" width="150" />
        <el-table-column label="广告组状态" align="center" prop="state" width="100">
          <template #default="scope">
            <el-tag :type="getStateType(scope.row.state)">
              {{ getStateLabel(scope.row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="默认出价" align="center" prop="defaultBid" width="100">
          <template #default="scope">
            {{ scope.row.defaultBid ? scope.row.defaultBid.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button size="small" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="small" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加/修改对话框 -->
    <el-dialog v-model="dialog.open" :title="dialog.title" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="店铺ID" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入店铺ID" />
        </el-form-item>
        <el-form-item label="广告活动ID" prop="campaignId">
          <el-input v-model="form.campaignId" placeholder="请输入广告活动ID" />
        </el-form-item>
        <el-form-item label="广告组名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入广告组名称" />
        </el-form-item>
        <el-form-item label="广告组状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择广告组状态">
            <el-option label="启用" value="enabled" />
            <el-option label="暂停" value="paused" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认出价" prop="defaultBid">
          <el-input-number v-model="form.defaultBid" :precision="2" :step="0.1" placeholder="请输入默认出价" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAdGroup, getAdGroup, addAdGroup, updateAdGroup, delAdGroup } from '@/app/erplus/api/erplus/advertisement/ad-group'

defineOptions({ name: 'AdGroupList' })

// 响应式数据
const loading = ref(true)
const ids = ref<number[]>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const adGroupList = ref<any[]>([])
const dialog = reactive({
  title: '',
  open: false
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  shopId: null,
  name: null,
  campaignId: null
})

// 表单数据
const form = ref<any>({})
const formRef = ref()
const queryFormRef = ref()

// 表单验证规则
const rules = reactive({
  shopId: [
    { required: true, message: '店铺ID不能为空', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '广告组名称不能为空', trigger: 'blur' }
  ],
  campaignId: [
    { required: true, message: '广告活动ID不能为空', trigger: 'blur' }
  ]
})

// 方法定义
const getList = () => {
  loading.value = true
  listAdGroup(queryParams).then(response => {
    adGroupList.value = response.data.list
    total.value = response.data.total
    loading.value = false
  })
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  // 重置查询表单
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    shopId: null,
    name: null,
    campaignId: null
  })
  handleQuery()
}

const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const handleCreate = () => {
  reset()
  dialog.title = '添加广告组'
  dialog.open = true
}

const handleUpdate = (row: any) => {
  reset()
  const id = row.id || ids.value
  getAdGroup(id).then(response => {
    form.value = response.data
    dialog.open = true
    dialog.title = '修改广告组'
  })
}

const submitForm = () => {
  if (!formRef.value) return
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateAdGroup(form.value).then(response => {
          ElMessage.success('修改成功')
          dialog.open = false
          getList()
        })
      } else {
        addAdGroup(form.value).then(response => {
          ElMessage.success('新增成功')
          dialog.open = false
          getList()
        })
      }
    }
  })
}

const handleDelete = (row: any) => {
  const idsToDelete = row.id || ids.value
  ElMessageBox.confirm('是否确认删除广告组编号为"' + idsToDelete + '"的数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delAdGroup(idsToDelete)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const cancel = () => {
  dialog.open = false
  reset()
}

const reset = () => {
  form.value = {
    id: null,
    shopId: null,
    campaignId: null,
    name: null,
    state: 'enabled',
    defaultBid: 1.00,
    description: null
  }
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const getStateType = (state: string) => {
  if (state === 'enabled') {
    return 'success'
  } else if (state === 'paused') {
    return 'warning'
  } else if (state === 'archived') {
    return 'info'
  }
  return 'info'
}

const getStateLabel = (state: string) => {
  if (state === 'enabled') {
    return '启用'
  } else if (state === 'paused') {
    return '暂停'
  } else if (state === 'archived') {
    return '已归档'
  }
  return '-'
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 10px;
  
  :deep(.el-card__header) {
    padding: 10px 20px;
    border-bottom: 1px solid #ebeef5;
    font-weight: bold;
    
    .tools {
      float: right;
    }
  }
  
  :deep(.el-card__body) {
    padding: 20px;
  }
}

:deep(.el-table th) {
  background-color: #f8f9fa !important;
}
</style>