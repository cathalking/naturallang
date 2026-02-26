package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm.*;
import static ck.apps.leabharcleachtadh.verblookup.ConjugationFinder.STMT_POS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled("Requires live focloir.ie network access")
class HtmlVerbLookupParameterizedTest {

    static Stream<Arguments> inputsAndExpectations() {
        return Stream.of(
                Arguments.of(Verb.MAKE, Tense.PAST, STMT_POS, Arrays.asList(
                        "rinne mé",
                            "rinne tú",
                            "rinne sé",
                            "rinne muid",
                            "rinne sibh",
                            "rinne siad")),
                Arguments.of(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_POSITIVE, Arrays.asList(
                        "an ndearna mé?",
                            "an ndearna tú?",
                            "an ndearna sé?",
                            "an ndearna muid?",
                            "an ndearna sibh?",
                            "an ndearna siad?")),
                Arguments.of(Verb.MAKE, Tense.PAST, SentenceForm.STATEMENT_NEGATIVE, Arrays.asList(
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
    @MethodSource("inputsAndExpectations")
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
                Arguments.of(Verb.GATHER, Tense.CONDITIONAL, QUESTION_VERB_POSITIVE, Subject.PLURAL_3RD, "an mbaileodh siad?"),
                Arguments.of(Verb.LOOK, Tense.CONDITIONAL, QUESTION_VERB_POSITIVE, Subject.SING_2ND, "an amharcfá?"),
                Arguments.of(Verb.FILL, Tense.PRESENT, QUESTION_VERB_POSITIVE, Subject.PLURAL_1ST, "an líonann muid?"),
                Arguments.of(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.SING_1ST, "tá mé"),
                Arguments.of(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.PLURAL_1ST, "tá muid"),
                Arguments.of(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.SING_2ND, "tá tú"),
                Arguments.of(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.PLURAL_2ND, "tá sibh"),
                Arguments.of(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.SING_3RD_MASC, "tá sé"),
                Arguments.of(Verb.BE, Tense.PRESENT, STATEMENT_POSITIVE, Subject.PLURAL_3RD, "tá siad"),
                Arguments.of(Verb.PUT, Tense.PAST, QUESTION_VERB_NEGATIVE, Subject.SING_3RD_MASC, "nar chuir sé?"),
                Arguments.of(Verb.MAKE, Tense.PAST, STATEMENT_POSITIVE, Subject.SING_1ST, "rinne mé"),
                Arguments.of(Verb.MAKE, Tense.PAST, STATEMENT_POSITIVE, Subject.SING_2ND, "rinne tú"),
                Arguments.of(Verb.MAKE, Tense.PAST, STATEMENT_NEGATIVE, Subject.PLURAL_3RD, "ní dhearna siad"),
                Arguments.of(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_POSITIVE, Subject.SING_3RD_MASC, "an ndearna sé?"),
                Arguments.of(Verb.FIND, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_1ST, "ní aimsíonn muid"),
                Arguments.of(Verb.FIND, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_2ND, "ní aimsíonn sibh"),
                Arguments.of(Verb.FIND, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_3RD, "ní aimsíonn siad"),
                Arguments.of(Verb.MAKE, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_1ST, "ní dhéanann muid"),
                Arguments.of(Verb.MAKE, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_2ND, "ní dhéanann sibh"),
                Arguments.of(Verb.MAKE, Tense.PRESENT, SentenceForm.STATEMENT_NEGATIVE, Subject.PLURAL_3RD, "ní dhéanann siad"),
                Arguments.of(Verb.BE, Tense.PRESENT, SentenceForm.STATEMENT_POSITIVE, Subject.SING_1ST, "tá mé"),
                Arguments.of(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_NEGATIVE, Subject.SING_3RD_MASC, "nach ndearna sé?"),
                Arguments.of(Verb.LEAVE, Tense.PRESENT, SentenceForm.QUESTION_VERB_NEGATIVE, Subject.SING_3RD_MASC, "nach imíonn sé?"),
                Arguments.of(Verb.DO, Tense.PRESENT, SentenceForm.QUESTION_VERB_POSITIVE, Subject.PLURAL_3RD, "an ndéanann siad?"),
                Arguments.of(Verb.FIND, Tense.PAST, SentenceForm.STATEMENT_POSITIVE, Subject.PLURAL_3RD, "d'aimsigh siad")
        );
    }
    @ParameterizedTest
    @MethodSource("inputsAndExpectations2")
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
