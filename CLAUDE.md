# Project Context: hzapp-erplus

An ERP management system built with Spring Boot (Java) and Vue 3 (Frontend).

## Tech Stack
- **Backend**: Java 17+, Spring Boot, MyBatis-Plus, Maven, Redis, MySQL.
- **Frontend**: Vue 3 (Composition API), Vite, Element Plus, TypeScript, pnpm, UnoCSS.
- **Architecture**: Modular monolith with `hzapp-module-erplus` containing the core ERP business logic.

## Build & Run Commands
### Backend (Root)
- Build: `mvn clean install`
- Run Server: `mvn spring-boot:run -pl hzapp-server`

### Frontend (`hzapp-ui/hzapp-ui-admin-vue3-tiny`)
- Install: `pnpm i`
- Dev: `pnpm dev`
- Build: `pnpm build:local`
- Lint: `pnpm lint:eslint`

## Coding Standards & Guidelines

### Java (Backend)
- **Avoid Fully Qualified Names**: Do not use full paths like `java.util.List` in code. Always add `import` and use short names.
- **Naming Conventions**: 
  - Data Objects: `Erp*DO`
  - Response VOs: `Erp*RespVO`
  - Request VOs: `Erp*SaveReqVO`, `Erp*PageReqVO`
- **Controller Layer**: Controllers act as the external interface layer, only handling protocol-related logic (request parsing, authorization, basic validation). All business calculations, data queries, and metadata enrichment/assembly MUST be consolidated into the Service layer to maintain clean architecture.
- **Batch Optimization**: Avoid N+1 query problems in loops. Use batch retrieval services (e.g., `getMaterialStockCountMap`).

### Vue 3 (Frontend)
- **Directory Isolation**: Project-specific code resides in `src/app`. Do not modify files outside this folder (e.g., in `src/components` or `src/layout`) unless strictly necessary to fix core infrastructure.
- **Composition API**: Use `<script setup lang="ts">`.
- **UI Components**: Use Element Plus and global components like `Pagination` (for table pagination) and `Icon`.
- **API Services**: Define logic in `@/app/erplus/api/*` using `request`.
- **Styling**: Prefer UnoCSS (Tailwind-like classes) over scoped CSS when possible.

## Key Modules
- `hzapp-module-erplus`: Core ERP module (Stock, Material, Product, Assembly).
- `hzapp-ui`: Administrative frontend.
