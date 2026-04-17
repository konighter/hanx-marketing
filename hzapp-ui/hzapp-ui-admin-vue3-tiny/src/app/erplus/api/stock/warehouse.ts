import request from '@/config/axios'
import type { Dayjs } from 'dayjs'
import { get } from 'http'

/** ERP 仓库信息 */
export interface Warehouse {
  id: number // 仓库编号
  name?: string // 仓库名称
  type?: number // 类型: 平台仓/海外仓/家庭仓/活动仓
  shopId: number // 店铺
  platformId: number // 平台ID
  marketId: string // 市场
  remark: string // 备注
  principal: string // 负责人
  status?: number // 开启状态
  defaultStatus: boolean // 是否默认
}

// 平台仓/海外仓/家庭仓/活动仓
export const WarehouseTypes = [
  { value: 0, label: '平台仓', exclusive: true },
  { value: 1, label: '海外仓', exclusive: false },
  { value: 2, label: '家庭仓', exclusive: false },
  { value: 3, label: '活动仓', exclusive: true },
  { value: 4, label: '本地仓', exclusive: false }
]

export const WarehouseTypeMap = new Map<number, string>(
  WarehouseTypes.map((type) => [type.value, type.label])
)

export const inboundSrcTypes = [
  { value: 0, label: '采购入库' },
  { value: 1, label: '调拨入库' },
  { value: 2, label: '其他入库' }
]

// ERP 仓库 API
export const WarehouseApi = {
  // 查询ERP 仓库分页
  getWarehousePage: async (params: any) => {
    return await request.get({ url: `/erplus/warehouse/page`, params })
  },

  // 查询ERP 仓库列表
  getWarehouseList: async () => {
    return await request.get({ url: `/erplus/warehouse/simple-list` })
  },

  // 查询ERP 仓库详情
  getWarehouse: async (id: number) => {
    return await request.get({ url: `/erplus/warehouse/get?id=` + id })
  },

  // 新增ERP 仓库
  createWarehouse: async (data: Warehouse) => {
    return await request.post({ url: `/erplus/warehouse/create`, data })
  },

  // 修改ERP 仓库
  updateWarehouse: async (data: Warehouse) => {
    return await request.put({ url: `/erplus/warehouse/update`, data })
  },

  // 删除ERP 仓库
  deleteWarehouse: async (id: number) => {
    return await request.delete({ url: `/erplus/warehouse/delete?id=` + id })
  },

  /** 批量删除ERP 仓库 */
  deleteWarehouseList: async (ids: number[]) => {
    return await request.delete({ url: `/erplus/warehouse/delete-list?ids=${ids.join(',')}` })
  },

  // 获取仓库库存列表
  getWarehouseInventoryList: async (warehouseId: number) => {
    return await request.get({ url: `/erplus/warehouse/inventory-list?warehouseId=` + warehouseId })
  },

  // 获取仓库库存分页
  getWarehouseInventoryPage: async (data: any) => {
    return await request.post({ url: `/erplus/inventory/page`, data })
  }
}
