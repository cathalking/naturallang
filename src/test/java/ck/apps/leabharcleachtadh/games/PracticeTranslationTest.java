package ck.apps.leabharcleachtadh.games;

import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.verblookup.BuNaMoVerbLookup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PracticeTranslationTest {
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
    void translate_replaces_se_with_si_for_feminine() throws Exception {
        Practice practice = new Practice();
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, Verb.DO, Subject.SING_3RD_FEM, Tense.PAST, "");
        Method translate = Practice.class.getDeclaredMethod("translate", VerbUsage.class, BuNaMoVerbLookup.PronounMode.class);
        translate.setAccessible(true);
        String result = (String) translate.invoke(practice, usage, BuNaMoVerbLookup.PronounMode.STRICT);
        assertThat(result, is("Rinne sí"));
    }

    @Test
    void accepts_response_without_pronoun_for_sg1() throws Exception {
        Practice practice = new Practice();
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_NEGATIVE, Verb.SEE, Subject.SING_1ST, Tense.PRESENT, "");
        Method translate = Practice.class.getDeclaredMethod("translate", VerbUsage.class, BuNaMoVerbLookup.PronounMode.class);
        translate.setAccessible(true);
        String official = (String) translate.invoke(practice, usage, BuNaMoVerbLookup.PronounMode.STRICT);

        Method isCorrectIrish = Practice.class.getDeclaredMethod("isCorrectIrish", String.class, String.class, Subject.class, BuNaMoVerbLookup.PronounMode.class);
        isCorrectIrish.setAccessible(true);
        boolean matches = (boolean) isCorrectIrish.invoke(practice, official, "Ní fheicim", Subject.SING_1ST, BuNaMoVerbLookup.PronounMode.STRICT);
        assertThat(matches, is(true));
    }
}
