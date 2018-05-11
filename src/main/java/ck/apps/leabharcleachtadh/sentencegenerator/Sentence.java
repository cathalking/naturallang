package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;

import static ck.apps.leabharcleachtadh.sentencegenerator.Verb.SENTENCE_FORMS;

public class Sentence {

    public static String toSentence(VerbUsage usage) {
        return toSentence(usage.getSentenceForm(), usage.getVerb(), usage.getSubject(), usage.getTense(), usage.getObject());
    }

    private static String toSentence(SentenceForm sentenceForm, Verb verb, Subject subject, Tense tense, String object) {
        BiFunction<String, String, String> formFn = getSentenceFormFn(verb, tense, subject, sentenceForm);
        String sentence = formFn.apply(subject.getText(), verb.getConjugation(sentenceForm, tense, subject));
        if (object != null && object.length() > 0) {
            sentence = sentence + " " + object;
        }
        if (sentenceForm.isQuestion()) {
            sentence = sentence + "?";
        }
        return StringUtils.capitalize(sentence);
    }

    private static BiFunction<String, String, String> getSentenceFormFn(Verb verb, Tense tense, Subject subject, SentenceForm sentenceForm) {
        BiFunction<String, String, String> formFn;
        if (SENTENCE_FORMS.containsKey(verb.toString() + sentenceForm.ordinal() + tense + subject)) {
            formFn = SENTENCE_FORMS.get(verb.toString() + sentenceForm.ordinal() + tense + subject);
        } else if (SENTENCE_FORMS.containsKey(verb.toString() + sentenceForm.ordinal() + tense)) {
            formFn = SENTENCE_FORMS.get(verb.toString() + sentenceForm.ordinal() + tense);
        } else if (SENTENCE_FORMS.containsKey("" + sentenceForm.ordinal() + tense + subject)) {
            formFn = SENTENCE_FORMS.get("" + sentenceForm.ordinal() + tense + subject);
        } else if (SENTENCE_FORMS.containsKey("" + sentenceForm.ordinal() + tense)) {
            formFn = SENTENCE_FORMS.get("" + sentenceForm.ordinal() + tense);
        } else {
            formFn = SENTENCE_FORMS.get("" + sentenceForm.ordinal());
        }
        return formFn;
    }
}
