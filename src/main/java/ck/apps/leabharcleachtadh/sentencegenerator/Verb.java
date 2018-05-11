package ck.apps.leabharcleachtadh.sentencegenerator;

import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ck.apps.leabharcleachtadh.sentencegenerator.IrregularConj.array;
import static ck.apps.leabharcleachtadh.sentencegenerator.IrregularConj.concatArrays;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm.*;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject.SING_3RD_FEM;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject.SING_3RD_MASC;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense.PAST;
import static ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense.PRESENT;
import static ck.apps.leabharcleachtadh.sentencegenerator.IrregularConj.irregular;

public enum Verb {
    BE(concatArrays(
      irregular("am", array(STATEMENT_POSITIVE, STATEMENT_NEGATIVE, QUESTION_VERB_POSITIVE, QUESTION_VERB_NEGATIVE), PRESENT, Subject.SING_1ST),
      irregular("are", array(STATEMENT_POSITIVE, STATEMENT_NEGATIVE, QUESTION_VERB_POSITIVE, QUESTION_VERB_NEGATIVE), PRESENT, Subject.SING_2ND, Subject.PLURAL_1ST, Subject.PLURAL_2ND, Subject.PLURAL_3RD),
      irregular("is", array(STATEMENT_POSITIVE, STATEMENT_NEGATIVE, QUESTION_VERB_POSITIVE, QUESTION_VERB_NEGATIVE), PRESENT, Subject.SING_3RD_MASC, Subject.SING_3RD_FEM),
      irregular("was", array(STATEMENT_POSITIVE, STATEMENT_NEGATIVE, QUESTION_VERB_POSITIVE, QUESTION_VERB_NEGATIVE), PAST, Subject.SING_1ST, Subject.SING_3RD_MASC, Subject.SING_3RD_FEM),
      irregular("were", array(STATEMENT_POSITIVE, STATEMENT_NEGATIVE, QUESTION_VERB_POSITIVE, QUESTION_VERB_NEGATIVE), PAST, Subject.SING_2ND, Subject.PLURAL_1ST, Subject.PLURAL_2ND, Subject.PLURAL_3RD)
    )),
    BEGIN(irregular("began", STATEMENT_POSITIVE, PAST, Subject.values())),
    COME("to", irregular("came", STATEMENT_POSITIVE, PAST, Subject.values())),
    DO(irregular("did", STATEMENT_POSITIVE, PAST, Subject.values())),
    FIND(irregular("found", STATEMENT_POSITIVE, PAST, Subject.values())),
    FILL(irregular("filled", STATEMENT_POSITIVE, PAST, Subject.values())),
    GATHER(irregular("gathered", STATEMENT_POSITIVE, PAST, Subject.values())),
    GET(irregular("got", STATEMENT_POSITIVE, PAST, Subject.values())),
    GIVE(irregular("gave", STATEMENT_POSITIVE, PAST, Subject.values())),
    GO("to", irregular("went", STATEMENT_POSITIVE, PAST, Subject.values())),
    LEAVE(irregular("left", STATEMENT_POSITIVE, PAST, Subject.values())),
    LOOK(irregular("at", STATEMENT_POSITIVE, PAST, Subject.values())),
    MAKE(irregular("made", STATEMENT_POSITIVE, PAST, Subject.values())),
    PUT(irregular("put", STATEMENT_POSITIVE, PAST, Subject.values())),
    SEE(irregular("saw", STATEMENT_POSITIVE, PAST, Subject.values())),
//    WRITE("scríobh", irregular("wrote", STATEMENT_POSITIVE, PAST, Subject.values())),
//    CONNECT("ceangail"),
//    FINISH("críochnaigh"),
//    LISTEN("éist"),
//    MEND("deisigh"),
//    OPEN("oscail"),
//    PLAY("imir"),
//    PREPARE("ullmhaigh"),
//    REACH("sroich"),
//    REPAIR("deisigh"),
//    SMOKE("caith"),
//    START("tosaigh"),
//    STAY("fan"),
//    TIE("ceangail"),
//    WAIT("fan"),
//    WASH("nigh"),
//    WATCH("amharc")

    /*
    BEGIN(),
    BREAK(),
    BRING(),
    BUILD(),
    BURN(),
    BUY(),
    CATCH(),
    CLEAN(),
    CLOSE(),
    CUT(),
    DRINK(),
    EAT(),
    GET_UP(),
    GO_AWAY(),
    GRAB(),
    HEAR(),
    HIT(),
    LEARN(),
    LOSE(),
    MEET(),
    READ(),
    RISE(),
    SAY(),
    SEE(),
    SELL(),
    SIT(),
    SPEND(),
    TAKE(),
    TAKE_OFF(),
    TELL(),
    THROW(),
    WAKE_UP(),
    WEAR(),
    WIN(),
    */

    ;

    // Key: Verb, Form, Tense, Subject
    // Key: Bí, Statement_Positive, Present, Sing_3rd_Masc
    // Key: Form, Tense, Subject
    // Key: Statement_Positive, Present, Sing_3rd_Masc
    // Key: Form, Tense
    // Key: Statement_Positive, Future
    // Key: Form
    // Key: Statement_Positive

    static Map<String, BiFunction<String, String, String>> SENTENCE_FORMS = ImmutableMap.<String,BiFunction<String, String, String>>builder()
            .put(""+ STATEMENT_POSITIVE.ordinal(), (s1, s2) -> String.format("%s %s", s1, s2))
            .put(STATEMENT_POSITIVE.ordinal() + Tense.FUTURE.toString(), (s1, s2) -> String.format("%s will %s", s1, s2))
            .put(STATEMENT_POSITIVE.ordinal() + Tense.CONDITIONAL.toString(), (s1, s2) -> String.format("%s would %s", s1, s2))

            .put(STATEMENT_NEGATIVE.ordinal() + Tense.PAST.toString(), (s1, s2) -> String.format("%s did not %s", s1, s2))
            .put(STATEMENT_NEGATIVE.ordinal() + Tense.PRESENT.toString(), (s1, s2) -> String.format("%s do not %s", s1, s2))
            .put(STATEMENT_NEGATIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_FEM.toString(), (s1, s2) -> String.format("%s does not %s", s1, s2))
            .put(STATEMENT_NEGATIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_MASC.toString(), (s1, s2) -> String.format("%s does not %s", s1, s2))
            .put(STATEMENT_NEGATIVE.ordinal() + Tense.FUTURE.toString(), (s1, s2) -> String.format("%s will not %s", s1, s2))
            .put(STATEMENT_NEGATIVE.ordinal() + Tense.CONDITIONAL.toString(), (s1, s2) -> String.format("%s would not %s", s1, s2))

            .put(QUESTION_VERB_POSITIVE.ordinal() + Tense.PAST.toString(), (s1, s2) -> String.format("Did %s %s", s1, s2))
            .put(QUESTION_VERB_POSITIVE.ordinal() + Tense.PRESENT.toString(), (s1, s2) -> String.format("Do %s %s", s1, s2))
            .put(QUESTION_VERB_POSITIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_FEM.toString(), (s1, s2) -> String.format("Does %s %s", s1, s2))
            .put(QUESTION_VERB_POSITIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_MASC.toString(), (s1, s2) -> String.format("Does %s %s", s1, s2))
            .put(QUESTION_VERB_POSITIVE.ordinal() + Tense.FUTURE.toString(), (s1, s2) -> String.format("Will %s %s", s1, s2))
            .put(QUESTION_VERB_POSITIVE.ordinal() + Tense.CONDITIONAL.toString(), (s1, s2) -> String.format("Would %s %s", s1, s2))

            .put(QUESTION_VERB_NEGATIVE.ordinal() + Tense.PAST.toString(), (s1, s2) -> String.format("Did %s not %s", s1, s2))
            .put(QUESTION_VERB_NEGATIVE.ordinal() + Tense.PRESENT.toString(), (s1, s2) -> String.format("Do %s not %s", s1, s2))
            .put(QUESTION_VERB_NEGATIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_FEM.toString(), (s1, s2) -> String.format("Does %s not %s", s1, s2))
            .put(QUESTION_VERB_NEGATIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_MASC.toString(), (s1, s2) -> String.format("Does %s not %s", s1, s2))
            .put(QUESTION_VERB_NEGATIVE.ordinal() + Tense.FUTURE.toString(), (s1, s2) -> String.format("Will %s not %s", s1, s2))
            .put(QUESTION_VERB_NEGATIVE.ordinal() + Tense.CONDITIONAL.toString(), (s1, s2) -> String.format("Would %s not %s", s1, s2))

            .put(Verb.BE.toString() + STATEMENT_NEGATIVE.ordinal() + Tense.PRESENT.toString(), (s1, s2) -> String.format("%s %s not", s1, s2))
            .put(Verb.BE.toString() + STATEMENT_NEGATIVE.ordinal() + Tense.PAST.toString(), (s1, s2) -> String.format("%s %s not", s1, s2))

            .put(Verb.BE.toString() + QUESTION_VERB_POSITIVE.ordinal() + Tense.PAST.toString(), (s1, s2) -> String.format("%s %s", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_POSITIVE.ordinal() + Tense.PRESENT.toString(), (s1, s2) -> String.format("%s %s", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_POSITIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_FEM.toString(), (s1, s2) -> String.format("%s %s", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_POSITIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_MASC.toString(), (s1, s2) -> String.format("%s %s", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_POSITIVE.ordinal() + Tense.FUTURE.toString(), (s1, s2) -> String.format("Will %s %s", s1, s2))
            .put(Verb.BE.toString() + QUESTION_VERB_POSITIVE.ordinal() + Tense.CONDITIONAL.toString(), (s1, s2) -> String.format("Would %s %s", s1, s2))

            .put(Verb.BE.toString() + QUESTION_VERB_NEGATIVE.ordinal() + Tense.PAST.toString(), (s1, s2) -> String.format("%s %s not", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_NEGATIVE.ordinal() + Tense.PRESENT.toString(), (s1, s2) -> String.format("%s %s not", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_NEGATIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_FEM.toString(), (s1, s2) -> String.format("%s %s not", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_NEGATIVE.ordinal() + Tense.PRESENT.toString() + Subject.SING_3RD_MASC.toString(), (s1, s2) -> String.format("%s %s not", s2, s1))
            .put(Verb.BE.toString() + QUESTION_VERB_NEGATIVE.ordinal() + Tense.FUTURE.toString(), (s1, s2) -> String.format("Will %s not %s", s1, s2))
            .put(Verb.BE.toString() + QUESTION_VERB_NEGATIVE.ordinal() + Tense.CONDITIONAL.toString(), (s1, s2) -> String.format("Would %s not %s", s1, s2))

            .build();


    private final String preposition;
    private String englishRoot;
    private Map<IrregularConjKey, String> irregularConjugations = new HashMap<>();

    Verb(String preposition, IrregularConj... irregularConjs) {
        this.preposition = preposition;
        this.englishRoot = this.name().toLowerCase();
        this.irregularConjugations = Stream.of(irregularConjs)
                .map(conj -> new SimpleEntry<>(conj.getKey(), conj.getConj()))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    Verb(IrregularConj... irregularConjs) {
        this(null, irregularConjs);
    }

    public String getConjugation(SentenceForm form, Tense tense, Subject subject) {
        IrregularConjKey irregularConjKey = new IrregularConjKey(form, tense, subject);
        String conjugation;
        if (irregularConjugations.containsKey(irregularConjKey)) {
            conjugation = irregularConjugations.get(irregularConjKey);
        } else if (SentenceForm.STATEMENT_POSITIVE.equals(form)
                && PRESENT.equals(tense)
                && Arrays.asList(SING_3RD_FEM, SING_3RD_MASC).contains(subject)) {
            conjugation = englishRoot + getPresentTenseExtension(englishRoot);
        } else {
            conjugation = englishRoot;
        }
        if (preposition != null) {
            conjugation += " " + preposition;
        }
        return conjugation;
    }

    private String getPresentTenseExtension(String root) {
        switch(root.charAt(root.length() - 1)) {
            case 'h':
            case 'o':
                return "es";
            default:
                return "s";
        }
    }


}
