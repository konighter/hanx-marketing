import request from '@/config/axios'

/** ERP 库存账单 (V2) Resp VO */
export interface InventoryBillRespVO {
  id: number
  billCode: string
  type: number // 10:入库, 20:出库, 30:调拨, 40:盘点
  fromType?: string // WH, VENDOR, CUST, VIRTUAL
  fromId?: string // 来源ID (仓库ID / 合作伙伴ID)
  toType?: string
  toId?: string
  refType?: string // PO, SO, SHIPMENT, TRANSFER_RECEIPT
  refCode?: string
  operatorId: number
  status: number // 10:完成, 20:待收货, 90:作废
  remark: string
  createTime: number
  items: InventoryBillItemRespVO[]
}

/** ERP 库存账单项 (V2) Resp VO */
export interface InventoryBillItemRespVO {
  id: number
  billId: number
  itemType: number // 1: SKU, 2: 耗材
  itemId: number
  sellerSku?: string
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
  fromId?: string
  toId?: string
  createTime?: [string, string]
  warehouseId?: string
  itemType?: number
  itemId?: number
}

/** ERP 库存账单提交 VO */
export interface InventoryBillSaveReqVO {
  id?: number
  type: number
  fromType?: string
  fromId?: string
  toType?: string
  toId?: string
  refType?: string
  refCode?: string
  remark?: string
  items: {
    itemType: number
    itemId: number
    sellerSku?: string
    qty: number
  }[]
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

  // 创建库存账单 (统一入口：入库/出库/调拨)
  createInventoryBill: async (data: InventoryBillSaveReqVO) => {
    return await request.post({ url: `/erplus/inventory-bill/create`, data })
  },

  // 获得库存账单明细分页
  getInventoryBillItemPage: async (params: any) => {
    return await request.get({ url: `/erplus/inventory-bill/item-page`, params })
  },

  // 确认收货 (仅调拨单)
  receiveInventoryBill: async (id: number) => {
    return await request.put({ url: `/erplus/inventory-bill/receive?id=${id}` })
  }
}
