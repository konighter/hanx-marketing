package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.google.adk.events.Event;
import com.google.adk.memory.BaseMemoryService;
import com.google.adk.memory.MemoryEntry;
import com.google.adk.memory.SearchMemoryResponse;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * ADK-compatible Memory Service that integrates with MAS Graph-level memory.
 *
 * <h3>Phase 3 changes</h3>
 * <ul>
 *   <li>{@link #addSessionToMemory(Session)} now converts ADK events to text summaries
 *       stored in {@code GlobalSessionMemory} under {@code _adk_history_{sessionId}} key,
 *       instead of maintaining a separate {@code sessionEvents} list.</li>
 *   <li>{@code WORD_PATTERN} extended to match Chinese characters and digits for CJK search.</li>
 *   <li>{@code searchEvents()} removed — all search goes through globalContext variables.</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GraphMemoryService implements BaseMemoryService {

    private final MasMemoryService masMemoryService;

    /**
     * Extended word pattern supporting:
     * - Latin letters (A-Za-z)
     * - CJK Unified Ideographs (\\u4e00-\\u9fff)
     * - Digits (0-9)
     */
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fff\\w]+");

    @Override
    public Completable addSessionToMemory(Session session) {
        return Completable.fromAction(() -> {
            log.info("[GraphMemory] Adding session {} to graph memory", session.id());
            GlobalSessionMemory globalMemory = masMemoryService.getSessionMemory(session.id());

            // Convert ADK events to text summary and store in globalContext
            List<Event> nonEmptyEvents = session.events().stream()
                    .filter(event -> event.content()
                            .flatMap(c -> c.parts())
                            .filter(parts -> !parts.isEmpty())
                            .isPresent())
                    .collect(toImmutableList());

            if (nonEmptyEvents.isEmpty()) {
                log.debug("[GraphMemory] No non-empty events in session {}", session.id());
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

            log.debug("[GraphMemory] Stored {} events as text summary under key '{}' ({} chars)",
                    nonEmptyEvents.size(), historyKey, newHistory.length());
        });
    }

    @Override
    public Single<SearchMemoryResponse> searchMemory(String appName, String userId, String query) {
        return Single.fromCallable(() -> {
            // Note: 'userId' here is the MAS sessionId passed from DynamicAdkAgent for isolation.
            String sessionId = userId;
            log.debug("[GraphMemory] Searching memory for query: '{}' in session: {}", query, sessionId);

            Set<String> wordsInQuery = extractWords(query);
            List<MemoryEntry> matchingMemories = new ArrayList<>();

            // Targeted search: only search in the specific session identified by 'userId'
            GlobalSessionMemory globalMemory = masMemoryService.getSessionMemory(sessionId);

            // Search through all session variables (including ADK history text summaries)
            searchVariables(globalMemory.snapshot(), wordsInQuery, matchingMemories);

            log.info("[GraphMemory] Search in session {} found {} matching memory entries", sessionId, matchingMemories.size());
            return SearchMemoryResponse.builder()
                    .setMemories(matchingMemories)
                    .build();
        });
    }

    private void searchVariables(Map<String, Object> variables, Set<String> wordsInQuery, List<MemoryEntry> results) {
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            Set<String> wordsInVar = extractWords(key + " " + value);
            if (wordsInVar.isEmpty()) continue;

            if (!Collections.disjoint(wordsInQuery, wordsInVar)) {
                Content content = Content.builder()
                        .parts(List.of(Part.builder().text("Variable [" + key + "]: " + value).build()))
                        .build();

                results.add(MemoryEntry.builder()
                        .content(content)
                        .author("MAS-Graph-State")
                        .timestamp(Instant.now().toString())
                        .build());
            }
        }
    }

    /**
     * Extracts searchable words/tokens from text, supporting CJK characters,
     * Latin letters, and digits.
     */
    private Set<String> extractWords(String text) {
        if (text == null) return Collections.emptySet();
        Set<String> words = new HashSet<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            words.add(matcher.group().toLowerCase(Locale.ROOT));
        }
        return words;
    }
}
