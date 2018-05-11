package ck.apps.leabharcleachtadh.sentencegenerator.domain;

import ck.apps.leabharcleachtadh.sentencegenerator.Verb;

public class VerbUsage {
    private Verb verb;
    private SentenceForm sentenceForm;
    private Subject subject;
    private Tense tense;
    private final String object;

    private VerbUsage(SentenceForm form, Verb verb, Subject subject, Tense tense, String object) {
        this.sentenceForm = form;
        this.verb = verb;
        this.subject = subject;
        this.tense = tense;
        this.object = object;
    }

    public static VerbUsage usage(SentenceForm form, Verb verb, Subject subject, Tense tense, String object) {
        return new VerbUsage(form, verb, subject, tense, object);
    }

    public Verb getVerb() {
        return verb;
    }

    public Subject getSubject() {
        return subject;
    }

    public Tense getTense() {
        return tense;
    }

    public SentenceForm getSentenceForm() {
        return sentenceForm;
    }

    public String getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Usage{" +
                "form=" + sentenceForm +
                ", verb=" + verb +
                ", subject=" + subject +
                ", tense=" + tense +
                '}';
    }

}
