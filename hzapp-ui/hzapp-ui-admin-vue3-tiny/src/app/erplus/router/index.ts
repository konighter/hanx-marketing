const erplusRouter: AppRouteRecordRaw[] = [
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
