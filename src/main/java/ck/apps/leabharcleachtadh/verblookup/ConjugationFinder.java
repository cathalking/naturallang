package ck.apps.leabharcleachtadh.verblookup;

import java.util.function.Predicate;

public class ConjugationFinder {

    private static final Predicate<String> STMT_POS_PREDICATE = conj -> {
        return isUlsterForm(conj.replaceAll("\\?", "")) && !conj.contains(" sí");
    };

    private static boolean isUlsterForm(String conj) {
        return
                !conj.endsWith("dar") && !conj.endsWith("mar") &&
                !conj.endsWith("mid") &&
                !conj.endsWith("dís") && !conj.endsWith("mis");
    }

    // conj.matches(HtmlVerbLookup.REGEX_IRISH_PRONOUNS);
    private static final Predicate<String> STMT_NEG_PREDICATE = conj -> STMT_POS_PREDICATE.test(conj) && !conj.endsWith("?");
    private static final Predicate<String> QUST_POS_PREDICATE = conj -> STMT_POS_PREDICATE.test(conj) && conj.endsWith("?");
    private static final String STMT_POS_QUERY = "div.introd > div.line > span.primary";
    private static final String QUST_POS_QUERY = "div.introd > div.bulletted > span.value";
    private static final String STMT_NEG_QUERY = QUST_POS_QUERY;
    public final static ConjugationFinder QUST_POS = new ConjugationFinder(QUST_POS_QUERY, QUST_POS_PREDICATE);
    public final static ConjugationFinder STMT_NEG = new ConjugationFinder(STMT_NEG_QUERY, STMT_NEG_PREDICATE);
    public final static ConjugationFinder STMT_POS = new ConjugationFinder(STMT_POS_QUERY, STMT_POS_PREDICATE);

    private String query;
    private Predicate<String> predicate;

    public ConjugationFinder(String query, Predicate<String> predicate) {
        this.query = query;
        this.predicate = predicate;
    }

    public String getQuery() {
        return query;
    }

    public Predicate<String> getPredicate() {
        return predicate;
    }
}
