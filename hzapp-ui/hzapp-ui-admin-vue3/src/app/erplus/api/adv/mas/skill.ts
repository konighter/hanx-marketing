import request from '@/config/axios'

export interface MasSkillVO {
  id: number
  name: string
  skillCode: string
  description: string
  icon: string
  category: string
}

export const MasSkillApi = {
  // 获取技能大厅列表
  getSkillList: (params?: any) => {
    return request.get({ url: '/erplus/adv/mas/skill/list', params })
  },
  // 获取已激活的技能实例列表
  getSkillInstanceList: (params?: any) => {
    return request.get({ url: '/erplus/adv/mas/skill/instance/list', params })
  },
  // 分页获取已激活的技能实例
  getSkillInstancePage: (params?: any) => {
    return request.get({ url: '/erplus/adv/mas/skill/instance/page', params })
  },
  // 获取技能实例运行日志
  getSkillInstanceLogs: (id: number) => {
    return request.get({ url: '/erplus/adv/mas/skill/instance/logs', params: { id } })
  },
  // 获取技能实例详细消息 (含 AI 思考和对话)
  getSkillInstanceMessages: (processInstanceId: string) => {
    return request.get({ url: '/erplus/adv/mas/skill/instance/messages', params: { processInstanceId } })
  },
  // 激活技能
  activateSkill: (data: any) => {
    return request.post({ url: '/erplus/adv/mas/skill/activate', data })
  }
}

// 兼容旧的导出 (保持命名输出，防止其他组件报错)
export const getSkillList = MasSkillApi.getSkillList
export const getSkillInstanceList = MasSkillApi.getSkillInstanceList
export const getSkillInstancePage = MasSkillApi.getSkillInstancePage
export const getSkillInstanceLogs = MasSkillApi.getSkillInstanceLogs
export const getSkillInstanceMessages = MasSkillApi.getSkillInstanceMessages
export const activateSkill = MasSkillApi.activateSkill
