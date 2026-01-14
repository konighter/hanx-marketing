export const SHIPPING_MODES = [
  { label: '地面小包裹 (SPD)', value: 'GROUND_SMALL_PARCEL' },
  { label: '零担货运 (LTL)', value: 'FREIGHT_LTL' },
  { label: '整车运输 (托盘)', value: 'FREIGHT_FTL_PALLET' },
  { label: '整车运输 (非托盘)', value: 'FREIGHT_FTL_NONPALLET' },
  { label: '海运拼箱 (LCL)', value: 'OCEAN_LCL' },
  { label: '海运整箱 (FCL)', value: 'OCEAN_FCL' },
  { label: '空运小包裹', value: 'AIR_SMALL_PARCEL' },
  { label: '空运小包裹 (快递)', value: 'AIR_SMALL_PARCEL_EXPRES' }
]

export const SHIPPING_SOLUTIONS = [
  { label: '亚马逊合作承运人', value: 'AMAZON_PARTNERED_CARRIER' },
  { label: '非亚马逊合作承运人', value: 'USE_YOUR_OWN_CARRIER' }
]

export const SHIPPING_MODES_MAP: Record<string, string> = SHIPPING_MODES.reduce(
  (acc, cur) => {
    acc[cur.value] = cur.label
    return acc
  },
  {} as Record<string, string>
)

export const SHIPPING_SOLUTIONS_MAP: Record<string, string> = SHIPPING_SOLUTIONS.reduce(
  (acc, cur) => {
    acc[cur.value] = cur.label
    return acc
  },
  {} as Record<string, string>
)

export const AVAILABILITY_TYPES_MAP: Record<string, string> = {
  AVAILABLE: '可用',
  UNAVAILABLE: '不可用',
  LIMITED: '有限'
}
