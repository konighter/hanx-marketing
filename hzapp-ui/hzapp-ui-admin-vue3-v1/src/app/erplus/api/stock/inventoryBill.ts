import request from '@/config/axios'

/** ERP 库存账单 (V2) Resp VO */
export interface InventoryBillRespVO {
  id: number
  billCode: string
  type: number
  refType: string
  refCode: string
  fromType: string
  fromId: number
  toType: string
  toId: number
  totalCount: number
  remark: string
  createTime: number
  items: InventoryBillItemRespVO[]
}

/** ERP 库存账单项 (V2) Resp VO */
export interface InventoryBillItemRespVO {
  id: number
  billId: number
  sellerSku: string
  qty: number
  snapshotQty: number
}

/** ERP 库存账单分页查询 VO */
export interface InventoryBillPageReqVO {
  pageNo: number
  pageSize: number
  billCode?: string
  type?: number
  refType?: string
  refCode?: string
  fromId?: number
  toId?: number
  createTime?: [string, string]
  warehouseId?: number
  sellerSku?: string
}

// ERP 库存账单 API
export const InventoryBillApi = {
  // 获得库存账单分页
  getInventoryBillPage: async (params: InventoryBillPageReqVO) => {
    return await request.get({ url: `/erplus/inventory-bill/page`, params })
  },

  // 获得库存账单
  getInventoryBill: async (id: number) => {
    return await request.get({ url: `/erplus/inventory-bill/get?id=${id}` })
  },

  // 获得库存账单明细分页
  getInventoryBillItemPage: async (params: InventoryBillPageReqVO) => {
    return await request.get({ url: `/erplus/inventory-bill/item-page`, params })
  }
}
