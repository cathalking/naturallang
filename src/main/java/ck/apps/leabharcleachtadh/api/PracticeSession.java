package ck.apps.leabharcleachtadh.api;

import ck.apps.leabharcleachtadh.games.PracticeEngine;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.verblookup.BuNaMoVerbLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class PracticeSession {
    private final String id;
    private final List<VerbUsage> remaining;
    private final PracticeEngine engine;
    private final Integer maxQuestions;
    private int asked;
    private int correct;
    private int incorrect;
    private int skipped;
    private VerbUsage lastPrompt;

    PracticeSession(String id, List<VerbUsage> permutations, BuNaMoVerbLookup.PronounMode pronounMode, Integer maxQuestions) {
        this.id = id;
        this.remaining = new ArrayList<>(permutations);
        this.engine = new PracticeEngine(pronounMode);
        this.maxQuestions = maxQuestions;
    }

    String getId() {
        return id;
    }

    PromptResult nextPrompt() {
        if (maxQuestions != null && asked >= maxQuestions) {
            return new PromptResult(null, null, true, summary());
        }
        if (remaining.isEmpty()) {
            return new PromptResult(null, null, true, summary());
        }
        int index = ThreadLocalRandom.current().nextInt(remaining.size());
        lastPrompt = remaining.remove(index);
        asked++;
        return new PromptResult(lastPrompt, engine.prompt(lastPrompt), false, summary());
    }

    AnswerResult answer(String response) {
        if (lastPrompt == null) {
            throw new IllegalStateException("No prompt issued yet.");
        }
        boolean isCorrect = engine.isCorrect(lastPrompt, response);
        if (isCorrect) {
            correct++;
        } else {
            incorrect++;
        }
        String expected = engine.translate(lastPrompt);
        String prompt = engine.prompt(lastPrompt);
        return new AnswerResult(isCorrect, expected, prompt, lastPrompt, asked, remaining());
    }

    SummaryResult skip() {
        if (lastPrompt == null) {
            throw new IllegalStateException("No prompt issued yet.");
        }
        skipped++;
        lastPrompt = null;
        return summary();
    }

    SummaryResult summary() {
        return new SummaryResult(asked, correct, incorrect, skipped, remaining());
    }

    Integer remaining() {
        if (maxQuestions == null) {
            return remaining.size();
        }
        return Math.min(remaining.size(), Math.max(0, maxQuestions - asked));
    }

    record AnswerResult(boolean correct, String expected, String prompt, VerbUsage usage, int asked, Integer remaining) {}

    record SummaryResult(int asked, int correct, int incorrect, int skipped, Integer remaining) {}

    record PromptResult(VerbUsage usage, String prompt, boolean done, SummaryResult summary) {}
}
