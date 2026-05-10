import request from '@/config/axios'

export interface SubscribeVO {
  id: number
  tenantId: number
  packageId: number
  status: number
  startTime: Date
  endTime: Date
  createTime: Date
}

export interface SubscribePageReqVO extends PageParam {
  tenantId?: number
  packageId?: number
  status?: number
}

// 查询租户订阅列表
export const getSubscribePage = (params: SubscribePageReqVO) => {
  return request.get({ url: '/system/tenant-subscribe/page', params })
}
