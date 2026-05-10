import router from './router'
import type { RouteRecordRaw } from 'vue-router'
import { isRelogin } from '@/config/axios/service'
import { getAccessToken, getTenantId } from '@/utils/auth'
import { useTitle } from '@/hooks/web/useTitle'
import { useNProgress } from '@/hooks/web/useNProgress'
import { usePageLoading } from '@/hooks/web/usePageLoading'
import { useDictStoreWithOut } from '@/store/modules/dict'
import { useUserStoreWithOut } from '@/store/modules/user'
import { usePermissionStoreWithOut } from '@/store/modules/permission'

const { start, done } = useNProgress()

const { loadStart, loadDone } = usePageLoading()

const parseURL = (
  url: string | null | undefined
): { basePath: string; paramsObject: { [key: string]: string } } => {
  // 如果输入为 null 或 undefined，返回空字符串和空对象
  if (url == null) {
    return { basePath: '', paramsObject: {} }
  }

  // 找到问号 (?) 的位置，它之前是基础路径，之后是查询参数
  const questionMarkIndex = url.indexOf('?')
  let basePath = url
  const paramsObject: { [key: string]: string } = {}

  // 如果找到了问号，说明有查询参数
  if (questionMarkIndex !== -1) {
    // 获取 basePath
    basePath = url.substring(0, questionMarkIndex)

    // 从 URL 中获取查询字符串部分
    const queryString = url.substring(questionMarkIndex + 1)

    // 使用 URLSearchParams 遍历参数
    const searchParams = new URLSearchParams(queryString)
    searchParams.forEach((value, key) => {
      // 封装进 paramsObject 对象
      paramsObject[key] = value
    })
  }

  // 返回 basePath 和 paramsObject
  return { basePath, paramsObject }
}

// 路由不重定向白名单
const whiteList = [
  '/login',
  '/social-login',
  '/auth-redirect',
  '/bind',
  '/register',
  '/oauthLogin/gitee',
  '/tenant/select',
  '/tenant/create'
]

// 路由加载前
router.beforeEach(async (to, from, next) => {
  start()
  loadStart()
  if (getAccessToken()) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      // 获取所有字典
      const dictStore = useDictStoreWithOut()
      const userStore = useUserStoreWithOut()
      const permissionStore = usePermissionStoreWithOut()
      if (!userStore.getIsSetUser) {
        try {
          // Pro Max: 并行加载字典和用户信息，减少首屏等待时间
          await Promise.all([
            !dictStore.getIsSetDict ? dictStore.setDictMap() : Promise.resolve(),
            userStore.setUserInfoAction()
          ])
          // 后端过滤菜单
          await permissionStore.generateRoutes()
          permissionStore.getAddRouters.forEach((route) => {
            router.addRoute(route as unknown as RouteRecordRaw) // 动态添加可访问路由表
          })
        } catch (error) {
          console.error('Initial data loading failed:', error)
          // 如果加载失败，且不是因为已经弹出重新登录框（即不是 401 导致的），则强制进入登录页
          if (!isRelogin.show) {
            next({ path: '/login', query: { redirect: to.fullPath } })
          }
          return
        }

        const redirectPath = (from.query.redirect || to.path) as string
        // 修复跳转时不带参数的问题
        const { paramsObject: query } = parseURL(redirectPath)
        const nextData = to.path === redirectPath ? { ...to, replace: true } : { path: redirectPath, query }
        
        // 路由白名单和特殊允许无租户访问的个人中心页面
        const noTenantWhiteList = [...whiteList, '/user/profile', '/user/profile2', '/profile', '/user/notify-message']
        
        // 校验租户：如果启用了租户，且没有选择租户，则强制跳转到租户选择页
        if (import.meta.env.VITE_APP_TENANT_ENABLE === 'true') {
          const tenants = userStore.getTenants
          const tenantId = getTenantId()
          // 情况一：用户完全没有租户
          if (tenants.length === 0 && !noTenantWhiteList.includes(to.path)) {
            next({ path: '/tenant/select', query: { ...to.query, type: 'create' } })
            return
          }
          // 情况二：用户有多个租户，但尚未选择租户 (如果是从登录页跳转来的，或者刷新页面)
          // 注意：排除白名单，防止死循环
          if (tenants.length > 0 && !tenantId && !noTenantWhiteList.includes(to.path)) {
            next({ path: '/tenant/select', query: { redirect: to.fullPath } })
            return
          }
        }

        if (whiteList.includes(to.path)) {
          next()
        } else {
          next(nextData)
        }
      } else {
        const noTenantWhiteList = [...whiteList, '/user/profile', '/user/profile2', '/profile', '/user/notify-message']
        if (import.meta.env.VITE_APP_TENANT_ENABLE === 'true') {
          const tenants = userStore.getTenants
          const tenantId = getTenantId()
          if ((tenants.length === 0 || (tenants.length > 0 && !tenantId)) && !noTenantWhiteList.includes(to.path)) {
            next({ path: '/tenant/select', query: { ...to.query, type: tenants.length === 0 ? 'create' : undefined } })
          } else {
            next()
          }
        } else {
          next()
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
    }
  }
})

router.afterEach((to) => {
  useTitle(to?.meta?.title as string)
  done() // 结束Progress
  loadDone()
})
