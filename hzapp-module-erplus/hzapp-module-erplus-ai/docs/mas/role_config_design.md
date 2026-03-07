# MAS Agent 角色通用化设计 - 修改要点

## 变更概述

本次修改实现了 MAS (多智能体系统) 核心角色设置的通用化设计，主要包括：

1. **角色配置化** - 从硬编码枚举改为动态配置类
2. **Agent 注册中心** - 支持运行时动态注册和发现 Agent
3. **Prompt 模板化** - 支持占位符动态渲染
4. **新增角色实现** - EXPERT 和 REVIEWER 角色完整实现

---

## 文件变更清单

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `mas/framework/agent/MasRoleConfig.java` | 角色配置类，替代硬编码枚举 |
| `mas/framework/agent/MasAgentRegistry.java` | Agent 注册中心接口 |
| `mas/framework/agent/DefaultMasAgentRegistry.java` | Agent 注册中心默认实现 |
| `mas/framework/agent/ConfiguredMasAgent.java` | 配置驱动的 Agent 实现 |
| `mas/core/agent/ExpertAgent.java` | 领域专家角色实现 |
| `mas/core/agent/ReviewerAgent.java` | 审核人角色实现 |
| `docs/sql/mas_role_config_v2.sql` | 数据库升级脚本 |

### 修改文件

| 文件路径 | 变更内容 |
|---------|---------|
| `mas/core/orchestrator/MasOrchestrator.java` | 新增 agentRegistry 字段和 resolveAgent 方法 |

---

## 核心设计

### 1. MasRoleConfig - 角色配置类

```java
@Data
@Builder
public class MasRoleConfig {
    private String roleId;           // 角色唯一标识
    private String name;            // 角色显示名称
    private String description;     // 角色职责描述
    private List<String> defaultTools; // 默认工具列表
    private String systemPromptTemplate; // Prompt 模板
    private String extConfig;       // 扩展配置
    private boolean enabled;        // 是否启用
    private int priority;           // 优先级
}
```

**支持占位符**：
- `{{sessionId}}` - 会话 ID
- `{{goal}}` - 顶层目标
- `{{currentTask}}` - 当前任务
- `{{currentTaskResult}}` - 任务结果

### 2. MasAgentRegistry - 注册中心接口

```java
public interface MasAgentRegistry {
    void register(MasAgent agent);
    void register(String roleId, MasAgent agent);
    void unregister(String roleId);
    Optional<MasAgent> getAgent(String roleId);
    List<MasAgent> getAllAgents();
    List<MasAgent> getEnabledAgents();
    void loadFromConfig(List<MasRoleConfig> configs);
    void updateConfig(String roleId, MasRoleConfig config);
    Optional<MasRoleConfig> getConfig(String roleId);
    List<MasRoleConfig> getAllConfigs();
}
```

### 3. ConfiguredMasAgent - 配置驱动 Agent

- 根据 MasRoleConfig 动态创建
- 支持运行时 Prompt 模板渲染
- 支持默认工具集合配置

---

## 向后兼容性

1. **MasRole 枚举保留** - 标记为 @Deprecated，但仍然可用
2. **MasOrchestrator 构造函数兼容** - 旧版构造函数仍然可用
3. **activeAgents Map 保留** - 用于手动注册的 Agent

---

## 使用方式

### 方式一：使用注册中心（推荐）

```java
@Autowired
private MasAgentRegistry agentRegistry;

public void startSession(String goal) {
    MasOrchestrator orchestrator = new MasOrchestrator(
        sessionId,
        eventBus,
        taskExecutor,
        persistenceService,
        agentRegistry  // 传入注册中心
    );
    orchestrator.start(goal);
}
```

### 方式二：手动注册（兼容旧版）

```java
MasOrchestrator orchestrator = new MasOrchestrator(
    sessionId, eventBus, taskExecutor, persistenceService
);
orchestrator.registerAgent(new ManagerAgent(chatClientBuilder));
orchestrator.registerAgent(new PmAgent(chatClientBuilder));
```

### 动态更新配置

```java
MasRoleConfig newConfig = MasRoleConfig.builder()
    .roleId("CUSTOM_ROLE")
    .name("自定义角色")
    .systemPromptTemplate("新的 Prompt 模板...")
    .enabled(true)
    .build();

agentRegistry.updateConfig("CUSTOM_ROLE", newConfig);
```

---

## 数据库变更

执行 `docs/sql/mas_role_config_v2.sql` 后新增字段：

| 字段名 | 类型 | 说明 |
|-------|------|------|
| priority | INT | 优先级，数值越小越优先 |
| enabled | BIT | 是否启用 |
| prompt_template | TEXT | Prompt 模板 |
| version | INT | 配置版本号 |

---

## 后续扩展建议

1. **配置持久化服务** - 从数据库加载配置到注册中心
2. **热更新机制** - 配置变更后无需重启服务
3. **自定义角色支持** - 通过 API 动态创建新角色
4. **Agent 协作工作流** - 可配置的 Agent 协作模式
