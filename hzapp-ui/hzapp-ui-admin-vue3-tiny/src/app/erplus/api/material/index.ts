import request from '@/config/axios'

export interface MaterialVO {
  id?: number
  name: string
  code: string
  barcode?: string
  category?: string
  unit?: number
  length?: number
  width?: number
  height?: number
  weight?: number
  volume?: number
  capacity?: number
  status: number
  remark?: string
  totalCount?: number
}

export interface MaterialStockVO {
  id: number
  materialId: number
  warehouseId: number
  quantity: number
  materialName?: string
  warehouseName?: string
}

export const MaterialApi = {
  // ==================== 耗材基础档案 ====================
  
  getMaterialPage: async (params: any) => {
    return await request.get({ url: '/erplus/material/page', params })
  },
  
  getMaterial: async (id: number) => {
    return await request.get({ url: `/erplus/material/get?id=${id}` })
  },
  
  createMaterial: async (data: MaterialVO) => {
    return await request.post({ url: '/erplus/material/create', data })
  },
  
  generateMaterialCode: async (name: string) => {
    return await request.get({ url: `/erplus/material/generate-code?name=${name}` })
  },
  
  updateMaterial: async (data: MaterialVO) => {
    return await request.put({ url: '/erplus/material/update', data })
  },
  
  deleteMaterial: async (id: number) => {
    return await request.delete({ url: `/erplus/material/delete?id=${id}` })
  },

  // ==================== 耗材库存 ====================
  
  getMaterialStockPage: async (params: any) => {
    return await request.get({ url: '/erplus/material-stock/page', params })
  },
  
  getMaterialStockCount: async (materialId: number, warehouseId: number) => {
    return await request.get({ 
      url: `/erplus/material-stock/get-count?materialId=${materialId}&warehouseId=${warehouseId}` 
    })
  }
}
