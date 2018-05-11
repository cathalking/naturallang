package ck.apps.leabharcleachtadh.sentencegenerator.domain;

import ck.apps.leabharcleachtadh.verblookup.ConjugationFinder;

public enum SentenceForm {
    STATEMENT_POSITIVE(false, ConjugationFinder.STMT_POS),
    STATEMENT_NEGATIVE(false, ConjugationFinder.STMT_NEG),
    QUESTION_VERB_POSITIVE(true, ConjugationFinder.QUST_POS),
    QUESTION_VERB_NEGATIVE(true, ConjugationFinder.QUST_POS),
//    QUESTION_WHY_POSITIVE("why"),
//    QUESTION_WHY_NEGATIVE("why", false),
//    QUESTION_HOW_POSITIVE("how"),
//    QUESTION_HOW_NEGATIVE("how", false),
//    QUESTION_WHEN_POSITIVE("when"),
//    QUESTION_WHEN_NEGATIVE("when", false),
//    QUESTION_WHERE_POSITIVE("where"),
//    QUESTION_WHERE_NEGATIVE("where", false)
    ;

    private final boolean question;
    private final ConjugationFinder conjugationFinder;

    SentenceForm(boolean question, ConjugationFinder conjugationFinder) {
        this.question = question;
        this.conjugationFinder = conjugationFinder;
    }

    public boolean isQuestion() {
        return question;
    }

    public ConjugationFinder getConjugationFinder() {
        return conjugationFinder;
    }
}
