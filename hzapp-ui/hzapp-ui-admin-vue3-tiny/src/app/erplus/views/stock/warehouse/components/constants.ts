/**
 * ERP 库存账单业务类型常量
 */
export const ErpInventoryBillRefTypeEnum = {
  MANUAL: 'MANUAL', // 手动操作
  PURCHASE: 'PURCHASE', // 采购入库
  SALE: 'SALE', // 销售出库
  PRODUCTION: 'PRODUCTION', // 生产入库
  ASSEMBLY: 'ASSEMBLY', // 装配出库
  SHIPMENT: 'SHIPMENT', // platform货件
  TRANSFER_RECEIPT: 'TRANSFER_RECEIPT' // 调拨收货
}

/** 业务类型枚举值 -> 中文标签 */
export const ErpInventoryBillRefTypeLabelMap: Record<string, string> = {
  [ErpInventoryBillRefTypeEnum.MANUAL]: '手动操作',
  [ErpInventoryBillRefTypeEnum.PURCHASE]: '采购入库',
  [ErpInventoryBillRefTypeEnum.SALE]: '销售出库',
  [ErpInventoryBillRefTypeEnum.PRODUCTION]: '生产入库',
  [ErpInventoryBillRefTypeEnum.ASSEMBLY]: '装配出库',
  [ErpInventoryBillRefTypeEnum.SHIPMENT]: '平台货件',
  [ErpInventoryBillRefTypeEnum.TRANSFER_RECEIPT]: '调拨收货'
}

/**
 * 单据类型 (10:入库, 20:出库, 30:调拨) 与 业务类型 (refType) 的映射关系
 */
export const ErpInventoryBillTypeOptions = {
  10: [
    { value: ErpInventoryBillRefTypeEnum.MANUAL, label: '手动操作' },
    { value: ErpInventoryBillRefTypeEnum.PURCHASE, label: '采购入库' },
    { value: ErpInventoryBillRefTypeEnum.PRODUCTION, label: '生产入库' },
    { value: ErpInventoryBillRefTypeEnum.SHIPMENT, label: '平台货件' },
    { value: ErpInventoryBillRefTypeEnum.TRANSFER_RECEIPT, label: '调拨收货' }
  ],
  20: [
    { value: ErpInventoryBillRefTypeEnum.MANUAL, label: '手动操作' },
    { value: ErpInventoryBillRefTypeEnum.SALE, label: '销售出库' },
    { value: ErpInventoryBillRefTypeEnum.ASSEMBLY, label: '装配出库' },
    { value: ErpInventoryBillRefTypeEnum.SHIPMENT, label: '平台货件' }
  ],
  30: [
    { value: ErpInventoryBillRefTypeEnum.MANUAL, label: '手动动作' }
  ]
}

/** 物料类型枚举 */
export enum ErpItemTypeEnum {
  SKU = 1,
  MATERIAL = 2
}
