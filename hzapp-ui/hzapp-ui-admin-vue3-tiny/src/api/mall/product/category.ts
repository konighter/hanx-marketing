export interface Category {
  id: number;
  parentId: number;
  name: string;
}

export type CategoryVO = Category;

export const getCategoryList = async (_params: any): Promise<Category[]> => {
  return [];
};
