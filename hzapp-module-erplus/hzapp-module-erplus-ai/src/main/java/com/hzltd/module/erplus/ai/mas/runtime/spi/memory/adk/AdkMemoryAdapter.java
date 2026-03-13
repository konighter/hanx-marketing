package com.hzltd.module.erplus.ai.mas.runtime.spi.memory.adk;

import com.google.adk.events.Event;
import com.google.adk.memory.BaseMemoryService;
import com.google.adk.memory.MemoryEntry;
import com.google.adk.memory.SearchMemoryResponse;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryEntry;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasMemoryManager;
import com.hzltd.module.erplus.ai.mas.runtime.spi.memory.MasSessionMemory;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ADK-compatible Memory Service that acts as an adapter for MAS Memory.
 * It translates ADK specific objects (Session, Event) into MAS agnostic calls.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdkMemoryAdapter implements BaseMemoryService {

    private final MasMemoryManager masMemoryManager;

    @Override
    public Completable addSessionToMemory(Session session) {
        return Completable.fromAction(() -> {
            log.info("[AdkMemoryAdapter] Adding session {} to MAS memory", session.id());
            MasSessionMemory globalMemory = masMemoryManager.getSessionMemory(session.id());

            // Convert ADK events to text summary
            List<Event> nonEmptyEvents = session.events().stream()
                    .filter(event -> event.content()
                            .flatMap(c -> c.parts())
                            .filter(parts -> !parts.isEmpty())
                            .isPresent())
                    .collect(Collectors.toList());

            if (nonEmptyEvents.isEmpty()) {
                log.debug("[AdkMemoryAdapter] No non-empty events in session {}", session.id());
                return;
            }

            // Build text summary from events
            StringBuilder summary = new StringBuilder();
            for (Event event : nonEmptyEvents) {
                String author = event.author();
                String content = event.stringifyContent();
                if (content != null && !content.isBlank()) {
                    summary.append("[").append(author).append("]: ").append(content).append("\n");
                }
            }

            String historyKey = "_adk_history_" + session.id();
            // Append to existing history if present
            Object existing = globalMemory.get(historyKey);
            String newHistory = (existing != null ? existing.toString() + "\n" : "") + summary.toString().trim();
            globalMemory.put(historyKey, newHistory);

            log.debug("[AdkMemoryAdapter] Stored {} events to MAS memory under key '{}'",
                    nonEmptyEvents.size(), historyKey);
        });
    }

    @Override
    public Single<SearchMemoryResponse> searchMemory(String appName, String userId, String query) {
        return Single.fromCallable(() -> {
            // Note: 'userId' here is the MAS sessionId passed from the agent for isolation.
            String sessionId = userId;
            log.debug("[AdkMemoryAdapter] Searching MAS memory for: {} in session: {}", query, sessionId);

            List<MasMemoryEntry> agnosticResults = masMemoryManager.searchMemory(sessionId, query);

            List<MemoryEntry> adkResults = agnosticResults.stream()
                    .map(res -> MemoryEntry.builder()
                            .content(Content.builder()
                                    .parts(List.of(Part.builder().text(res.getContent()).build()))
                                    .build())
                            .author(res.getAuthor())
                            .timestamp(res.getTimestamp())
                            .build())
                    .collect(Collectors.toList());

            return SearchMemoryResponse.builder()
                    .setMemories(adkResults)
                    .build();
        });
    }
}
