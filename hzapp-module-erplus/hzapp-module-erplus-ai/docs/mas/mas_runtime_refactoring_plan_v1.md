# MAS Runtime Engine Refactoring Plan (v1)

This document outlines the architecture and implementation plan for refactoring the Multi-Agent System (MAS) runtime engine, integrating the core philosophies of Google ADK (Agent Development Kit), while preserving the existing `masv0` legacy system.

## 1. Package Structure
Build the new runtime engine under the existing `mas` package conceptually as the new standard, ensuring zero collision with `masv0`.
- **Location:** `com.hzltd.module.erplus.ai.mas.runtime` (or `com.hzltd.module.erplus.ai.mas` directly).
- **Core modules:** `agent`, `loop`, `memory`, `communication`, `persistence`, `tools`.

## 2. Architecture & Google ADK Paradigm
The design will adopt Google ADK principles natively in Java:
- **Agent Loop (AgentLoop):** Replaces static orchestration. The state machine continually evaluates the environment and decides the next step (Think-Act-Observe).
- **Tool/Skill/MCP selection:** A unified registry allows any agent to dynamically discover and bind tools/skills/MCP definitions at runtime before execution.

## 3. Agent Roles Implementation
Agents will be implemented as distinct classes inheriting from a base `BaseAgent` class:
- `ProjectManagerAgent`: Decomposes tasks, and has the authority to select/routing to other Agents.
- `ProductManagerAgent`: Handles product requirements and PRD generation.
- `IndustryExpertAgent`: Domain-specific knowledge extraction.
- `ReviewExpertAgent`: Evaluates work products and returns feedback (Review loop iteration).
- `ExecutionExpertAgent`: Concrete execution. Will support polymorphic configurations for copywriting, coding, drawing, video editing, etc.

## 4. A2A (Agent-to-Agent) Communication
Instead of a central orchestrator strictly piping data, Agents will communicate directly.
- **`A2AMessageBus`**: A lightweight local event bus (or reactive stream) where agents can send `AgentMessage`s to one another (e.g., PM requests Review Expert).
- Each agent will have an inbox to process incoming peer messages.

## 5. Task Persistence & Checkpoint Recovery
- **Snapshot Mechanism:** At every loop iteration, the Agent's state (current task, memory context, next step) is serialized and saved via `MasPersistenceService`.
- **Checkpointing:** If the system restarts or a task fails, the `AgentLoop` can reload the latest state snapshot from the database and resume exact execution without starting from scratch.

## 6. Memory System
- **Short-Term Memory (STM):** Conversation history and scratchpads stored in-memory and persisted temporarily during the session (using `MasSessionVariableDO` concepts).
- **Long-Term Memory (LTM):** Historical task extraction, rules, and facts stored in a Vector Database or relational DB, retrievable via RAG by Agents when starting tasks.

## 7. Verification Plan

### Automated Tests
- Unit tests for the `AgentLoop` to ensure correct state transitions and checkpoint saving/loading.
- Unit tests for `A2AMessageBus` to verify direct messaging syntax and agent inbox consumption.
- Mock the LLM calls to test the `ProjectManagerAgent`'s decision-making in selecting appropriate downstream Agents.

### Manual Verification
- Start a full MAS session via the UI or Swagger/Postman.
- Monitor the logs to verify PM Agent decomposing the task and A2A communicating with the PM/Execution Agents.
- Simulate an application crash/restart mid-task and verify that the session resumes correctly from the last checkpoint.
