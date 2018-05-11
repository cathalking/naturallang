package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm.*;
import static ck.apps.leabharcleachtadh.verblookup.ConjugationFinder.STMT_POS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.jupiter.params.provider.ObjectArrayArguments.create;

class HtmlVerbLookupParameterizedTest {

    static Stream<Arguments> inputsAndExpectations() {
        return Stream.of(
                create(Verb.MAKE, Tense.PAST, STMT_POS, Arrays.asList(
                        "rinne mé",
                            "rinne tú",
                            "rinne sé",
                            "rinne muid",
                            "rinne sibh",
                            "rinne siad")),
                create(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_POSITIVE, Arrays.asList(
                        "an ndearna mé?",
                            "an ndearna tú?",
                            "an ndearna sé?",
                            "an ndearna muid?",
                            "an ndearna sibh?",
                            "an ndearna siad?")),
                create(Verb.MAKE, Tense.PAST, SentenceForm.STATEMENT_NEGATIVE, Arrays.asList(
                        "ní dhearna mé",
                            "ní dhearna tú",
                            "ní dhearna sé",
                            "ní dhearna muid",
                            "ní dhearna sibh",
                            "ní dhearna siad"
                ))
        );
    }
    @ParameterizedTest
    @MethodSource(names = "inputsAndExpectations")
    @Test
    void _1findVerbTenseForm(Verb verb, Tense tense, SentenceForm sentenceForm, List<String> expectedConjugations) {
        try {
            List<String> verbStatements = HtmlVerbLookup.getConjugations(verb, tense, sentenceForm);
            assertThat(verbStatements, is(equalTo(expectedConjugations)));
        } catch (IOException e) {
            fail();
        }
    }

    static Stream<Arguments> inputsAndExpectations2() {
        return Stream.of(
                create(Verb.GATHER, Tense.CONDITIONAL, QUESTION_VERB_POSITIVE, Subject.PLURAL_3RD, "an mbaileodh siad?"),
                create(Verb.LOOK, Tense.CONDITIONAL, QUESTION_VERB_POSITIVE, Subject.SING_2ND, "an amharcfá?"),
                create(Verb.FILL, Tense.PRESENT, QUESTION_VERB_POSITIVE, Subject.PLURAL_1ST, "an líonann muid?"),
                create(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.SING_1ST, "tá mé"),
                create(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.PLURAL_1ST, "tá muid"),
                create(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.SING_2ND, "tá tú"),
                create(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.PLURAL_2ND, "tá sibh"),
                create(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.SING_3RD_MASC, "tá sé"),
                create(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.PLURAL_3RD, "tá siad"),
                create(Verb.PUT, Tense.PAST, QUESTION_VERB_NEGATIVE, Subject.SING_3RD_MASC, "nar chuir sé?"),
                create(Verb.MAKE, Tense.PAST, STATEMENT_POSITIVE, Subject.SING_1ST, "rinne mé"),
                create(Verb.MAKE, Tense.PAST, STATEMENT_POSITIVE, Subject.SING_2ND, "rinne tú"),
                create(Verb.MAKE, Tense.PAST, STATEMENT_NEGATIVE, Subject.PLURAL_3RD, "ní dhearna siad"),
                create(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_POSITIVE, Subject.SING_3RD_MASC, "an ndearna sé?"),
                create(Verb.FIND, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_1ST, "ní aimsíonn muid"),
                create(Verb.FIND, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_2ND, "ní aimsíonn sibh"),
                create(Verb.FIND, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_3RD, "ní aimsíonn siad"),
                create(Verb.MAKE, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_1ST, "ní dhéanann muid"),
                create(Verb.MAKE, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_2ND, "ní dhéanann sibh"),
                create(Verb.MAKE, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_3RD, "ní dhéanann siad"),
                create(Verb.BE, Tense.PRESENT, SentenceForm.STATEMENT_POSITIVE, Subject.SING_1ST, "tá mé"),
                create(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_NEGATIVE, Subject.SING_3RD_MASC, "nach ndearna sé?"),
                create(Verb.LEAVE, Tense.PRESENT, SentenceForm.QUESTION_VERB_NEGATIVE, Subject.SING_3RD_MASC, "nach imíonn sé?"),
                create(Verb.DO, Tense.PRESENT, SentenceForm.QUESTION_VERB_POSITIVE, Subject.PLURAL_3RD, "an ndéanann siad?"),
                create(Verb.FIND, Tense.PAST, SentenceForm.STATEMENT_POSITIVE, Subject.PLURAL_3RD, "d'aimsigh siad")
        );
    }
    @ParameterizedTest
    @MethodSource(names = "inputsAndExpectations2")
    @Test
    void _2findVerbTenseFormPerson(Verb verb, Tense tense, SentenceForm form, Subject subject, String expectedConjugation) {
        try {
            VerbUsage verbUsage = VerbUsage.usage(form, verb, subject, tense, "");

            String verbConj = HtmlVerbLookup.parse(verbUsage);
            assertThat(verbConj, is(equalTo(expectedConjugation)));
        } catch (IOException e) {
            fail();
        }
    }


}