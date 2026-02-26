package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class BuNaMoVerbLookupTest {
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

    @Test
    void parse_dean_past_statement_positive_sg1() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, Verb.DO, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("rinne mé"));
    }

    @Test
    void parse_dean_past_statement_positive_pl1() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, Verb.DO, Subject.PLURAL_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("rinneamar muid"));
    }

    @Test
    void parse_dean_past_statement_positive_sg3_feminine_subject() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, Verb.DO, Subject.SING_3RD_FEM, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("rinne sé"));
    }

    @Test
    void parse_dean_past_statement_negative_sg1() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_NEGATIVE, Verb.DO, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("níor dhearna mé"));
    }

    @Test
    void parse_dean_past_question_positive_sg1() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.QUESTION_VERB_POSITIVE, Verb.DO, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("an ndearna mé?"));
    }

    @Test
    void parse_faigh_present_question_positive_sg1() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.QUESTION_VERB_POSITIVE, Verb.GET, Subject.SING_1ST, Tense.PRESENT, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("an bhfaighim mé?"));
    }

    @Test
    void parse_teigh_past_question_negative_sg1() throws IOException {
        VerbUsage usage = VerbUsage.usage(SentenceForm.QUESTION_VERB_NEGATIVE, Verb.GO, Subject.SING_1ST, Tense.PAST, "");
        assertThat(BuNaMoVerbLookup.parse(usage), is("nár dheachaigh mé?"));
    }
}
