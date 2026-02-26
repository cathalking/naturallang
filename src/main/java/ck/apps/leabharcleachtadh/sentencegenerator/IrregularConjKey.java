package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;

record IrregularConjKey(String form, Tense tense, Subject subject) {
    IrregularConjKey(SentenceForm form, Tense tense, Subject subject) {
        this(form.toString(), tense, subject);
    }
}
