import request from '@/config/axios'

export interface WorkflowProcessVO {
  id: string
  name: string
  key: string
  version: number
  deploymentId: string
  resourceName: string
  suspensionState: number // 1: active, 2: suspended
  deployTime: string
}

export const WorkflowApi = {
  // 分页查询流程定义
  getPage: async (params: any) => {
    return await request.get({ url: `/erplus/sys/workflow/page`, params })
  },

  // 部署流程 (上传BPMN/XML文件)
  deploy: async (data: any) => {
    return await request.post({ 
      url: `/erplus/sys/workflow/deploy`, 
      data, 
      headers: { 'Content-Type': 'multipart/form-data' } 
    })
  },

  // 暂停流程
  suspend: async (id: string) => {
    return await request.put({ url: `/erplus/sys/workflow/suspend?id=` + id })
  },

  // 恢复流程
  activate: async (id: string) => {
    return await request.put({ url: `/erplus/sys/workflow/activate?id=` + id })
  },

  // 加载流程（比如获取XML内容预览）
  load: async (id: string) => {
    return await request.get({ url: `/erplus/sys/workflow/load?id=` + id })
  },

  // 移除流程
  delete: async (deploymentId: string) => {
    return await request.delete({ url: `/erplus/sys/workflow/delete?deploymentId=` + deploymentId })
  }
}
