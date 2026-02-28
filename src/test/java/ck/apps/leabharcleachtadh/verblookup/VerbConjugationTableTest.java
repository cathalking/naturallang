package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class VerbConjugationTableTest {
    private static String previousVerbDir;

    @BeforeAll
    static void setUp() {
        previousVerbDir = System.getProperty("bunamo.verb.dir");
        System.setProperty("bunamo.verb.dir", Paths.get("vendor", "BuNaMo", "verb").toString());
    }

    @AfterAll
    static void tearDown() {
        if (previousVerbDir == null) {
            System.clearProperty("bunamo.verb.dir");
        } else {
            System.setProperty("bunamo.verb.dir", previousVerbDir);
        }
    }

    @ParameterizedTest(name = "irregular past question: {0}")
    @MethodSource("irregularPastQuestionCases")
    void irregular_past_question_positive_sg1(Verb verb, String expected) throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.QUESTION_VERB_POSITIVE, verb, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is(expected));
    }

    @ParameterizedTest(name = "irregular past negative: {0}")
    @MethodSource("irregularPastNegativeCases")
    void irregular_past_negative_statement_sg1(Verb verb, String expected) throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_NEGATIVE, verb, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is(expected));
    }

    @ParameterizedTest(name = "regular past question: {0}")
    @MethodSource("regularPastQuestionCases")
    void regular_past_question_positive_sg1(Verb verb, String expected) throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.QUESTION_VERB_POSITIVE, verb, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is(expected));
    }

    @ParameterizedTest(name = "regular past negative: {0}")
    @MethodSource("regularPastNegativeCases")
    void regular_past_negative_statement_sg1(Verb verb, String expected) throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_NEGATIVE, verb, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is(expected));
    }

    @ParameterizedTest(name = "strict synthetic omission: {0}")
    @MethodSource("strictSyntheticOmissionCases")
    void strict_synthetic_forms_omit_pronouns(Verb verb, Subject subject, String expected) throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, verb, subject, Tense.PRESENT, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is(expected));
    }

    @ParameterizedTest(name = "strict base forms keep pronoun: {0}")
    @MethodSource("strictBaseFormCases")
    void strict_base_forms_keep_pronouns(Verb verb, Subject subject, String expected) throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, verb, subject, Tense.PRESENT, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is(expected));
    }

    private static Stream<Arguments> irregularPastQuestionCases() {
        return Stream.of(
                Arguments.of(Verb.BE, "an raibh mé?"),
                Arguments.of(Verb.DO, "an ndearna mé?"),
                Arguments.of(Verb.GET, "an bhfuair mé?"),
                Arguments.of(Verb.SEE, "an bhfaca mé?"),
                Arguments.of(Verb.GO, "an ndeachaigh mé?")
        );
    }

    private static Stream<Arguments> irregularPastNegativeCases() {
        return Stream.of(
                Arguments.of(Verb.BE, "ní raibh mé"),
                Arguments.of(Verb.DO, "ní dhearna mé"),
                Arguments.of(Verb.GET, "ní fhuair mé"),
                Arguments.of(Verb.SEE, "ní fhaca mé"),
                Arguments.of(Verb.GO, "ní dheachaigh mé")
        );
    }

    private static Stream<Arguments> regularPastQuestionCases() {
        return Stream.of(
                Arguments.of(Verb.FILL, "ar líon mé?"),
                Arguments.of(Verb.PUT, "ar chuir mé?"),
                Arguments.of(Verb.GATHER, "ar bhailigh mé?")
        );
    }

    private static Stream<Arguments> regularPastNegativeCases() {
        return Stream.of(
                Arguments.of(Verb.FILL, "níor líon mé"),
                Arguments.of(Verb.PUT, "níor chuir mé"),
                Arguments.of(Verb.GATHER, "níor bhailigh mé")
        );
    }

    private static Stream<Arguments> strictSyntheticOmissionCases() {
        return Stream.of(
                Arguments.of(Verb.DO, Subject.SING_1ST, "déanaim"),
                Arguments.of(Verb.GET, Subject.SING_1ST, "faighim")
        );
    }

    private static Stream<Arguments> strictBaseFormCases() {
        return Stream.of(
                Arguments.of(Verb.DO, Subject.SING_3RD_MASC, "déanann sé"),
                Arguments.of(Verb.DO, Subject.SING_3RD_FEM, "déanann sí"),
                Arguments.of(Verb.DO, Subject.PLURAL_2ND, "déanann sibh"),
                Arguments.of(Verb.DO, Subject.PLURAL_1ST, "déanann muid")
        );
    }
}
