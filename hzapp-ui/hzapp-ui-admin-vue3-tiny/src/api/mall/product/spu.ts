export interface Sku {
  id: number;
  picUrl: string;
  properties: any[];
  price: number;
}
export interface Spu {
  skus: Sku[];
  deliveryTypes?: number[];
  deliveryTemplateId?: number;
}
export const getSpu = async (_spuId: number): Promise<Spu> => {
  return { skus: [] }
};
export const getSpuDetailList = async (_ids: number[]): Promise<Spu[]> => {
  return []
};
export const getSpuPage = async (_params: any): Promise<{list: Spu[], total: number}> => {
  return { list: [], total: 0 };
};

export const getTabsCount = async (): Promise<any> => {
  return { 0: 0, 1: 0, 2: 0, 3: 0, 4: 0 };
};

export const updateStatus = async (_data: any): Promise<void> => {
  return;
};

export const deleteSpu = async (_id: number): Promise<void> => {
  return;
};

export const exportSpu = async (_params: any): Promise<any> => {
  return new Blob();
};

export const getSpuSimpleList = async (): Promise<Spu[]> => {
  return [];
};

export const createSpu = async (_data: any): Promise<void> => {
  return;
};

export const updateSpu = async (_data: any): Promise<void> => {
  return;
};
