package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm.QUESTION_VERB_POSITIVE;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm.STATEMENT_POSITIVE;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject.*;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense.*;

@RunWith(JUnitParamsRunner.class)
public class SentenceGeneratorTest {

    Object[] inputsAndExpectations() {
        return $(
            $(QUESTION_VERB_POSITIVE, Verb.MAKE, SING_2ND, PAST, "the object",
                    "Did you make the object?"),
            $(QUESTION_VERB_POSITIVE, Verb.MAKE, SING_3RD_MASC, PRESENT, "the object",
                    "Does he make the object?"),
            $(QUESTION_VERB_POSITIVE, Verb.MAKE, SING_3RD_FEM, FUTURE, "the object",
                    "Will she make the object?"),

            $(STATEMENT_POSITIVE, Verb.DO, SING_3RD_FEM, PRESENT, "the object",
                    "She does the object"),
            $(STATEMENT_POSITIVE, Verb.DO, SING_3RD_FEM, PRESENT, "the object",
                    "She does the object"))

                ;
    }

    @Parameters(method = "inputsAndExpectations")
    @Test
    public void generateTenses(SentenceForm form, Verb verb, Subject subject, Tense tense, String object, String expected) {
        VerbUsage usage = VerbUsage.usage(form, verb, subject, tense, object);
        String sentence = Sentence.toSentence(usage);
        assertThat(sentence, is(equalTo(expected)));
    }

    @DisplayName("Build Permutations")
    @Test
    public void buildPermutations() {
    }

    static SentenceForm[] forms = new SentenceForm[] { SentenceForm.STATEMENT_POSITIVE, SentenceForm.QUESTION_VERB_POSITIVE};
    @Test
    public void showPermutations() {
        Stream.of(Verb.values())
            .forEach(verb -> {
                System.out.println("--- VERB: " + verb + " ---");
                Stream.of(forms)
                    .forEach(form -> {
                        System.out.println("--- FORM: " + form + " ---");
                        Stream.of(Tense.values())
                                .forEach(tense -> {
                                    System.out.println("--- TENSE: " + tense + " ---");
                                    Stream.of(Subject.values())
                                            .forEach(subject -> {
                                                VerbUsage usage = VerbUsage.usage(form, verb, subject, tense, "the object");
                                                String string = Sentence.toSentence(usage);
                                                System.out.println(string);
                                            });
                                });
                    });
                System.out.println("------ ------ ------");
        });
    }

    private List<String> mapArrayToList(Object[] expected, Function<Object, String> mapper) {
        return Arrays.stream(expected).map(mapper).collect(Collectors.toList());
    }

}
