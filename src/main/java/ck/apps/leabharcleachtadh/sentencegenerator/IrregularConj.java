package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IrregularConj {
    private IrregularConjKey key;
    private String conj;

    IrregularConj(String conj, SentenceForm form, Tense tense, Subject subject) {
        this.key = new IrregularConjKey(form, tense, subject);
        this.conj = conj;
    }

    static IrregularConj[] irregular(String conj, SentenceForm form, Tense tense, Subject... subjects) {
        return Stream.of(subjects)
            .map(subject -> new IrregularConj(conj, form, tense, subject))
            .collect(Collectors.toList())
            .toArray(new IrregularConj[0]);
    }

    static IrregularConj[] irregular(String conj, SentenceForm[] forms, Tense tense, Subject... subjects) {
        return Stream.of(subjects)
            .flatMap(subject -> Stream.of(forms).map(form -> new IrregularConj(conj, form, tense, subject)))
            .collect(Collectors.toList())
            .toArray(new IrregularConj[0]);
    }

    static IrregularConj[] concatArrays(IrregularConj[]... irregularConjArrays) {
        List<IrregularConj> list = new ArrayList<>();
        Stream.of(irregularConjArrays)
            .forEach(irregularConjs -> list.addAll(Arrays.asList(irregularConjs)));
        return list.toArray(new IrregularConj[0]);
    }

    static <T> T[] array(T... ts) {
        return ts;
    }

    IrregularConjKey getKey() {
        return key;
    }

    String getConj() {
        return conj;
    }
}
