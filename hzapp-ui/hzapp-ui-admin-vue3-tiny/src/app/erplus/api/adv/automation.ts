import request from '@/config/axios'

export interface AutomationTemplate {
  id?: number
  name: string
  type: string
  config: any
  status: number
}

export interface AutomationPlan {
  id?: number
  templateId: number
  shopId: number
  platform: string
  sku?: string
  context: any
  status: string
}

export const AutomationApi = {
  // 模版 API
  getTemplateList: async (params: any) => {
    return await request.get({ url: '/erplus/adv/automation/template/page', params })
  },
  getTemplate: async (id: number) => {
    return await request.get({ url: '/erplus/adv/automation/template/get?id=' + id })
  },
  
  // 计划 API
  createPlan: async (data: AutomationPlan) => {
    return await request.post({ url: '/erplus/adv/automation/plan/create', data })
  },
  getPlanPage: async (params: any) => {
    return await request.get({ url: '/erplus/adv/automation/plan/page', params })
  },
  updatePlanStatus: async (id: number, status: string) => {
    return await request.put({ url: '/erplus/adv/automation/plan/update-status?id=' + id + '&status=' + status })
  },
  initStructure: async (id: number) => {
    return await request.post({ url: '/erplus/adv/automation/plan/init-structure?id=' + id })
  },
  executeCycle: async (id: number) => {
    return await request.post({ url: '/erplus/adv/automation/plan/execute-cycle?id=' + id })
  },
  getLogPage: async (params: any) => {
    return await request.get({ url: '/erplus/adv/automation/log/page', params })
  }
}
