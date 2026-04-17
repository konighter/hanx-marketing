import { MockMethod } from 'vite-plugin-mock'

/**
 * vite-plugin-mock v3 要点:
 * 1. url 必须是 string（path-to-regexp 格式），不能是 RegExp
 * 2. url 必须带上 axios baseURL 前缀 /admin-api
 * 3. response 回调签名为 ({ url, body, query, headers }) => any
 * 4. mock 中间件在 proxy 之前执行，匹配到则直接返回，不会继续走 proxy
 */

// 抽取自 ListingV2VO 的主要字段模型
const mockList = Array.from({ length: 12 }, (_, i) => ({
  id: 1000 + i,
  platformId: i % 2 === 0 ? 1 : 2, // 1: Amazon, 2: Shopee
  platformProductCode: `ASIN-000${i}`,
  sellerProductCode: `SKU-PROD-${i}`,
  title: `[Mock] 高性能跨境专供商品 - 迭代版本 ${i + 1}`,
  status: i % 3 === 0 ? 'active' : (i % 3 === 1 ? 'pending' : 'soldout'),
  statusName: i % 3 === 0 ? '在线' : (i % 3 === 1 ? '审核中' : '已下架'),
  mainImage: { url: `https://picsum.photos/200/200?random=${i}` },
  price: [{ salePrice: 1990 + i * 100, currency: 'USD' }],
  diagnosis: {
    score: 85 - i * 2,
    issues: [
      { type: 'warning', label: '主图非纯白', description: '底部由于光影产生的一点点灰度' },
      { type: 'info', label: '标题描述可优化', description: '建议增加核心成交词' }
    ]
  },
  performance: {
    sales7d: 120 + i * 5,
    sales30d: 450 + i * 20,
    gmv30d: 8900 + i * 500,
    revenueCurve: Array.from({ length: 10 }, () => Math.floor(Math.random() * 100))
  },
  mockVariants: [
    { sku: `SKU-PROD-${i}-XL`, spec: 'XL / Black', price: 2999, stock: 45, sales7d: 58, gmv30d: 1739 },
    { sku: `SKU-PROD-${i}-L`, spec: 'L / White', price: 2899, stock: 12, sales7d: 12, gmv30d: 347 },
    { sku: `SKU-PROD-${i}-M`, spec: 'M / Black', price: 2799, stock: 0, sales7d: 5, gmv30d: 139 },
    { sku: `SKU-PROD-${i}-S`, spec: 'S / White', price: 2699, stock: 5, sales7d: 22, gmv30d: 593 },
    { sku: `SKU-PROD-${i}-XS`, spec: 'XS / Black', price: 2599, stock: 2, sales7d: 3, gmv30d: 77 }
  ]
}))

export default [
  // {
  //   url: '/admin-api/erplus/cross-product/page',
  //   method: 'post',
  //   response: ({ body }) => {
  //     console.log('[Mock] Hit: /admin-api/erplus/cross-product/page, body:', body)
  //     const { pageNo = 1, pageSize = 12 } = body || {}
  //     return {
  //       code: 0,
  //       data: {
  //         list: mockList.slice((pageNo - 1) * pageSize, pageNo * pageSize),
  //         total: 48
  //       },
  //       msg: 'success'
  //     }
  //   }
  // },
] as MockMethod[]
