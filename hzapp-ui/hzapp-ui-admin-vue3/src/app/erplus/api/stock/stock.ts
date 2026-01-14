import request from '@/config/axios'
import type { Dayjs } from 'dayjs';
import { get } from 'http';



export interface WarehouseInventoryQuery {
    warehouseId: number; // 出仓仓库ID
    inboundWarehouseId: number; // 入仓仓库ID
    
}


export const getTransferAvailablInventory = async (data: WarehouseInventoryQuery) => {

  return await request.post({ url: '/erplus/stock/transfer_available', data })


}