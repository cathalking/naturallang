package ck.apps.leabharcleachtadh.api;

import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.verblookup.BuNaMoVerbLookup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice")
class PracticeController {
    private final PracticeSessionManager sessionManager = new PracticeSessionManager();

    @PostMapping("/sessions")
    ResponseEntity<CreateSessionResponse> createSession(@RequestBody(required = false) CreateSessionRequest request) {
        CreateSessionRequest effective = request == null ? new CreateSessionRequest(null, null, null, null, null, null, null) : request;
        List<Verb> verbs;
        List<SentenceForm> forms;
        List<Tense> tenses;
        List<Subject> subjects;
        try {
            verbs = parseEnums(effective.verbs(), Verb.class, List.of(Verb.values()));
            forms = parseEnums(effective.forms(), SentenceForm.class, List.of(SentenceForm.values()));
            tenses = parseEnums(effective.tenses(), Tense.class, List.of(Tense.values()));
            subjects = parseEnums(effective.subjects(), Subject.class, List.of(Subject.values()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        BuNaMoVerbLookup.PronounMode pronounMode = parsePronounMode(effective.pronounMode());
        PracticeSession session = sessionManager.createSession(
                verbs,
                forms,
                tenses,
                subjects,
                effective.object(),
                effective.maxQuestions(),
                pronounMode
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateSessionResponse(session.getId(), effective.maxQuestions(), verbs.size() * forms.size() * tenses.size() * subjects.size()));
    }

    @GetMapping("/sessions/{id}/prompt")
    ResponseEntity<PromptResponse> nextPrompt(@PathVariable String id) {
        PracticeSession session = sessionManager.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        PracticeSession.PromptResult result = session.nextPrompt();
        if (result.done()) {
            return ResponseEntity.ok(new PromptResponse(null, result.summary().remaining(), true, result.summary()));
        }
        return ResponseEntity.ok(new PromptResponse(new PromptPayload(result.usage(), result.prompt()), result.summary().remaining(), false, result.summary()));
    }

    @PostMapping("/sessions/{id}/answer")
    ResponseEntity<AnswerResponse> answer(@PathVariable String id, @RequestBody AnswerRequest request) {
        PracticeSession session = sessionManager.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        if (request == null || request.response() == null) {
            return ResponseEntity.badRequest().build();
        }
        PracticeSession.AnswerResult result = session.answer(request.response());
        return ResponseEntity.ok(new AnswerResponse(result.correct(), result.expected(), result.prompt(), result.usage(), result.asked(), result.remaining()));
    }

    @GetMapping("/sessions/{id}/summary")
    ResponseEntity<PracticeSession.SummaryResult> summary(@PathVariable String id) {
        PracticeSession session = sessionManager.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session.summary());
    }

    @PostMapping("/sessions/{id}/quit")
    ResponseEntity<PracticeSession.SummaryResult> quit(@PathVariable String id) {
        PracticeSession session = sessionManager.removeSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session.summary());
    }

    private <E extends Enum<E>> List<E> parseEnums(List<String> raw, Class<E> type, List<E> defaults) {
        if (raw == null || raw.isEmpty()) {
            return defaults;
        }
        return raw.stream().map(value -> Enum.valueOf(type, value)).toList();
    }

    private BuNaMoVerbLookup.PronounMode parsePronounMode(String raw) {
        if (raw == null || raw.isBlank()) {
            return BuNaMoVerbLookup.PronounMode.STRICT;
        }
        return switch (raw.trim().toLowerCase()) {
            case "always" -> BuNaMoVerbLookup.PronounMode.ALWAYS;
            case "prefer-synthetic" -> BuNaMoVerbLookup.PronounMode.PREFER_SYNTHETIC;
            case "omit" -> BuNaMoVerbLookup.PronounMode.OMIT;
            case "strict" -> BuNaMoVerbLookup.PronounMode.STRICT;
            default -> BuNaMoVerbLookup.PronounMode.STRICT;
        };
    }

    record CreateSessionRequest(List<String> verbs,
                                List<String> forms,
                                List<String> tenses,
                                List<String> subjects,
                                String object,
                                Integer maxQuestions,
                                String pronounMode) {}

    record CreateSessionResponse(String sessionId, Integer maxQuestions, int totalPermutations) {}

    record PromptPayload(VerbUsage usage, String prompt) {}

    record PromptResponse(PromptPayload prompt, Integer remaining, boolean done, PracticeSession.SummaryResult summary) {}

    record AnswerRequest(String response) {}

    record AnswerResponse(boolean correct, String expected, String prompt, VerbUsage usage, int asked, Integer remaining) {}
}
