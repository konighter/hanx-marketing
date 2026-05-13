import { Layout } from '@/utils/routerHelper'

const erplusRouter: AppRouteRecordRaw[] = [
  {
    path: '/erplus',
    component: Layout,
    name: 'ErplusDashboard',
    meta: {
      hidden: true,
      title: '跨境业务大盘',
      icon: 'ep:data-line'
    },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/app/erplus/views/dashboard/index.vue'),
        name: 'ErplusDashboardIndex',
        meta: {
          title: '业务大盘',
          icon: 'ep:data-line',
          noCache: false,
          affix: import.meta.env.VITE_APP_HOME_PATH === '/erplus/dashboard'
        }
      }
    ]
  },
  {
    path: '/auth/callback/:authType',
    component: () => import('@/app/erplus/views/system/shop/AuthCallback.vue'),
    name: 'AuthCallback',
    meta: {
      hidden: true,
      title: '授权回调',
      noTagsView: true
    }
  },
  {
    path: '/erplus/adv/auth/callback',
    component: () => import('@/app/erplus/views/adv/auth/callback.vue'),
    name: 'advCallback',
    meta: {
      hidden: true,
      title: '授权回调',
      noTagsView: true
    }
  }
]

export default erplusRouter
