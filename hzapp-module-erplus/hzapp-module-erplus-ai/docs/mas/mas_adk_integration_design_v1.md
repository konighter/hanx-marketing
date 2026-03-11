# MAS - Google ADK Integration Design (Phase 5)

## 1. 目标 (Goal)
将当前系统中持久化的智能体配置 (`MasAgentConfigDO`) 与刚才引入的 **Google ADK (0.8.0)** 原生对象模型进行完美转化与对接。这能确保我们自定义的 `BaseAgent` 通过 ADK 的内核驱动真正的 LLM 执行，又不丢失上层的编排灵活性。

## 2. 核心映射关系 (Data Mapping)

| 本地实体 `MasAgentConfigDO` 属性 | Google ADK 对应概念 / 映射逻辑                                                                 |
| ---------------------------------- | -------------------------------------------------------------------------------------------- |
| `roleCode` (角色代码)              | 映射给 MAS 内部调度的 `BaseAgent.getRoleName()`，决定它是否接受特定的 A2A Message。           |
| `agentName` (智能体名称)           | 传给 ADK 原生 Agent，用于日志和模型调用记录中的标识。                                         |
| `systemPrompt` (系统提示词)        | 映射为 ADK `Agent` 的 System Instructions。它决定了该 Agent 是充当 PM 还是 Reviewer。        |
| `toolBeans` (工具 Bean 列表 JSON)  | 转换为应用内的实际 Spring Bean，并按照 ADK 的 Tool 规范绑定到该 Agent (Function Calling)。   |
| `extConfig` (扩展配置 JSON)        | 提取出模型选用 (Model Name)、温度 (Temperature) 等核心参数，用于初始化 ADK `LanguageModel`。 |

## 3. 架构与组件扩展设计

### 3.1 `AdkAgentFactory`与动态加载
我们会引入一个统一的工厂类 `AdkAgentFactory`：
- 从数据库加载 `MasAgentConfigDO`。
- 解析 `extConfig` 并实例化 ADK 的 `LanguageModel` 接口。
- 解析 `toolBeans` 并使用 ADK 原生规范将 Spring 容器中的组件挂载为 Agent 的能力。
- 构建原生的 Google ADK Agent (例如 `Agent.builder().model(...).systemInstruction(...).tools(...).build()`)。

### 3.2 包装层 `DynamicAdkAgent`
我们会创建一个 `DynamicAdkAgent` 来实现我们第一阶段建立的 `BaseAgent`。
- **职责**：作为 Google ADK 原生 Agent 的外壳，适配我们在 Phase 3 构建的 `LoopGraphManager`。
- **执行**：在 `execute(String instruction, LoopMemory memory)` 方法中：
  1. 读取本节点的 `LoopMemory`，提取需要的上下文合并入 `Instruction` 中。
  2. 调用挂载的 Google ADK Agent 执行大模型的实际调用。
  3. 拿到 ADK 的返回文字后，将关键状态和提取的信息重新写入 `LoopMemory` 提供给下游。

## 4. 特殊模块对接方案
### 工具支持 (Tools / Skills)
- 我们将把系统内的 "爬虫工具"、"ERP查单工具" 注册为 Spring Bean。
- 在构建 ADK Agent 时，将对应解析到的 `toolBeans` 清单转换为 ADK 支持的 Tool，交由大模型自主通过 Function Calling 触发回调。

### 记忆穿越 (Memory Bridge)
- **ADK 内部记忆**：单次 Loop 中的多轮对话追问（如果有）由 ADK 自己的 `ChatMemory` 托管。
- **MAS 全局记忆**：一旦跳出单次推理(Execute)，生成的总结将直接落存到我们在 Phase 2 搞好的 `GlobalSessionMemory` 和 `LocalLoopMemory`，以便在整个有向无环图(DAG)里传递给其他并行 Agent 节点。

## 5. 验证标准
1. 能够从 DB 读取某配置后，反射或映射构建出一个生效的 `DynamicAdkAgent`。
2. 调用该对象的 `execute` 能够联通真实大模型，并在触发相应的 Tool 时正确获得结果。
