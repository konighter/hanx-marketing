import type { App } from 'vue'
import ArcoVue from '@arco-design/web-vue';

export const setupArco = (app: App<Element>) => {
  app.use(ArcoVue, {
  // 用于改变使用组件时的前缀名称
  componentPrefix: 'arco'
});
}