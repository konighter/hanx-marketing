# MAS Runtime Engine Refactoring Plan (v2)

This document outlines the architecture and implementation plan for refactoring the Multi-Agent System (MAS) runtime engine, integrating the core philosophies of Google ADK (Agent Development Kit), while preserving the existing `masv0` legacy system.

## Guiding Principle: Maximize Google ADK & Focus on the Upper Layer
**Do not reinvent the wheel.** The system must maximize the use of the official Google ADK Java SDK for core mechanisms (e.g., Model bindings, Agent Runtime execution, Memory management, A2A communication protocols). Our engineering focus is on building an **upper-layer Agent System** that is:
1. **User-Friendly:** Easy for operators to configure custom agents and view progress.
2. **Long-Term/Persistent:** Capable of running long-lifecycle tasks with robust checkpointing.
3. **Visual & Measurable:** Graph-based execution chains that can be rendered in the UI, with clear observability metrics for every loop and agent.

## 1. Package Structure
Build the new runtime engine under the existing `mas` package conceptually as the new standard, ensuring zero collision with `masv0`.
- **Location:** `com.hzltd.module.erplus.ai.mas.runtime` (or `com.hzltd.module.erplus.ai.mas` directly).
- **Core modules:** `agent`, `loop`, `memory`, `communication`, `persistence`, `tools`.

## 2. Architecture & Google ADK Java SDK
We will integrate the **Google ADK Java SDK** to power the runtime.
- **Agent Loop (AgentLoop):** Replaces static orchestration. A Loop represents an independent task block. The overall session is a chain or graph of these Loops.
- **Loop Lifecycle:** `Think` -> `Plan` -> `Execute` (can dispatch multiple parallel sub-tasks) -> `Review`.
- **Graph-Based Execution Chain:** Instead of simple linear chaining, Loops are managed as a Directed Acyclic Graph (DAG). The session orchestrator builds an ADK execution graph where each node is an `AgentLoop`. This natively supports complex dependency definitions and visualization.
- **Loop Parallelism:** Can `AgentLoop`s run in parallel? **Yes.** By evaluating the graph dependencies dynamically, the orchestrator/PM can spawn multiple parallel Agent Loops for independent branches. This requires a robust Thread/Coroutine pool and carefully scoped memory to avoid race conditions.

## 3. Agent Roles Implementation
Agents are divided into two main categories:
1. **System Agents (Invisible to User):** Form the backbone of the system's runtime.
   - `ProjectManagerAgent`: Decomposes tasks, spawns Agent Loops, and routes to other Agents.
   - `ProductManagerAgent`: Handles product requirements and PRD generation.
   - `ReviewExpertAgent`: Evaluates work products (runs in the Review node of a Loop).
   - `ExecutionExpertAgent`: Concrete execution. Will support polymorphic configurations for copywriting, coding, drawing, video editing, etc.
2. **User Custom Agents:** The system will support dynamically loading custom-defined agents (with specific tools and instructions) that users create. These participate within the Execution nodes of the loops.

## 4. A2A (Agent-to-Agent) Communication
Instead of a central orchestrator strictly piping data, Agents will communicate directly using the Google ADK messaging patterns.
- **`A2AMessageBus`**: A lightweight local event bus where agents can send `AgentMessage`s to one another.
- This is crucial for cross-loop synchronization when parallel loops complete and need to merge results.

## 5. Memory System & Boundaries (Scoping)
Memory management is segmented to support parallel execution:
- **Loop Memory (Local Scope):** Context tightly bounded to a single `AgentLoop` execution. It stores intermediate scratchpads, local tool execution results, and the current state (`Think/Plan/Execute/Review`). Parallel loops have isolated Loop Memories to avoid thread contention.
- **Session Memory (Global Scope):** Shared context across the entire MAS Session. Accessible by all chained and parallel loops. Contains the original user goal, globally shared variables, and the finalized outputs of completed loops. Read-heavy, write-protected (writes require merge strategies when parallel loops finish).
- **Long-Term Memory (Persistent):** Historic context across different sessions (Vector DB / RAG). Stores user preferences, standard operating procedures (SOPs), and historical task results.

## 6. Task Persistence & Checkpoint Recovery
- **Snapshot Mechanism:** At every node transition within a Loop (`Think` -> `Plan` -> `Execute`), the Loop Memory and global Session Memory references are serialized into `MasTaskHistoryDO` or a similar snapshot table.
- **Checkpointing:** Because of the Loop architecture, if the system crashes during `Execute`, it can reload the Loop state and resume exactly from `Execute` without repeating `Think` and `Plan`. Parallel loops snapshot independently.

## 7. Verification Plan

### Automated Tests
- Unit tests for the `AgentLoop` state machine, specifically testing `Think` -> `Plan` -> parallel `Execute` -> `Review`.
- Unit tests for Memory Isolation: ensure parallel loops cannot overwrite each other's local context.
- Unit tests for checkpoint restoring mid-loop.

### Manual Verification
- Start a full MAS session demonstrating parallel Execution tasks (e.g., generating text and image simultaneously).
- Verify the Google ADK Java SDK initialization and direct A2A messages in the logs.
- Simulate an application crash/restart mid-task and verify that the session resumes correctly from the last checkpoint.
