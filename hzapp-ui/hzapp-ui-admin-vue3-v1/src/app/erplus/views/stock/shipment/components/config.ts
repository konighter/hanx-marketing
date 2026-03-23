import { ShipmentStatus } from '@/app/erplus/api/stock/shipment'
import BoxPacking from '@/app/erplus/views/stock/components/amz_v2/BoxPackingV2.vue'
import ShipmentPlacement from '@/app/erplus/views/stock/components/amz_v2/ShipmentPlacementV2.vue'
import ShipmentLabel from '@/app/erplus/views/stock/components/amz_v2/ShipmentLabelV2.vue'
import ShipmentDelivery from '@/app/erplus/views/stock/components/amz_v2/ShipmentDeliveryV2.vue'

import ShipmentAudit from './ShipmentAudit.vue'

const platformComponentMap = {
  Amazon: {
    [ShipmentStatus.AUDITING]: ShipmentAudit,
    [ShipmentStatus.PENDING_BOXING]: BoxPacking,
    [ShipmentStatus.PENDING_SHIPMENT]: ShipmentPlacement,
    [ShipmentStatus.PENDING_LABEL]: ShipmentLabel,
    // [ShipmentStatus.PENDING_DELIVERY]: ShipmentDelivery,
    [ShipmentStatus.SHIPPED]: ShipmentDelivery
  }
}

export const selectComponent = (platformId: number, status: ShipmentStatus) => {
  const platform = platformComponentMap['Amazon']
  return platform[status]
}

/** 状态展示 */
export const getStatusLabel = (status: number) => {
  const statusMap: Record<number, string> = {
    [ShipmentStatus.INIT]: '已保存',
    [ShipmentStatus.AUDITING]: '待审核',
    [ShipmentStatus.PENDING_SHIPMENT]: '待配货',
    [ShipmentStatus.PENDING_BOXING]: '待装箱',
    [ShipmentStatus.PENDING_LABEL]: '待贴标',
    [ShipmentStatus.PENDING_DELIVERY]: '待发货',
    [ShipmentStatus.SHIPPED]: '已发货',
    [ShipmentStatus.CANCELED]: '已取消'
  }
  return statusMap[status] || '未知'
}
