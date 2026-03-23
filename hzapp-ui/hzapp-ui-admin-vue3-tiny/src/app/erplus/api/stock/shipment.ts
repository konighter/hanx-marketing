import request from '@/config/axios'

export enum ShipmentStatus {
  INIT = 0,
  AUDITING = 10,
  PENDING_BOXING = 20,
  PENDING_SHIPMENT = 30,
  PENDING_LABEL = 40,
  PENDING_DELIVERY = 50,
  SHIPPED = 90,
  CANCELED = 99
}

export const shipmentApi = {
  getShipmentPage: async (data: any) => {
    return await request.post({ url: '/erplus/inventory/shipment/page', data })
  },

  saveShipment: async (data: any) => {
    return await request.post({ url: '/erplus/inventory/shipment/save', data })
  },

  submitShipment: async (data: any) => {
    return await request.post({ url: '/erplus/inventory/shipment/submit', data })
  },

  auditShipment: async (data: any) => {
    return await request.post({ url: '/erplus/inventory/shipment/audit', data })
  },

  getShipment: async (id: number) => {
    return await request.get({ url: '/erplus/inventory/shipment?id=' + id })
  },

  updateShipmentStatus: async (data: any) => {
    return await request.post({ url: '/erplus/inventory/shipment/status', data })
  }
}
