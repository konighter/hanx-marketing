import {TenantVO} from "@/api/system/tenant";
import { useCache } from '@/hooks/web/useCache'

const { wsCache } = useCache()

const UserTenantKey = 'USER_TENANT'

export const setUserTenant = (tenants: TenantVO[]) => {
  wsCache.set(UserTenantKey, tenants);
}

export const getUserTenant = (): TenantVO[] => {
  return wsCache.get(UserTenantKey) as TenantVO[]
}