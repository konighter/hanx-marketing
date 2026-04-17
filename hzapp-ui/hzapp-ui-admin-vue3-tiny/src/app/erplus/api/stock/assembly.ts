import request from '@/config/axios'

export interface AssemblyOrderVO {
  id?: number
  no?: string
  skuId: number
  warehouseId: number
  plannedQty: number
  actualQty?: number
  batchNo?: string
  status?: number
  remark?: string
  createTime?: string
}

export interface AssemblyItemVO {
  id: number
  orderId: number
  materialId: number
  expectedQty: number
  shortfallQty: number
  materialName?: string // 方便前端显示
  materialUnit?: string
}

export const AssemblyApi = {
  getAssemblyOrderPage: async (params: any) => {
    return await request.get({ url: '/erplus/assembly-order/page', params })
  },
  
  getAssemblyOrder: async (id: number) => {
    return await request.get({ url: `/erplus/assembly-order/get?id=${id}` })
  },
  
  createAssemblyOrder: async (data: AssemblyOrderVO) => {
    return await request.post({ url: '/erplus/assembly-order/create', data })
  },
  
  updateAssemblyOrder: async (data: AssemblyOrderVO) => {
    return await request.put({ url: '/erplus/assembly-order/update', data })
  },
  
  deleteAssemblyOrder: async (id: number) => {
    return await request.delete({ url: `/erplus/assembly-order/delete?id=${id}` })
  },
  
  startAssemblyOrder: async (id: number) => {
    return await request.put({ url: `/erplus/assembly-order/start?id=${id}` })
  },
  
  completeAssemblyOrder: async (id: number) => {
    return await request.put({ url: `/erplus/assembly-order/complete?id=${id}` })
  },
  
  cancelAssemblyOrder: async (id: number) => {
    return await request.put({ url: `/erplus/assembly-order/cancel?id=${id}` })
  },
  
  getAssemblyOrderItems: async (orderId: number) => {
    return await request.get({ url: `/erplus/assembly-order/get-items?orderId=${orderId}` })
  }
}
