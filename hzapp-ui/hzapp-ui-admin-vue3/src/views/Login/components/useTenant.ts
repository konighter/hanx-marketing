import {TenantVO} from "@/api/system/tenant";
import * as LoginApi from '@/api/login'

export const getUserTenants = async (): Promise<TenantVO[]> => {
  const response = await LoginApi.getUserTenants()
  return response.data as TenantVO[]
}
