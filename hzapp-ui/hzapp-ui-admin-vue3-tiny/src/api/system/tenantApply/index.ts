import request from '@/config/axios'

export interface TenantApplyVO {
  id: number
  userId: number
  name: string
  contactName: string
  contactMobile: string
  remark: string
  packageId: number
  status: number
  auditTime: Date
  auditUserId: number
  auditRemark: string
  tenantId: number
  createTime: Date
}

export interface TenantApplyPageReqVO extends PageParam {
  name?: string
  contactName?: string
  status?: number
  createTime?: Date[]
}

export interface TenantApplyAuditReqVO {
  id: number
  status: number
  auditRemark?: string
}

// 查询租户申请分页
export const getTenantApplyPage = (params: TenantApplyPageReqVO) => {
  return request.get({ url: '/system/tenant-apply/page', params })
}

// 审批租户申请
export const auditTenantApply = (data: TenantApplyAuditReqVO) => {
  return request.post({ url: '/system/tenant-apply/audit', data })
}

// 获得我的租户申请列表
export const getMyTenantApplyList = () => {
  return request.get({ url: '/system/tenant-apply/list-my' })
}

// 提交租户申请
export const createTenantApply = (data: any) => {
  return request.post({ url: '/system/tenant-apply/create', data })
}
