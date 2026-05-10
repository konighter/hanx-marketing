import request from '@/config/axios'

export interface OrderVO {
  id: number
  tenantId: number
  orderNo: string
  packageId: number
  subscribeMonths: number
  price: number
  payStatus: number
  payType: number
  payTime: Date
  createTime: Date
}

export interface OrderPageReqVO extends PageParam {
  tenantId?: number
  orderNo?: string
  payStatus?: number
}

// 创建租户订阅订单
export const createOrder = (data: any) => {
  return request.post({ url: '/system/tenant-subscribe-order/create', data })
}

// 查询租户订阅订单分页
export const getOrderPage = (params: OrderPageReqVO) => {
  return request.get({ url: '/system/tenant-subscribe-order/page', params })
}
