package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.google.adk.events.Event;
import com.google.adk.memory.BaseMemoryService;
import com.google.adk.memory.MemoryEntry;
import com.google.adk.memory.SearchMemoryResponse;
import com.google.adk.sessions.Session;
import com.google.common.collect.ImmutableSet;
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

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * ADK-compatible Memory Service that integrates with MAS Graph-level memory.
 * Stores ADK session events into GlobalSessionMemory and provides keyword-based search
 * across both session events (history) and global session variables.
 * 
 * Optimized to use the ADK 'userId' (mapped from MAS sessionId) for isolated searches.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GraphMemoryService implements BaseMemoryService {

    private final MasMemoryService masMemoryService;
    private static final Pattern WORD_PATTERN = Pattern.compile("[A-Za-z]+");

    @Override
    public Completable addSessionToMemory(Session session) {
        return Completable.fromAction(() -> {
            log.info("[GraphMemory] Adding session {} to graph memory", session.id());
            GlobalSessionMemory globalMemory = masMemoryService.getSessionMemory(session.id());
            
            List<Event> nonEmptyEvents = session.events().stream()
                    .filter(event -> event.content()
                            .flatMap(c -> c.parts())
                            .filter(parts -> !parts.isEmpty())
                            .isPresent())
                    .collect(toImmutableList());
            
            globalMemory.getSessionEvents().addAll(nonEmptyEvents);
            log.debug("[GraphMemory] Added {} events to session {}", nonEmptyEvents.size(), session.id());
        });
    }

    @Override
    public Single<SearchMemoryResponse> searchMemory(String appName, String userId, String query) {
        return Single.fromCallable(() -> {
            // Note: 'userId' here is the MAS sessionId passed from DynamicAdkAgent for isolation.
            String sessionId = userId;
            log.debug("[GraphMemory] Searching memory for query: '{}' in session: {}", query, sessionId);
            
            ImmutableSet<String> wordsInQuery = ImmutableSet.copyOf(query.toLowerCase(Locale.ROOT).split("\\s+"));
            List<MemoryEntry> matchingMemories = new ArrayList<>();

            // Targeted search: only search in the specific session identified by 'userId'
            GlobalSessionMemory globalMemory = masMemoryService.getSessionMemory(sessionId);
            
            // 1. Search through session events (Conversation History)
            searchEvents(globalMemory.getSessionEvents(), wordsInQuery, matchingMemories);
            
            // 2. Search through session variables (Graph State / Intermediate Results)
            searchVariables(globalMemory.snapshot(), wordsInQuery, matchingMemories);

            log.info("[GraphMemory] Search in session {} found {} matching memory entries", sessionId, matchingMemories.size());
            return SearchMemoryResponse.builder()
                    .setMemories(matchingMemories)
                    .build();
        });
    }

    private void searchEvents(List<Event> events, Set<String> wordsInQuery, List<MemoryEntry> results) {
        for (Event event : events) {
            if (event.content().isEmpty() || event.content().get().parts().isEmpty()) {
                continue;
            }

            Set<String> wordsInEvent = extractWords(event.stringifyContent());
            if (wordsInEvent.isEmpty()) continue;

            if (!Collections.disjoint(wordsInQuery, wordsInEvent)) {
                results.add(MemoryEntry.builder()
                        .content(event.content().get())
                        .author(event.author())
                        .timestamp(Instant.ofEpochSecond(event.timestamp()).toString())
                        .build());
            }
        }
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
