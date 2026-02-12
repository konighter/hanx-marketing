<template>
  <div class="app-container">
    <el-card class="box-card" shadow="never">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
        <el-form-item label="店铺" prop="shopId">
          <el-input
            v-model="queryParams.shopId"
            placeholder="请输入店铺ID"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="活动名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入活动名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="活动状态" prop="state">
          <el-select v-model="queryParams.state" placeholder="请选择活动状态" clearable>
            <el-option label="启用" value="enabled" />
            <el-option label="暂停" value="paused" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="box-card" shadow="never" style="margin-top: 10px;">
      <div slot="header" class="clearfix">
        <span>广告活动列表</span>
        <div class="tools">
          <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleCreate">新增</el-button>
          <el-button type="success" icon="el-icon-refresh" size="mini" @click="handleSync">同步</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="campaignList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="100" />
        <el-table-column label="店铺ID" align="center" prop="shopId" width="120" />
        <el-table-column label="活动名称" align="center" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="活动状态" align="center" prop="state" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStateType(scope.row.state)">
              {{ getStateLabel(scope.row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="广告类型" align="center" prop="campaignType" width="120" />
        <el-table-column label="每日预算" align="center" prop="dailyBudget" width="100">
          <template slot-scope="scope">
            {{ scope.row.dailyBudget ? scope.row.dailyBudget.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="出价策略" align="center" prop="biddingStrategy" width="120" />
        <el-table-column label="同步状态" align="center" prop="syncStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="getSyncStatusType(scope.row.syncStatus)">
              {{ getSyncStatusLabel(scope.row.syncStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
              <span class="el-dropdown-link">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="syncToAmazon" icon="el-icon-upload2">同步到亚马逊</el-dropdown-item>
                <el-dropdown-item command="viewReport" icon="el-icon-document">查看报告</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加/修改对话框 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="店铺ID" prop="shopId">
          <el-input v-model="form.shopId" placeholder="请输入店铺ID" />
        </el-form-item>
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择活动状态">
            <el-option label="启用" value="enabled" />
            <el-option label="暂停" value="paused" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="广告类型" prop="campaignType">
          <el-select v-model="form.campaignType" placeholder="请选择广告类型">
            <el-option label="Sponsored Products" value="sponsoredProducts" />
            <el-option label="Sponsored Brands" value="sponsoredBrands" />
            <el-option label="Sponsored Display" value="sponsoredDisplay" />
          </el-select>
        </el-form-item>
        <el-form-item label="每日预算" prop="dailyBudget">
          <el-input-number v-model="form.dailyBudget" :precision="2" :step="0.1" placeholder="请输入每日预算" />
        </el-form-item>
        <el-form-item label="出价策略" prop="biddingStrategy">
          <el-select v-model="form.biddingStrategy" placeholder="请选择出价策略">
            <el-option label="手动出价" value="manual" />
            <el-option label="自动出价" value="autoForSales" />
            <el-option label="旧版自动出价" value="legacyForSales" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标市场" prop="targetingType">
          <el-select v-model="form.targetingType" placeholder="请选择目标市场">
            <el-option label="自动" value="auto" />
            <el-option label="手动" value="manual" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCampaign, getCampaign, addCampaign, updateCampaign, delCampaign, syncCampaignToAmazon } from '@/app/erplus/api/advertisement/campaign'

export default {
  name: 'CampaignList',
  data() {
    return {
      // 遫星数据
      loading: true,
      // 选中的数据
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 广告活动列表
      campaignList: [],
      // 弹窗标题
      dialog: {
        title: '',
        open: false
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        shopId: null,
        name: null,
        state: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        shopId: [
          { required: true, message: '店铺ID不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '活动名称不能为空', trigger: 'blur' }
        ],
        state: [
          { required: true, message: '活动状态不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询广告活动列表 */
    getList() {
      this.loading = true
      listCampaign(this.queryParams).then(response => {
        this.campaignList = response.data.list
        this.total = response.data.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 选择事件 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleCreate() {
      this.reset()
      this.dialog.title = '添加广告活动'
      this.dialog.open = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getCampaign(id).then(response => {
        this.form = response.data
        this.dialog.open = true
        this.dialog.title = '修改广告活动'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCampaign(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.dialog.open = false
              this.getList()
            })
          } else {
            addCampaign(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.dialog.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除广告活动编号为"' + ids + '"的数据项？').then(function() {
        return delCampaign(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 取消按钮 */
    cancel() {
      this.dialog.open = false
      this.reset()
    },
    /** 重置表单 */
    reset() {
      this.form = {
        id: null,
        shopId: null,
        name: null,
        state: 'enabled',
        campaignType: 'sponsoredProducts',
        dailyBudget: 10.00,
        biddingStrategy: 'manual',
        targetingType: 'auto',
        description: null
      }
      this.resetForm('form')
    },
    /** 获取状态类型 */
    getStateType(state) {
      if (state === 'enabled') {
        return 'success'
      } else if (state === 'paused') {
        return 'warning'
      } else if (state === 'archived') {
        return 'info'
      }
      return 'info'
    },
    /** 获取状态标签 */
    getStateLabel(state) {
      if (state === 'enabled') {
        return '启用'
      } else if (state === 'paused') {
        return '暂停'
      } else if (state === 'archived') {
        return '已归档'
      }
      return '-'
    },
    /** 获取同步状态类型 */
    getSyncStatusType(syncStatus) {
      if (syncStatus === 0) {
        return 'info'
      } else if (syncStatus === 1) {
        return 'warning'
      } else if (syncStatus === 2) {
        return 'success'
      } else if (syncStatus === 3) {
        return 'danger'
      }
      return 'info'
    },
    /** 获取同步状态标签 */
    getSyncStatusLabel(syncStatus) {
      if (syncStatus === 0) {
        return '未同步'
      } else if (syncStatus === 1) {
        return '同步中'
      } else if (syncStatus === 2) {
        return '已同步'
      } else if (syncStatus === 3) {
        return '同步失败'
      }
      return '-'
    },
    /** 同步按钮操作 */
    handleSync() {
      this.$modal.msgSuccess('开始同步广告活动...')
      // 这里可以添加实际的同步逻辑
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case 'syncToAmazon':
          this.syncToAmazon(row)
          break
        case 'viewReport':
          this.viewReport(row)
          break
        default:
          break
      }
    },
    /** 同步到亚马逊 */
    async syncToAmazon(row) {
      try {
        await syncCampaignToAmazon({ shopId: row.shopId, id: row.id })
        this.$modal.msgSuccess('同步到亚马逊成功')
        this.getList()
      } catch (error) {
        this.$modal.msgError('同步到亚马逊失败：' + error.message)
      }
    },
    /** 查看报告 */
    viewReport(row) {
      this.$router.push('/erplus/amzadv/report/' + row.id)
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.box-card {
  margin-bottom: 10px;
  
  ::v-deep .el-card__header {
    padding: 10px 20px;
    border-bottom: 1px solid #ebeef5;
    font-weight: bold;
    
    .tools {
      float: right;
    }
  }
  
  ::v-deep .el-card__body {
    padding: 20px;
  }
}

::v-deep .el-table th {
  background-color: #f8f9fa !important;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
</style>