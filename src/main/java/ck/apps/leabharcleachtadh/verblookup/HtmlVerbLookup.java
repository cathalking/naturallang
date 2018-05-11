package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.games.Practice;
import ck.apps.leabharcleachtadh.sentencegenerator.*;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HtmlVerbLookup {

    public static final String REGEX_IRISH_PRONOUNS = Pattern.compile(".* (mé|tú|sé|muid|sibh|siad)").pattern();

    public static String parse(VerbUsage verbUsage) throws IOException {
        List<String> conjs = getConjugations(verbUsage.getVerb(), verbUsage.getTense(), verbUsage.getSentenceForm());
        String conj = getConjugation(conjs, verbUsage.getVerb(), verbUsage.getTense(), verbUsage.getSubject());
        if (SentenceForm.QUESTION_VERB_NEGATIVE.equals(verbUsage.getSentenceForm())) {
            conj = conj.replace("an ", "nach ");
            conj = conj.replace("ar ", "nar ");
        }
        return conj;
    }

    static List<String> getConjugations(Verb verb, Tense tense, SentenceForm sentenceForm) throws IOException {
        return getConjugations(getUrl(verb), tense, sentenceForm);
    }

    private static List<String> getConjugations(String url, Tense tense, SentenceForm sentenceForm) throws IOException {
        ConjugationFinder finder = sentenceForm.getConjugationFinder();
        Document doc = Jsoup.connect(url).get();
        Elements verbTense = doc.select("div#" + tense.getHtmlText());
        Elements subsections = verbTense.select("div.subsection");
        return IntStream.range(0, 2)
                .mapToObj(i -> {
                    Element subSection = subsections.get(i);
                    return subSection.select(finder.getQuery());
                })
                .flatMap(conjs -> conjs.stream().map(Element::text))
                .filter(conj -> finder.getPredicate().test(conj))
                .collect(Collectors.toList());
    }

    private static String getConjugation(List<String> conjs, Verb verb, Tense tense, Subject subject) throws IOException {
        int idxCorrection = 0;
        if (tense.equals(Tense.PRESENT) && verb.equals(Verb.BE)) {
            idxCorrection = 1;
        }
        int index = (subject.getPerson() + idxCorrection) + (subject.getMultiplicity() == 1 ? 0 : 3);
        return conjs.get(index - 1);
    }

    private static String getUrl(Verb verb) {
        String verbRootRaw = Practice.ENGLISH2IRISH.get(verb);
        String verbRoot = verbRootRaw
                .replaceAll("á", "a_x")
                .replaceAll("é", "e_x")
                .replaceAll("í", "i_x")
                .replaceAll("ó", "o_x")
                .replaceAll("ú", "u_x")
                ;
        return "http://www.focloir.ie/en/grammar/ei/" + verbRoot + "_verb";
    }

}
