<template>
  <ContentWrap>
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="68px">
      <el-form-item label="单据号" prop="no">
        <el-input
          v-model="queryParams.no"
          placeholder="请输入装配单号"
          clearable
          @keyup.enter="handleQuery"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="单据状态" clearable class="!w-150px">
          <el-option label="待启动" :value="0" />
          <el-option label="装配中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" @click="openForm">
          <Icon icon="ep:plus" class="mr-5px" /> 新建装配单
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column label="单据编号" align="center" prop="no" min-width="160" />
      <el-table-column label="目标 SKU" align="left" prop="skuName" min-width="200" show-overflow-tooltip>
        <template #default="scope">
          <div class="flex flex-col items-start gap-1">
            <span class="font-bold">{{ scope.row.skuName || '未知商品' }}</span>
            <el-tag v-if="scope.row.skuCode" size="small" type="info">{{ scope.row.skuCode }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="计划数量" align="center" prop="plannedQty" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="160">
        <template #default="scope">
          <div class="flex flex-col items-center gap-2px">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
            
            <el-popover
              v-if="scope.row.hasShortfall"
              placement="bottom"
              title="缺料清单"
              :width="350"
              trigger="hover"
            >
              <template #reference>
                <el-link type="danger" :underline="false" class="text-12px mt-2px">
                  <Icon icon="ep:warning" class="mr-2px" /> 缺少耗材
                </el-link>
              </template>
              
              <el-table :data="getPagedShortfallItems(scope.row)" size="small" border>
                <el-table-column property="materialName" label="耗材名称" min-width="120" show-overflow-tooltip />
                <el-table-column property="shortfallQty" label="缺料量" width="80" align="center">
                  <template #default="item">
                    <span class="text-danger font-bold">{{ item.row.shortfallQty }}</span>
                  </template>
                </el-table-column>
                <el-table-column property="materialUnit" label="单位" width="60" align="center" />
              </el-table>
              
              <div class="mt-10px flex items-center justify-between border-t pt-10px">
                <div class="flex items-center gap-10px">
                  <span class="text-12px text-gray-400">共 {{ scope.row.shortfallItems?.length }} 项</span>
                  <el-button size="small" type="primary" link @click="handleDownloadShortfall(scope.row)">
                    <Icon icon="ep:download" class="mr-2px" /> 下载
                  </el-button>
                </div>
                <Pagination
                  v-if="scope.row.shortfallItems?.length > 10"
                  small
                  layout="prev, pager, next"
                  :pager-count="3"
                  :total="scope.row.shortfallItems.length"
                  :limit="10"
                  v-model:page="shortfallPageStates[scope.row.id]"
                  class="!m-0 !mt-0 !mb-0 !float-none"
                />
              </div>
            </el-popover>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="批次号" align="center" prop="batchNo" width="160">
        <template #default="scope">
          <el-tag v-if="scope.row.batchNo" type="info" size="small">{{ scope.row.batchNo }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" :formatter="dateFormatter" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="openView(scope.row.id)">
            {{ scope.row.status <= 1 ? '处理' : '详情' }}
          </el-button>
          <el-button v-if="scope.row.status === 0 || scope.row.status === 3" link type="danger" @click="handleDelete(scope.row.id)">
            删除
          </el-button>
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
    <AssemblyOrderForm ref="formRef" @success="getList" />
    <!-- 详情抽屉 -->
    <AssemblyOrderView ref="viewRef" @success="getList" />
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import { AssemblyApi, AssemblyOrderVO } from '@/app/erplus/api/stock/assembly'
import AssemblyOrderForm from './components/AssemblyOrderForm.vue'
import AssemblyOrderView from './components/AssemblyOrderView.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'

defineOptions({ name: 'StockAssembly' })

const loading = ref(true)
const total = ref(0)
const list = ref<AssemblyOrderVO[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  no: '',
  status: undefined
})
const queryFormRef = ref()
const formRef = ref()
const viewRef = ref()

// 缺料清单分页状态: { orderId: currentPage }
const shortfallPageStates = reactive<Record<number, number>>({})

/** 截取当前页的缺料项 */
const getPagedShortfallItems = (row: AssemblyOrderVO) => {
  if (!row.shortfallItems) return []
  const page = shortfallPageStates[row.id] || 1
  const pageSize = 10
  const start = (page - 1) * pageSize
  return row.shortfallItems.slice(start, start + pageSize)
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AssemblyApi.getAssemblyOrderPage(queryParams)
    list.value = data.list
    total.value = data.total
    
    // 初始化或保留分页状态
    data.list.forEach(row => {
      if (row.hasShortfall && !shortfallPageStates[row.id]) {
        shortfallPageStates[row.id] = 1
      }
    })
  } finally {
    loading.value = false
  }
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

/** 新建操作 */
const openForm = () => {
  formRef.value.open()
}

/** 处理/详情操作 */
const openView = (id: number) => {
  viewRef.value.open(id)
}

/** 删除操作 */
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('是否确认删除该装配单？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await AssemblyApi.deleteAssemblyOrder(id)
    ElMessage.success('删除成功')
    getList()
  } catch {}
}

/** 下载缺料单 */
const handleDownloadShortfall = (row: AssemblyOrderVO) => {
  if (!row.shortfallItems || row.shortfallItems.length === 0) return
  
  const header = '耗材名称,预期需求,缺料数量,单位\n'
  const rows = row.shortfallItems.map(item => 
    `${item.materialName},${item.expectedQty},${item.shortfallQty},${item.materialUnit || ''}`
  ).join('\n')
  
  const csvContent = '\uFEFF' + header + rows // Add BOM for Excel UTF-8 support
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' })
  download.excel(blob, `缺料清单_${row.no || row.id}.csv`)
}

const getStatusLabel = (status: number) => {
  const labels = ['待启动', '装配中', '已完成', '已取消']
  return labels[status] || '未知'
}

const getStatusType = (status: number) => {
  const types = ['', 'warning', 'success', 'danger']
  return types[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>
