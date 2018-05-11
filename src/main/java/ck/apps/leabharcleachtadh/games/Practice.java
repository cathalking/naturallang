package ck.apps.leabharcleachtadh.games;

import ck.apps.leabharcleachtadh.audio.AudioFilePlayer;
import ck.apps.leabharcleachtadh.games.domain.UserInput;
import ck.apps.leabharcleachtadh.sentencegenerator.*;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import ck.apps.leabharcleachtadh.verblookup.HtmlVerbLookup;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Practice {

    public static Map<Verb, String> ENGLISH2IRISH = ImmutableMap.<Verb,String>builder()
            .put(Verb.BE, "bí")
            .put(Verb.GO, "téigh")
            .put(Verb.COME, "tar")
            .put(Verb.BEGIN, "tosaigh")
            .put(Verb.DO, "déan")
            .put(Verb.FIND, "aimsigh")
            .put(Verb.GET, "faigh")
            .put(Verb.GIVE, "tabhair")
            .put(Verb.LEAVE, "imigh")
            .put(Verb.MAKE, "déan")
            .put(Verb.PUT, "cuir")
            .put(Verb.FILL, "líon")
            .put(Verb.GATHER, "bailigh")
            .put(Verb.SEE, "feic")
            .put(Verb.LOOK, "amharc")
            .build();

    static int promptColour = 36;

    public static void main(String[] args) {
        if (args.length > 0) {
            promptColour = Integer.parseInt(args[0]);
        }

        Practice practice = new Practice();
        SentenceForm[] forms = new SentenceForm[] { SentenceForm.STATEMENT_POSITIVE, SentenceForm.STATEMENT_NEGATIVE, SentenceForm.QUESTION_VERB_POSITIVE, SentenceForm.QUESTION_VERB_NEGATIVE };
//        Verb[] verbs = Verb.values();
        // Verb[] verbs = new Verb[] { Verb.DO };
        Verb[] verbs = ENGLISH2IRISH.keySet().toArray(new Verb[0]);
        List<VerbUsage> permutations = permutations(verbs, forms, Tense.values(), Subject.values(), "(the thing)");
        practice.runSession(() -> getNextRandom(permutations));
    }

    void runSession(Supplier<VerbUsage> supplier) {
        Scanner scanner = new Scanner(System.in);
        List<VerbUsage> skipped = new ArrayList<>();
        AudioFilePlayer audioPlayer = new AudioFilePlayer();

        System.out.printf("Commands: Next/Skip 'n', Progress Summary 'p', Quit 'q'\n");

        Map<VerbUsage, String> correct = new HashMap<>();
        Map<VerbUsage, String> incorrect = new HashMap<>();
        while(true) {
            VerbUsage verbUsage = supplier.get();
            System.out.printf(colour("%s : \n", promptColour), Sentence.toSentence(verbUsage));
//            System.out.printf("%s : \n", translate(usage) + " - " + usage);

            String response = scanner.nextLine();
            if (response.length() == 1) {
                UserInput userInput = UserInput.from(response);
                boolean keepGoing = true;
                switch (userInput) {
                    case SKIP: {
                        skipped.add(verbUsage);
                        System.out.printf("Skipping...\n");
                        break;
                    }
                    case SHOW_SUMMARY: {
                        showSummary(skipped, correct, incorrect);
                        break;
                    }
                    case QUIT: {
                        System.out.printf("Quitting...\n");
                        showSummary(skipped, correct, incorrect);
                        keepGoing = false;
                    }
                    default:
                        break;
                }
                if (!keepGoing) {
                    return;
                }
            } else {
                String officialTranslation = translate(verbUsage);
//                String officialTranslationRaw = usage.toSentence();
//                String officialTranslation = officialTranslationRaw.replaceAll(" \\(the thing\\)", "");
                if (officialTranslation.equalsIgnoreCase(response)) {
                    correct.put(verbUsage, response);
                } else {
                    incorrect.put(verbUsage, response);
                    System.out.printf((char)27 + "[31m"+ "X >>> %s\n", officialTranslation + (char)27 + "[0m");
                }
//                 audioPlayer.playSentence(officialTranslation, SpeechLookup.Speed.SLOWER);
            }
        }

    }

    private static VerbUsage getNextRandom(List<VerbUsage> permutations) {
        int size = permutations.size();
        int index = (int)(Math.random() * size);
        return permutations.get(index);
    }

    private void showSummary(List<VerbUsage> skipped, Map<VerbUsage, String> correct, Map<VerbUsage, String> incorrect) {
        System.out.printf("Summary: Correct: " + colour("%d", 32) + " Incorrect: " + colour("%d", 31) + " Skipped %d\n", correct.size(), incorrect.size(), skipped.size());
        System.out.printf("\n%d skipped\n", skipped.size());
        final int minWidth1 = getMaxSentenceLength(skipped);
        skipped.forEach(usage -> {
            System.out.printf("- %-" + minWidth1 + "s => ??? ??? ??? ???\n", Sentence.toSentence(usage));
        });
        int maxSentenceLength = getMaxSentenceLength(correct.keySet());
        final int minWidth2 = maxSentenceLength > 0 ? maxSentenceLength : 1;
        System.out.printf(colour("\n%d translated correctly \n", 32), correct.size());
        correct.forEach((usage, userInput) -> {
            System.out.printf("+ %-" + minWidth2 +"s => %s\n", Sentence.toSentence(usage), userInput);
        });
        System.out.printf(colour("\n%d translated incorrectly \n", 31), incorrect.size());
        incorrect.forEach((usage, userInput) -> {
            String officialTranslation = translate(usage);
            System.out.printf("+ %-" + minWidth2 +"s => %s (%s)\n", Sentence.toSentence(usage), officialTranslation, userInput);
        });
    }

    /*
    +~~~~~~+~~~~~~+~~~~~~~~~~~+
    |  fg  |  bg  |  color    |
    +~~~~~~+~~~~~~+~~~~~~~~~~~+
    |  30  |  40  |  black    |
    |  31  |  41  |  red      |
    |  32  |  42  |  green    |
    |  33  |  43  |  yellow   |
    |  34  |  44  |  blue     |
    |  35  |  45  |  magenta  |
    |  36  |  46  |  cyan     |
    |  37  |  47  |  white    |
    |  39  |  49  |  default  |
    +~~~~~~+~~~~~~+~~~~~~~~~~~+
    */

    private String colour(String s, int colour) {
        return (char)27 + "["+ colour +  "m" + s + (char)27 + "[0m";
    }

    private String translate(VerbUsage verbUsage) {
        String irishVerbConj = "?";
        try {
            irishVerbConj = HtmlVerbLookup.parse(verbUsage);
            if (verbUsage.getSubject().equals(Subject.SING_3RD_FEM)) {
                irishVerbConj = irishVerbConj.replaceAll(" sé", " sí");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.capitalize(irishVerbConj);
    }

    private int getMaxSentenceLength(Iterable<VerbUsage> usages) {
        int maxSkippedLength = 0;
        for (VerbUsage u : usages) {
            int sentenceLen = Sentence.toSentence(u).length();
            if (sentenceLen > maxSkippedLength) {
                maxSkippedLength = sentenceLen;
            }
        }
        return maxSkippedLength;
    }

    public static Map<Verb, Map<SentenceForm, Map<Tense, List<VerbUsage>>>> groupPermutations(List<VerbUsage> verbUsages) {
        return verbUsages.stream()
                .collect(Collectors.groupingBy(VerbUsage::getVerb,
                    Collectors.groupingBy(VerbUsage::getSentenceForm,
                        Collectors.groupingBy(VerbUsage::getTense))));
    }

    public static List<VerbUsage> permutations(Verb[] verbs, SentenceForm[] forms, Tense[] tenses, Subject[] subjects, String object) {
        return
            Stream.of(verbs).flatMap(verb ->
                Stream.of(forms).flatMap(form ->
                    Stream.of(tenses).flatMap(tense ->
                        Stream.of(subjects).map(subject ->
                            VerbUsage.usage(form, verb, subject, tense, object)))))
                .collect(Collectors.toList());
    }
}
