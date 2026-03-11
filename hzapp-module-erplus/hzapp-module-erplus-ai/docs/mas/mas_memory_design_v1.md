# MAS Runtime: Long-term & Hierarchical Memory Design (Phase 7)

## Overview
As Agent sessions grow in duration and complexity, managing context efficiently becomes critical. Phase 7 introduces hierarchical memory and archiving strategies to ensure performance and reliability.

## Hierarchical Memory Model

1.  **Local Memory (Scratchpad)**: Transient, loop-specific data. Discarded or merged after completion.
2.  **Global Session Memory**: Shared context available to all agents in the current session. Persisted as session variables.
3.  **Long-term Memory (Archive)**: Historical data from previous sessions or completed tasks, moved to long-term storage (Vector DB or normalized table) to save working context space.

## Proposed Components

### 1. `MasMemoryService`
A central service to manage the lifecycle of memory.
- **Context Pruning**: Automatically summarizes or archives old session variables when the "Working Memory" exceeds a configurable token limit.
- **Hierarchical Lookup**: When a key is missing in Local/Global, the system can optionally query Long-term memory.

### 2. `LongTermMemoryService`
Handles the actual persistence of archived data.
- **Table**: `mas_session_variable` (status=ARCHIVED) or a dedicated vector table.
- **Retrieval**: Semantic search (optional) or keyword-based lookup.

## Context Management Strategy

- **FIFO Summarization**: When context is too large, the earliest parts of the session are summarized by a "Summarizer Agent" and compressed into a single "Summary Variable".
- **ADK Integration**: Leverages `com.google.adk.memory.BaseMemoryService` if possible for standardized artifact handling.

## Implementation Tasks

1.  Create `LongTermMemoryService` with archiving capability.
2.  Implement `MemoryPruningListener` that monitors `GlobalSessionMemory` size.
3.  Add "Summarize" utility to `ProjectManagerAgent` or a dedicated `MemoryAgent`.
