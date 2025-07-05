import {Layout} from "@/utils/routerHelper";


const remainingRouter: AppRouteRecordRaw[] = [

  {
    path: '/erp/product', // 商品中心
    component: Layout,
    name: 'ProductCenter',
    meta: {
      hidden: true
    },
    children: [
      {
        path: 'spu/add',
        component: () => import('@/app/erp/views/product/spu/form/index.vue'),
        name: 'ErpProductSpuAdd',
        meta: {
          noCache: true,
          hidden: true,
          canTo: true,
          icon: 'ep:edit',
          title: '商品添加',
          activeMenu: '/erp/product/product_plus'
        }
      },
      {
        path: 'spu/edit/:id(\\d+)',
        component: () => import('@/app/erp/views/product/spu/form/index.vue'),
        name: 'ErpProductSpuEdit',
        meta: {
          noCache: true,
          hidden: true,
          canTo: true,
          icon: 'ep:edit',
          title: '商品编辑',
          activeMenu: '/erp/product/product_plus'
        }
      },
      {
        path: 'spu/detail/:id(\\d+)',
        component: () => import('@/app/erp/views/product/spu/form/index.vue'),
        name: 'ErpProductSpuDetail',
        meta: {
          noCache: true,
          hidden: true,
          canTo: true,
          icon: 'ep:view',
          title: '商品详情',
          activeMenu: '/erp/product/product_plus'
        }
      },
      {
        path: 'property/value/:propertyId(\\d+)',
        component: () => import('@/views/mall/product/property/value/index.vue'),
        name: 'ErpProductPropertyValue',
        meta: {
          noCache: true,
          hidden: true,
          canTo: true,
          icon: 'ep:view',
          title: '商品属性值',
          activeMenu: '/product/property'
        }
      },
      {
        path: 'claim/:id(\\d+)',
        component: () => import('@/app/erp/views/product/spu/claim/index.vue'),
        name: 'ErpProductSpuClaim',
        meta: {
          noCache: true,
          hidden: true,
          canTo: true,
          icon: 'ep:view',
          title: '认领商品',
          activeMenu: '/erp/product/product_plus'
        }
      },
    ]
  },

]

export default remainingRouter
