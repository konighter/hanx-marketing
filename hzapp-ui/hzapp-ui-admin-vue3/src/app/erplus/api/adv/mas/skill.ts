import request from '@/config/axios'

export interface MasSkillVO {
  id: number
  skillCode: string
  name: string
  description: string
  icon: string
  useCaseTags: string
  strategyInstruction: string
  requiredTools: string
  paramSchema: string
}

export const getSkillList = () => {
  return request.get({ url: '/adv/mas-skills/list' })
}

export const activateSkill = (data: any) => {
  return request.post({ url: '/adv/mas-skills/activate', data })
}
