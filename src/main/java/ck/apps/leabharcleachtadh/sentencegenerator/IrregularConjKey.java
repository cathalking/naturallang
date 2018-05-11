package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;

import java.util.Objects;

class IrregularConjKey {
    private String form;
    private Tense tense;
    private Subject subject;

    public IrregularConjKey(SentenceForm form, Tense tense, Subject subject) {
        this.form = form.toString();
        this.tense = tense;
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(form, tense, subject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IrregularConjKey that = (IrregularConjKey) o;
        return Objects.equals(form, that.form) &&
                tense == that.tense &&
                subject == that.subject;
    }
}
