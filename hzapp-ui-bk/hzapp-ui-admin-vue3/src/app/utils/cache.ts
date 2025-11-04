import { useCache } from '@/hooks/web/useCache'
import Logger from '@/utils/Logger'

const { wsCache } = useCache('sessionStorage')

export const cacheLoader = async (key: string, fetcher: () => Promise<any>) => {
  const cachedData = wsCache.get(key) || undefined
  Logger.prettyInfo(`cacheLoader - retrieved from cache key:${key} data:`, cachedData)
  if (cachedData) {
    return cachedData
  } else {
    const data = await fetcher()
    wsCache.set(key, data)
    return data
  }
}





