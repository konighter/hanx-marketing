---
name: hzapp-project-guidelines
description: 翰展 ERP 项目 (hzapp-erplus) 开发规范指南。在 hzapp-erplus 仓库中进行 Java 后端开发、Vue 3 前端开发、处理 ERP 业务逻辑（库存、物料、装配）或重构 Controller/Service 时使用。
---

# hzapp-project-guidelines

本 Skill 基于项目的 `CLAUDE.md` 规范，指导在 `hzapp-erplus` 项目中的日常开发工作。

## 核心工作流

### 1. 后端 (Java) 开发
在处理后端接口或业务逻辑时，必须遵循“瘦 Controller、胖 Service”架构。

- **Controller**: 仅处理协议、入参校验及权限。
- **Service**: 负责所有业务计算、多表数据聚合及元数据装配。

详细规范参见：[backend.md](references/backend.md)

### 2. 前端 (Vue 3) 开发
在处理 `hzapp-ui` 模块下的前端代码时，确保目录隔离及 Composition API 的正确使用。

- **目录隔离**: 所有的业务代码必须位于 `src/app` 目录下。
- **组件规范**: 优先使用 Element Plus 和项目全局组件。

Detailed规范参见：[frontend.md](references/frontend.md)

## 工具与工作流规范

为了保证开发的高效性与安全性，必须遵循以下协作原则：

### 1. Skill 发现与使用
在开始任何特定任务（如 SEO 优化、性能分析、自动化测试等）前，必须先检索是否存在相关的 **Skill**。如果存在，应优先加载并遵循该 Skill 的指导。

### 2. 复杂任务与“超级力量”(Superpowers)
对于复杂的架构设计、跨模块重构或大型功能开发，必须：
- **进入规划模式**：利用 `writing-plans` 或相关的 `superpowers` 能力进行深度分析。
- **产出文档**：首先生成 `implementation_plan.md` 明确技术路径。
- **获取授权**：在开始执行任务前，务必获得用户的明确批准。

### 3. 简单任务与修改范围
对于修复 Bug、微调样式或简单的代码更新，可以快速执行，但必须：
- **明确范围**：在回复或执行前，口头或在记录中明确受影响的文件和逻辑范围。
- **最小化修改**：严格在授权范围内进行操作，避免过度重构或引入不相关的副作用。

## 规范检查清单

- [ ] 是否存在魔法数字？（应替换为 Enum）
- [ ] 是否在循环中查询了数据库？（应改为批次 Map 查询）
- [ ] Controller 是否包含了复杂的业务装配逻辑？（应下沉到 Service）
- [ ] 后端代码是否使用了全限定类名？（应改为 import）
- [ ] 前端代码是否修改了 `src/app` 以外的核心架构文件？（除非必要，否则应避免）
