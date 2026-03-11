# MAS Runtime Engine Development Plan (v1)

This document breaks down the refactoring of the Multi-Agent System (MAS) runtime engine into actionable development phases. 

## Guiding Principle: Maximize Google ADK & Focus on the Upper Layer
**Do not reinvent the wheel.** The system must maximize the use of the official Google ADK Java SDK for core mechanisms (e.g., Model bindings, Agent Runtime execution, Memory management, A2A communication protocols). Our engineering focus is on building an **upper-layer Agent System** that is:
1. **User-Friendly:** Easy for operators to configure custom agents and view progress.
2. **Long-Term/Persistent:** Capable of running long-lifecycle tasks with robust checkpointing.
3. **Visual & Measurable:** Graph-based execution chains that can be rendered in the UI, with clear observability metrics for every loop and agent.

## Phase 1: Core Framework & ADK Integration
- **Goal:** Set up the basic runtime environment and integrate the Google ADK Java SDK.
- **Detailed Design:**
  - Create the `mas.runtime` package structure (`agent`, `loop`, `memory`, etc.).
  - Add Google ADK Java SDK dependencies to `pom.xml`.
  - Define the base interfaces: `BaseAgent`, `AgentContext`.
  - Implement a basic ApplicationRunner or initialization configuration to load the ADK environment within Spring Boot.
- **Verification Plan:**
  - Verify Maven builds successfully with ADK dependencies.
  - Write a simple unit test initializing a dummy ADK agent without doing any real work to guarantee context loads.

## Phase 2: Memory System Implementation
- **Goal:** Implement the isolated and global memory scoping required for parallel executions.
- **Detailed Design:**
  - Create `LocalLoopMemory` (ThreadLocal or explicitly passed context object) for `Think->Plan->Execute->Review` scratchpads.
  - Create `GlobalSessionMemory` as a thread-safe shared object (e.g., `ConcurrentHashMap` wrapped in a service) to store finalized task outputs.
  - Define interface methods for merging `LocalLoopMemory` into `GlobalSessionMemory` upon loop completion.
- **Verification Plan:**
  - Unit tests launching multiple threaded simulator loops writing to their own `LocalLoopMemory` and verifying no cross-contamination.
  - Unit tests verifying the merge logic into `GlobalSessionMemory`.

## Phase 3: Graph-Based Agent Loop & State Machine
- **Goal:** Build the core `Think -> Plan -> Execute -> Review` lifecycle and the Graph orchestrator.
- **Detailed Design:**
  - Implement `AgentLoopRunner` using ADK's workflow concepts. Define 4 discrete nodes as distinct executable states.
  - Implement a `LoopGraphManager` that parses task dependencies and constructs a DAG (Directed Acyclic Graph) of `AgentLoopRunner` instances.
  - Build the Thread/Coroutine pool executor to evaluate the graph and spawn parallel `AgentLoopRunner` instances when multiple graph nodes have satisfied dependencies.
- **Verification Plan:**
  - Unit test building a graph with 3 nodes (A -> B, A -> C) and verifying that B and C execute in parallel after A finishes.
  - Verify that the `Review` node inside a specific Loop behaves correctly before the next graph node triggers.

## Phase 4: A2A Communication Bus
- **Goal:** Enable direct agent-to-agent messaging for synchronization.
- **Detailed Design:**
  - Implement `A2AMessageBus` utilizing Spring's `ApplicationEventPublisher` or Project Reactor's `Sinks.Many`.
  - Define `AgentMessage` (sender, receiver, payload, correlationId).
  - Implement inbox consumption logic on `BaseAgent`.
- **Verification Plan:**
  - Unit test where Agent A sends a message to Agent B, and Agent B's inbox processor successfully handles and acknowledges it asynchronously.

## Phase 5: Agent Roles & Custom Loader
- **Goal:** Implement the concrete system agents and the custom agent injection mechanism.
- **Detailed Design:**
  - Implement `ProjectManagerAgent`, `ProductManagerAgent`, `ReviewExpertAgent`, and `ExecutionExpertAgent` extending `BaseAgent`.
  - Build `CustomAgentLoaderService` that reads a database config (e.g., `MasAgentConfigDO`) and instantiates a dynamic ADK Agent with specified Tools and Prompts.
  - Define the unified Tool Registry bridging generic Java methods to ADK Tools.
- **Verification Plan:**
  - Mock LLM responses to test PM Agent task decomposition.
  - Unit test parsing a dummy database config and successfully building a runnable Custom Agent instance.

## Phase 6: Task Persistence & Checkpointing
- **Goal:** Ensure system resilience by periodically saving loop states and allowing resumption.
- **Detailed Design:**
  - Hook into the `AgentLoopRunner` state transitions.
  - Implement `MasCheckpointService` to serialize `LocalLoopMemory` and the current state enum (Think/Plan/Execute/Review) into `MasTaskHistoryDO`.
  - Implement a `resume(sessionId, loopId)` method that deserializes the state and restarts the `AgentLoopRunner` from the saved node.
- **Verification Plan:**
  - Integration test: Run a loop, artificially throw an exception during `Execute` to crash it.
  - Call `resume()` and verify via logs that the loop bypasses `Think` and `Plan` and directly restarts the `Execute` node with the restored context.
