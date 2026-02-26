package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.Verb;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Disabled("Requires live focloir.ie network access")
class HtmlVerbLookupTest {

    @Test
    void parse_statements_positive() throws IOException {
        List<String> verbStatements =
                HtmlVerbLookup.getConjugations(Verb.MAKE, Tense.PAST, SentenceForm.STATEMENT_POSITIVE);

        assertThat(verbStatements,
                is(equalTo(
                Arrays.asList(
                    "rinne mé",
                        "rinne tú",
                        "rinne sé",
                        "rinne muid",
                        "rinne sibh",
                        "rinne siad"
                ))));
    }
    @Test
    void parse_statements_negative() throws IOException {
        List<String> verbStatements =
                HtmlVerbLookup.getConjugations(Verb.MAKE, Tense.PAST, SentenceForm.STATEMENT_NEGATIVE);

        assertThat(verbStatements,
                is(equalTo(
                Arrays.asList(
                        "ní dhearna mé",
                        "ní dhearna tú",
                        "ní dhearna sé",
                        "ní dhearna muid",
                        "ní dhearna sibh",
                        "ní dhearna siad"
                ))));
    }

    @Test
    void parse_questions_positive() throws IOException {
        List<String> verbStatements =
                HtmlVerbLookup.getConjugations(Verb.MAKE, Tense.PAST, SentenceForm.QUESTION_VERB_POSITIVE);

        assertThat(verbStatements,
                is(equalTo(
                        Arrays.asList(
                                "an ndearna mé?",
                                "an ndearna tú?",
                                "an ndearna sé?",
                                "an ndearna muid?",
                                "an ndearna sibh?",
                                "an ndearna siad?"
                        ))));
    }

}
