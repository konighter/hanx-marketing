# Vue 3 前端开发规范 (hzapp-ui)

## 1. 目录隔离原则 (Directory Isolation)

- **核心包保护**：严禁修改 `src/components`、`src/layout` 或 `src/hooks` 等通用架构文件夹，除非是为了修复核心基础组件的 Bug。
- **业务代码存放**：所有项目业务逻辑（页面、专属组件、API 定义）必须存放在 `src/app/erplus` 目录下。

## 2. 状态与逻辑 (Composition API)

- **脚本风格**：统一使用 `<script setup lang="ts">`。
- **响应式架构**：
    - 简单的 UI 状态直接使用 `ref` 或 `reactive`。
    - 复杂的联动逻辑（如 Amazon Listing Schema）应收敛到专用的 `use*` Hook 中。
- **表单校验**：优先使用 Element Plus 的 `el-form` 配合静态 `rules`。

## 3. UI 组件与样式

- **通用组件**：
    - 分页：优先使用全局封装的 `Pagination`。
    - 图标：使用全局 `Icon` 组件。
- **样式规范**：
    - 优先使用 **UnoCSS** (类 Tailwind) 进行布局与边距微调。
    - 严禁在组件内编写大量 Scoped CSS，除非是极其特殊的视觉效果。
- **API 交互**：
    - 所有的请求定义在 `src/app/erplus/api/` 目录下。
    - 使用封装好的 `request` 工具，不要直接调用 `axios`。

## 4. 优化技巧

- **懒加载**：对于大型抽屉 (Drawer) 或弹窗 (Modal) 中的复杂表单，考虑使用动态组件或懒渲染以提升开启速度。
- **TS 类型契约**：必须为所有的 API 返回结果填充类型定义（通常与后端的 RespVO 对应）。
