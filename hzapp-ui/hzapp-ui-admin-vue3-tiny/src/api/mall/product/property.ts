export interface PropertyVO { id?: number; name?: string; }
export const getPropertyPage = async () => ({ list: [], total: 0 });
export const getPropertyListAndValue = async () => ([]);
export const getProperty = async () => ({});
export const createProperty = async () => ({});
export const updateProperty = async () => ({});
export const deleteProperty = async () => ({});

export const getPropertyValuePage = async (_params: any) => ({ list: [], total: 0 });
export const createPropertyValue = async (_data: any) => ({});
export const updatePropertyValue = async (_data: any) => ({});
export const deletePropertyValue = async (_id: number) => ({});
