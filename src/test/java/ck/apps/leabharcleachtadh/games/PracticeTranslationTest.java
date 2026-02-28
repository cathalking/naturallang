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
        PracticeEngine engine = new PracticeEngine(BuNaMoVerbLookup.PronounMode.STRICT);
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_POSITIVE, Verb.DO, Subject.SING_3RD_FEM, Tense.PAST, "");
        String result = engine.translate(usage);
        assertThat(result, is("Rinne sí"));
    }

    @Test
    void accepts_response_without_pronoun_for_sg1() throws Exception {
        PracticeEngine engine = new PracticeEngine(BuNaMoVerbLookup.PronounMode.STRICT);
        VerbUsage usage = VerbUsage.usage(SentenceForm.STATEMENT_NEGATIVE, Verb.SEE, Subject.SING_1ST, Tense.PRESENT, "");
        String official = engine.translate(usage);
        boolean matches = engine.isCorrect(usage, "Ní fheicim");
        assertThat(matches, is(true));
    }
}
