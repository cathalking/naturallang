package ck.apps.leabharcleachtadh.api;

import ck.apps.leabharcleachtadh.games.Practice;
import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.verblookup.BuNaMoVerbLookup;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class PracticeSessionManager {
    private final Map<String, PracticeSession> sessions = new ConcurrentHashMap<>();

    PracticeSession createSession(List<Verb> verbs,
                                  List<SentenceForm> forms,
                                  List<Tense> tenses,
                                  List<Subject> subjects,
                                  String object,
                                  Integer maxQuestions,
                                  BuNaMoVerbLookup.PronounMode pronounMode) {
        Verb[] verbArray = verbs.toArray(new Verb[0]);
        SentenceForm[] formArray = forms.toArray(new SentenceForm[0]);
        Tense[] tenseArray = tenses.toArray(new Tense[0]);
        Subject[] subjectArray = subjects.toArray(new Subject[0]);
        String resolvedObject = object == null ? "(the thing)" : object;
        List<VerbUsage> permutations = Practice.permutations(verbArray, formArray, tenseArray, subjectArray, resolvedObject);
        String id = UUID.randomUUID().toString();
        PracticeSession session = new PracticeSession(id, permutations, pronounMode, maxQuestions);
        sessions.put(id, session);
        return session;
    }

    PracticeSession getSession(String id) {
        return sessions.get(id);
    }

    PracticeSession removeSession(String id) {
        return sessions.remove(id);
    }
}
