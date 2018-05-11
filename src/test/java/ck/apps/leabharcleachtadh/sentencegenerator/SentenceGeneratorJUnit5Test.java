package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.games.Practice;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static ck.apps.leabharcleachtadh.sentencegenerator.IrregularConj.array;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.params.provider.ObjectArrayArguments.create;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm.*;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject.*;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense.*;

class SentenceGeneratorJUnit5Test {

    static Stream<Arguments> inputsAndExpectations() {
        return Stream.of(
                create(STATEMENT_POSITIVE, Verb.BE, SING_1ST, PRESENT, "i am %s"),
                create(STATEMENT_POSITIVE, Verb.BE, SING_2ND, PRESENT, "you are %s"),
                create(STATEMENT_POSITIVE, Verb.BE, SING_3RD_MASC, PRESENT, "he is %s"),
                create(STATEMENT_POSITIVE, Verb.BE, SING_3RD_FEM, PRESENT, "she is %s"),
                create(STATEMENT_POSITIVE, Verb.BE, PLURAL_1ST, PRESENT, "we are %s"),
                create(STATEMENT_POSITIVE, Verb.BE, PLURAL_2ND, PRESENT, "you(pl) are %s"),
                create(STATEMENT_POSITIVE, Verb.BE, PLURAL_3RD, PRESENT, "they are %s"),

                create(STATEMENT_NEGATIVE, Verb.BE, SING_1ST, PRESENT, "i am not %s"),
                create(STATEMENT_NEGATIVE, Verb.BE, SING_2ND, PRESENT, "you are not %s"),
                create(STATEMENT_NEGATIVE, Verb.BE, SING_3RD_MASC, PRESENT, "he is not %s"),
                create(STATEMENT_NEGATIVE, Verb.BE, SING_3RD_FEM, PRESENT, "she is not %s"),
                create(STATEMENT_NEGATIVE, Verb.BE, PLURAL_1ST, PRESENT, "we are not %s"),
                create(STATEMENT_NEGATIVE, Verb.BE, PLURAL_2ND, PRESENT, "you(pl) are not %s"),
                create(STATEMENT_NEGATIVE, Verb.BE, PLURAL_3RD, PRESENT, "they are not %s"),

                create(QUESTION_VERB_POSITIVE, Verb.BE, SING_1ST, PRESENT, "am I %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.BE, SING_2ND, PRESENT, "are you %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.BE, SING_3RD_MASC, PRESENT, "is he %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.BE, SING_3RD_FEM, PRESENT, "is she %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.BE, PLURAL_1ST, PRESENT, "are we %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.BE, PLURAL_2ND, PRESENT, "are you(pl) %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.BE, PLURAL_3RD, PRESENT, "are they %s?"),

                create(QUESTION_VERB_NEGATIVE, Verb.BE, SING_1ST, PRESENT, "am i not %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.BE, SING_2ND, PRESENT, "are you not %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.BE, SING_3RD_MASC, PRESENT, "is he not %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.BE, SING_3RD_FEM, PRESENT, "is she not %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.BE, PLURAL_1ST, PRESENT, "are we not %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.BE, PLURAL_2ND, PRESENT, "are you(pl) not %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.BE, PLURAL_3RD, PRESENT, "are they not %s?"),

                create(STATEMENT_POSITIVE, Verb.BE, SING_1ST, PAST, "i was %s"),
                create(STATEMENT_POSITIVE, Verb.BE, SING_2ND, PAST, "you were %s"),
                create(STATEMENT_POSITIVE, Verb.BE, SING_3RD_MASC, PAST, "he was %s"),
                create(STATEMENT_POSITIVE, Verb.BE, SING_3RD_FEM, PAST, "she was %s"),
                create(STATEMENT_POSITIVE, Verb.BE, PLURAL_1ST, PAST, "we were %s"),
                create(STATEMENT_POSITIVE, Verb.BE, PLURAL_2ND, PAST, "you(pl) were %s"),
                create(STATEMENT_POSITIVE, Verb.BE, PLURAL_3RD, PAST, "they were %s"),

                create(STATEMENT_POSITIVE, Verb.MAKE, SING_2ND, PAST, "you made %s"),
                create(STATEMENT_POSITIVE, Verb.MAKE, SING_3RD_MASC, PRESENT, "he makes %s"),
                create(STATEMENT_POSITIVE, Verb.MAKE, SING_2ND, FUTURE, "you will make %s"),

                create(STATEMENT_NEGATIVE, Verb.MAKE, SING_2ND, PAST, "you did not make %s"),
                create(STATEMENT_NEGATIVE, Verb.MAKE, SING_3RD_MASC, PRESENT, "he does not make %s"),

                create(QUESTION_VERB_POSITIVE, Verb.MAKE, SING_2ND, PAST, "did you make %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.MAKE, SING_3RD_MASC, PRESENT, "does he make %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.MAKE, SING_3RD_FEM, FUTURE, "will she make %s?"),

                create(QUESTION_VERB_NEGATIVE, Verb.MAKE, SING_2ND, PAST, "did you not make %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.MAKE, SING_3RD_MASC, PRESENT, "does he not make %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.MAKE, SING_3RD_FEM, FUTURE, "will she not make %s?"),

                create(STATEMENT_POSITIVE, Verb.DO, SING_3RD_FEM, PRESENT, "she does %s"),
                create(STATEMENT_POSITIVE, Verb.DO, SING_3RD_FEM, PAST, "she did %s"),

                create(STATEMENT_NEGATIVE, Verb.DO, SING_3RD_FEM, PRESENT, "she does not do %s"),
                create(STATEMENT_NEGATIVE, Verb.DO, SING_3RD_FEM, PAST, "she did not do %s"),

                create(QUESTION_VERB_POSITIVE, Verb.DO, SING_2ND, PAST, "did you do %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.DO, SING_3RD_MASC, PRESENT, "does he do %s?"),
                create(QUESTION_VERB_POSITIVE, Verb.DO, SING_3RD_FEM, FUTURE, "will she do %s?"),

                create(QUESTION_VERB_NEGATIVE, Verb.DO, SING_2ND, PAST, "did you not do %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.DO, SING_3RD_MASC, PRESENT, "does he not do %s?"),
                create(QUESTION_VERB_NEGATIVE, Verb.DO, SING_3RD_FEM, FUTURE, "will she not do %s?")
//                ,

//                create(STATEMENT_POSITIVE, Verb.LOOK_AT, SING_3RD_FEM, PRESENT, "she look_ats %s"),
//                create(STATEMENT_POSITIVE, Verb.LOOK_AT, SING_3RD_FEM, PAST, "she looked at %s")
        );
    }

    @ParameterizedTest(name = "Form={0}, Verb={1}, Subject={2}, Tense={3}, Expected={4}")
    @MethodSource(names = "inputsAndExpectations")
    void generateTenses(SentenceForm form, Verb verb, Subject subject, Tense tense, String expected) {
        String object = "the thing";
        String sentence = Sentence.toSentence(VerbUsage.usage(form, verb, subject, tense, object));
        assertThat(sentence.toLowerCase(), is(equalTo(String.format(expected, object).toLowerCase())));
    }

    @Test
    void printPermutations1() {
        List<VerbUsage> permutations = Practice.permutations(
                Verb.values(),
                new SentenceForm[] { STATEMENT_POSITIVE/*, STATEMENT_NEGATIVE*/ },
                new Tense[] { Tense.PRESENT, Tense.PAST },
                Subject.values(),
                "it");
        printPermutations(
            Practice.groupPermutations(permutations),
            usage -> System.out.println("    " + Sentence.toSentence(usage)));
        System.out.printf("Num. permutations=%d\n", permutations.size());
    }

    @Test
    void questionFormPermutations() {
        Verb[] verbs = array(/*Verb.BE, Verb.DO,*/ Verb.SEE);
        Arrays.sort(verbs, Comparator.comparingInt(Enum::ordinal));
        Tense[] tenses = Tense.values();
        Arrays.sort(tenses, Comparator.comparingInt(Enum::ordinal));
        Subject[] subjects = Subject.values();
        Arrays.sort(subjects, Comparator.comparingInt(Enum::ordinal));
        printPermutations(
            Practice.groupPermutations(
                Practice.permutations(
                    verbs,
                        sentenceForms,
                    tenses,
                    subjects, "it")),
                    usage -> System.out.println("    " + Sentence.toSentence(usage)));
    }

    static SentenceForm[] sentenceForms = array(
            STATEMENT_POSITIVE,
            STATEMENT_NEGATIVE,
            QUESTION_VERB_POSITIVE,
            QUESTION_VERB_NEGATIVE
//                        QUESTION_HOW_POSITIVE,
//                        QUESTION_WHY_POSITIVE,
//                        QUESTION_WHEN_POSITIVE,
//                        QUESTION_WHERE_POSITIVE
    );


    //                  Question word   Preposition
    //                  N/A + verb_question_form
    //                      Did (subj) (VERB_DEAN)
    //                      Do (subj) (VERB_DEAN)
    //                      Will (subj) (VERB_DEAN)
    //                      Would (subj) (VERB_DEAN)
    //                  Why + verb_question_form
    //                      Why did (subj) (VERB_DEAN)
    //                      Why do (subj) (VERB_DEAN)
    //                      Why will (subj) (VERB_DEAN)
    //                      Why would (subj) (VERB_DEAN)
    //                  How + verb_question_form
    //                      How did (subj) (VERB_DEAN)
    //                      How do (subj) (VERB_DEAN)
    //                      How will (subj) (VERB_DEAN)
    //                      How would (subj) (VERB_DEAN)
    //                  When + verb_question_form
    //                      When did (subj) (VERB_DEAN)
    //                      When do (subj) (VERB_DEAN)
    //                      When will (subj) (VERB_DEAN)
    //                      When would (subj) (VERB_DEAN)
    //                  What + verb_question_form
    //                      What did (subj) (VERB_DEAN)
    //                      What do (subj) (VERB_DEAN)
    //                      What will (subj) (VERB_DEAN)
    //                      What would (subj) (VERB_DEAN)
    //                  Where + verb_question_form
    //                      Where did (subj) (VERB_DEAN)
    //                      Where do (subj) (VERB_DEAN)
    //                      Where will (subj) (VERB_DEAN)
    //                      Where would (subj) (VERB_DEAN)
    //                  How much + verb_question_form
    //                      How much did (subj) (VERB_DEAN)
    //                      How much do (subj) (VERB_DEAN)
    //                      How much will (subj) (VERB_DEAN)
    //                      How much would (subj) (VERB_DEAN)
    //                  How many + verb_question_form
    //                      How many did (subj) (VERB_DEAN)
    //                      How many do (subj) (VERB_DEAN)
    //                      How many will (subj) (VERB_DEAN)
    //                      How many would (subj) (VERB_DEAN)
    //                  Who + verb_statement_form
    //                      Who (statement) VERB_DEAN)
    //                      Who (VERB_DEAN
    //                      Who do
    // Who would i do it ?
    // Who would you do it ?

    // Why



    // Which (prep: to/for)

    // Where


    private void printPermutations(Map<Verb, Map<SentenceForm, Map<Tense, List<VerbUsage>>>> groupedPermutations, Consumer<VerbUsage> consumer) {
        groupedPermutations
            .forEach((verb, formGroup) -> {
                System.out.println("--- VERB: " + verb + " ---");
                formGroup
                    .forEach((form, tenseGroup) -> {
                        System.out.println("--- FORM: " + form + " ---");
                        tenseGroup
                            .forEach((tense, usages) -> {
                                System.out.printf("Verb/Tense[%s/%s] - [%s]:\n", verb, tense, form);
                                usages.forEach(consumer::accept);
                            });
                    });
            });
    }

}
