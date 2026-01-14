import request from '@/config/axios'

export interface ShipFromAddress {
  id?: number
  name: string
  phoneNumber: string
  addressLine1: string
  addressLine2?: string
  city: string
  stateOrRegion?: string
  postalCode: string
  countryCode: string
  phone?: string
  email?: string
  stateOrProvinceCode?: string
}

export interface InventoryItem {
  sellerSku: string
  platformProductId?: string
  quantity: number
}

export interface CreateInboundPlanRequest {
  name: string
  address: ShipFromAddress

  marketId: string
  shopId: number
  items: InventoryItem[]
}

export const preOwnerTypes = [
  { value: 0, label: '亚马逊贴标' },
  { value: 1, label: '商家贴标' },
  { value: 9, label: '其他' }
]

export const AmzInboundApi = {
  // 1. Create Inbound Plan
  createInboundPlan: async (data: CreateInboundPlanRequest) => {
    return await request.post({ url: `/erplus/amz-fulfill/create-inbound-plan`, data })
  },

  listInboundPlans: async (params: any) => {
    return await request.get({ url: `/erplus/amz-fulfill/list-inbound-plans`, params })
  },
  listInboundPlansPage: async (params: any) => {
    return await request.get({ url: `/erplus/amz-fulfill/list-inbound-plans-page`, params })
  },

  // 2. Set Packing Information
  setPackingInformation: async (data: any) => {
    return await request.post({ url: `/erplus/amz-fulfill/set-packing-info`, data })
  },

  generatePackingOptions: async (data: any) => {
    return await request.post({ url: `/erplus/amz-fulfill/generate-packing-options`, data })
  },

  listPackingOptions: async (planId: string) => {
    return await request.post({
      url: `/erplus/amz-fulfill/list-packing-options`,
      data: { planId }
    })
  },

  confirmPackingOption: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/confirm-packing-option`,
      data
    })
  },

  getPackingInfo: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/get-packing-info`,
      data
    })
  },

  // 3. Generate Placement Options
  generatePlacementOptions: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/generate-placement-options`,
      data
    })
  },

  listPlacementOptions: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/list-placement-options`,
      data
    })
  },

  // 4. Confirm Placement Option
  confirmPlacementOption: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/confirm-placement-option`,
      data
    })
  },

  getPlacementInfo: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/get-placement-info`,
      data
    })
  },

  setPlacementInformation: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/set-placement-info`,
      data
    })
  },

  // 5. List Transportation Options
  listTransportationOptions: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/list-transportation-options`,
      data
    })
  },

  generateTransportationOptions: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/generate-transportation-options`,
      data
    })
  },

  // 6. Confirm Transportation Options
  confirmTransportationOptions: async (data: any) => {
    return await request.post({
      url: `/erplus/amz-fulfill/confirm-transportation-options`,
      data
    })
  },

  // 7. Get Labels
  getLabels: async (planId: string, shipmentId: string, params: any) => {
    return await request.get({
      url: `/erplus/amz-fulfill/plans/${planId}/shipments/${shipmentId}/labels`,
      params
    })
  },

  // Get Inbound Plan Details
  getInboundPlan: async (planId: string) => {
    return await request.get({ url: `/erplus/amz-fulfill/plans/${planId}` })
  },

  // Get Ship From Addresses
  getShipFromAddresses: async () => {
    return await request.get({ url: `/erplus/amz-fulfill/addresses` })
  }
}
