import request from '@/config/axios'

export interface TenantInviteVO {
  inviteeMobile: string
  expireTime: string
}

export interface TenantInviteRespVO {
  id: number
  tenantId: number
  tenantName: string
  inviteeMobile: string
  inviteCode: string
  status: number
  expireTime: string
  createTime: string
}

// 创建租户邀请
export const createTenantInvite = (data: TenantInviteVO) => {
  return request.post({ url: '/system/tenant-invite/create', data })
}

// 根据邀请码获取邀请详情
export const getTenantInviteByCode = (inviteCode: string) => {
  return request.get({ url: '/system/tenant-invite/get-by-code?inviteCode=' + inviteCode })
}

// 接受租户邀请
export const acceptTenantInvite = (inviteCode: string) => {
  return request.post({ url: '/system/tenant-invite/accept?inviteCode=' + inviteCode })
}
