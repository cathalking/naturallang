package ck.apps.leabharcleachtadh.games;

import ck.apps.leabharcleachtadh.sentencegenerator.Sentence;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.verblookup.BuNaMoVerbLookup;
import ck.apps.leabharcleachtadh.verblookup.HtmlVerbLookup;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class PracticeEngine {
    private final BuNaMoVerbLookup.PronounMode pronounMode;

    public PracticeEngine(BuNaMoVerbLookup.PronounMode pronounMode) {
        this.pronounMode = pronounMode;
    }

    public String prompt(VerbUsage usage) {
        return Sentence.toSentence(usage);
    }

    public String translate(VerbUsage verbUsage) {
        String irishVerbConj = "?";
        try {
            try {
                irishVerbConj = BuNaMoVerbLookup.parse(verbUsage, pronounMode);
            } catch (IOException e) {
                irishVerbConj = HtmlVerbLookup.parse(verbUsage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.capitalize(irishVerbConj);
    }

    public boolean isCorrect(VerbUsage usage, String response) {
        String official = translate(usage);
        return isCorrectIrish(official, response, usage.getSubject());
    }

    private boolean isCorrectIrish(String official, String response, Subject subject) {
        String normalizedOfficial = normalizeIrish(official);
        String normalizedResponse = normalizeIrish(response);
        if (normalizedOfficial.equals(normalizedResponse)) {
            return true;
        }
        if (pronounMode == BuNaMoVerbLookup.PronounMode.STRICT) {
            return false;
        }
        String officialNoPronoun = stripTrailingPronoun(normalizedOfficial, subject);
        String responseNoPronoun = stripTrailingPronoun(normalizedResponse, subject);
        return officialNoPronoun.equals(normalizedResponse)
                || normalizedOfficial.equals(responseNoPronoun)
                || officialNoPronoun.equals(responseNoPronoun);
    }

    private String normalizeIrish(String value) {
        return value
                .replaceAll("\\?", "")
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private String stripTrailingPronoun(String value, Subject subject) {
        String pronoun = switch (subject) {
            case SING_1ST -> "mé";
            case SING_2ND -> "tú";
            case SING_3RD_MASC -> "sé";
            case SING_3RD_FEM -> "sí";
            case PLURAL_1ST -> "muid";
            case PLURAL_2ND -> "sibh";
            case PLURAL_3RD -> "siad";
        };
        if (value.endsWith(" " + pronoun)) {
            return value.substring(0, value.length() - pronoun.length() - 1);
        }
        return value;
    }
}
