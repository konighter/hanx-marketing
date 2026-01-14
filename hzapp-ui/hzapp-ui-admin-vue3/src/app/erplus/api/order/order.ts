import request from '@/config/axios'

export const queryCrossOrderPage = async (data: any) => {
  return await request.post({ url: `/erplus/order/cross-order/page`, data })
}

export const syncCrossOrders = async (data: any) => {
  return await request.post({ url: `/erplus/order/cross-order/sync`, data })
}