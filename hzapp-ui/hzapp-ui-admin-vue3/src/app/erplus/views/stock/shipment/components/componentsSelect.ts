import { ShipmentStatus } from '@/app/erplus/api/stock/shipment'
import BoxPacking from '@/app/erplus/views/stock/components/amz_v2/BoxPackingV2.vue'
import ShipmentPlacement from '@/app/erplus/views/stock/components/amz_v2/ShipmentPlacementV2.vue'

const platformComponentMap = {
  Amazon: {
    [ShipmentStatus.AUDITING]: BoxPacking,
    [ShipmentStatus.PENDING_BOXING]: BoxPacking,
    [ShipmentStatus.PENDING_SHIPMENT]: ShipmentPlacement,
    [ShipmentStatus.PENDING_LABEL]: BoxPacking,
    [ShipmentStatus.PENDING_DELIVERY]: BoxPacking,
    [ShipmentStatus.SHIPPED]: BoxPacking
  }
}

export const selectComponent = (platformId: number, status: ShipmentStatus) => {
  const platform = platformComponentMap['Amazon']
  return platform[status]
}
