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
  },

  // 手动触发流程
  startProcess: async (processKey: string, variables: any) => {
    return await request.post({ 
      url: `/erplus/sys/workflow/start?processKey=` + processKey,
      data: variables
    })
  },

  // 查询运行中的实例
  listInstances: async (params: any) => {
    return await request.get({ url: `/erplus/sys/workflow/instances`, params })
  },

  // 获取实例变量
  getInstanceVariables: async (processInstanceId: string) => {
    return await request.get({ url: `/erplus/sys/workflow/instance/variables?processInstanceId=` + processInstanceId })
  },

  // 终止实例
  deleteInstance: async (processInstanceId: string, reason?: string) => {
    return await request.delete({ 
      url: `/erplus/sys/workflow/instance?processInstanceId=${processInstanceId}&reason=${reason || ''}` 
    })
  },

  // 查询定时任务
  listTimers: async (processInstanceId: string) => {
    return await request.get({ url: `/erplus/sys/workflow/instance/timers?processInstanceId=` + processInstanceId })
  },

  // 触发定时任务
  triggerTimer: async (processInstanceId: string, jobId?: string) => {
    return await request.post({ 
      url: `/erplus/sys/workflow/instance/trigger-timer?processInstanceId=${processInstanceId}${jobId ? '&jobId=' + jobId : ''}` 
    })
  }
}
