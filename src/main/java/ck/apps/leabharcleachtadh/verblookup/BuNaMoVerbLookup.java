package ck.apps.leabharcleachtadh.verblookup;

import ck.apps.leabharcleachtadh.games.Practice;
import ck.apps.leabharcleachtadh.sentencegenerator.Verb;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.SentenceForm;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Subject;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.Tense;
import ck.apps.leabharcleachtadh.sentencegenerator.domain.VerbUsage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BuNaMoVerbLookup {
    private static final Path DEFAULT_VERB_ROOT = Paths.get("vendor", "BuNaMo", "verb");
    private static final Map<String, VerbData> CACHE = new HashMap<>();
    private static final boolean ULSTER_PL1_ANALYTIC = Boolean.parseBoolean(
            System.getProperty("irish.ulster.pl1.analytic", "true")
    );

    public static String parse(VerbUsage verbUsage) throws IOException {
        return parse(verbUsage, PronounMode.STRICT);
    }

    public static String parse(VerbUsage verbUsage, PronounMode pronounMode) throws IOException {
        VerbData data = loadVerb(verbUsage.getVerb());
        String lemma = Practice.ENGLISH2IRISH.get(verbUsage.getVerb());
        Dependency dependency = dependencyFor(verbUsage.getSentenceForm());
        TenseForm tenseForm = data.getTenseForm(verbUsage.getTense(), dependency, verbUsage.getSubject(), ULSTER_PL1_ANALYTIC);
        String particle = particleFor(verbUsage.getSentenceForm(), verbUsage.getTense(), lemma);
        String mutated = applyMutation(tenseForm.value(), particle);
        boolean includePronoun = shouldIncludePronoun(pronounMode, verbUsage.getSubject(), tenseForm.personSpecific());

        StringBuilder sb = new StringBuilder();
        if (!particle.isEmpty()) {
            sb.append(particle).append(" ").append(mutated);
        } else {
            sb.append(mutated);
        }
        if (includePronoun) {
            sb.append(" ").append(pronounFor(verbUsage.getSubject()));
        }
        if (verbUsage.getSentenceForm().isQuestion()) {
            sb.append("?");
        }
        return sb.toString();
    }

    private static VerbData loadVerb(Verb verb) throws IOException {
        String verbRoot = Practice.ENGLISH2IRISH.get(verb);
        if (verbRoot == null || verbRoot.isBlank()) {
            throw new IOException("No Irish lemma mapping for verb: " + verb);
        }
        Path verbFile = verbRoot().resolve(verbRoot + "_verb.xml");
        if (!Files.exists(verbFile)) {
            throw new IOException("Verb file not found: " + verbFile);
        }
        String cacheKey = verbFile.toAbsolutePath().toString();
        VerbData cached = CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        VerbData data = parseVerbFile(verbFile);
        CACHE.put(cacheKey, data);
        return data;
    }

    private static VerbData parseVerbFile(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = factory.newDocumentBuilder().parse(in);
            NodeList forms = doc.getElementsByTagName("tenseForm");
            VerbData data = new VerbData();
            for (int i = 0; i < forms.getLength(); i++) {
                Element form = (Element) forms.item(i);
                String value = form.getAttribute("default").trim();
                String tense = form.getAttribute("tense").trim();
                String dependency = form.getAttribute("dependency").trim();
                String person = form.getAttribute("person").trim();
                if (!value.isEmpty()) {
                    data.put(tense, dependency, person, value);
                }
            }
            return data;
        } catch (Exception e) {
            throw new IOException("Failed to parse verb file: " + file, e);
        }
    }

    private static Dependency dependencyFor(SentenceForm form) {
        return switch (form) {
            case STATEMENT_POSITIVE -> Dependency.INDEP;
            case STATEMENT_NEGATIVE, QUESTION_VERB_POSITIVE, QUESTION_VERB_NEGATIVE -> Dependency.DEP;
        };
    }

    private static String particleFor(SentenceForm form, Tense tense, String lemma) {
        boolean irregularPastUsesAnNi = tense == Tense.PAST && usesAnNiParticlesInPast(lemma);
        return switch (form) {
            case STATEMENT_POSITIVE -> "";
            case STATEMENT_NEGATIVE -> {
                if (tense == Tense.PAST && !irregularPastUsesAnNi) {
                    yield "níor";
                }
                yield "ní";
            }
            case QUESTION_VERB_POSITIVE -> {
                if (tense == Tense.PAST && !irregularPastUsesAnNi) {
                    yield "ar";
                }
                yield "an";
            }
            case QUESTION_VERB_NEGATIVE -> {
                if (tense == Tense.PAST && !irregularPastUsesAnNi) {
                    yield "nár";
                }
                yield "nach";
            }
        };
    }

    private static String pronounFor(Subject subject) {
        return switch (subject) {
            case SING_1ST -> "mé";
            case SING_2ND -> "tú";
            case SING_3RD_MASC -> "sé";
            case SING_3RD_FEM -> "sí";
            case PLURAL_1ST -> "muid";
            case PLURAL_2ND -> "sibh";
            case PLURAL_3RD -> "siad";
        };
    }

    private static String applyMutation(String verbForm, String particle) {
        if (particle.isEmpty()) {
            return verbForm;
        }
        if (particle.equals("an") || particle.equals("nach")) {
            return eclipse(verbForm);
        }
        if (particle.equals("ní") || particle.equals("níor") || particle.equals("nár") || particle.equals("ar")) {
            return lenite(verbForm);
        }
        return verbForm;
    }

    private static String lenite(String word) {
        if (word.length() < 2) {
            return word;
        }
        if (alreadyLenited(word)) {
            return word;
        }
        char first = word.charAt(0);
        return switch (first) {
            case 's' -> shouldLeniteS(word) ? "sh" + word.substring(1) : word;
            case 'b', 'c', 'd', 'f', 'g', 'm', 'p', 't' -> first + "h" + word.substring(1);
            default -> word;
        };
    }

    private static boolean alreadyLenited(String word) {
        if (word.length() < 2) {
            return false;
        }
        char first = word.charAt(0);
        char second = word.charAt(1);
        return second == 'h' && "bcdfgmst".indexOf(first) >= 0;
    }

    private static boolean shouldLeniteS(String word) {
        if (word.length() < 2) {
            return true;
        }
        char second = word.charAt(1);
        return "cfmpt".indexOf(second) < 0;
    }

    private static String eclipse(String word) {
        if (word.isEmpty()) {
            return word;
        }
        if (alreadyEclipsed(word)) {
            return word;
        }
        char first = word.charAt(0);
        return switch (first) {
            case 'b' -> "mb" + word.substring(1);
            case 'c' -> "gc" + word.substring(1);
            case 'd' -> "nd" + word.substring(1);
            case 'f' -> "bhf" + word.substring(1);
            case 'g' -> "ng" + word.substring(1);
            case 'p' -> "bp" + word.substring(1);
            case 't' -> "dt" + word.substring(1);
            case 'a', 'e', 'i', 'o', 'u', 'á', 'é', 'í', 'ó', 'ú' -> "n-" + word;
            default -> word;
        };
    }

    private static boolean alreadyEclipsed(String word) {
        return word.startsWith("mb")
                || word.startsWith("gc")
                || word.startsWith("nd")
                || word.startsWith("bhf")
                || word.startsWith("ng")
                || word.startsWith("bp")
                || word.startsWith("dt")
                || word.startsWith("n-");
    }

    private static Path verbRoot() {
        String direct = System.getProperty("bunamo.verb.dir");
        if (direct == null || direct.isBlank()) {
            direct = System.getenv("BUNAMO_VERB_DIR");
        }
        if (direct != null && !direct.isBlank()) {
            return Paths.get(direct);
        }
        String root = System.getProperty("bunamo.root");
        if (root == null || root.isBlank()) {
            root = System.getenv("BUNAMO_ROOT");
        }
        if (root != null && !root.isBlank()) {
            return Paths.get(root, "verb");
        }
        return DEFAULT_VERB_ROOT;
    }

    private enum Dependency {
        INDEP,
        DEP
    }

    public enum PronounMode {
        ALWAYS,
        PREFER_SYNTHETIC,
        STRICT,
        OMIT
    }

    private static class VerbData {
        private final Map<String, Map<String, Map<String, String>>> forms = new HashMap<>();

        void put(String tense, String dependency, String person, String value) {
            forms.computeIfAbsent(tense, key -> new HashMap<>())
                    .computeIfAbsent(dependency, key -> new HashMap<>())
                    .put(person, value);
        }

        TenseForm getTenseForm(Tense tense, Dependency dependency, Subject subject, boolean ulsterPl1Analytic) throws IOException {
            String tenseKey = switch (tense) {
                case PAST -> "Past";
                case PRESENT -> "PresCont";
                case FUTURE -> "Fut";
                case CONDITIONAL -> "Cond";
            };
            String dependencyKey = dependency == Dependency.INDEP ? "Indep" : "Dep";
            String personKey = personKeyFor(subject);

            Map<String, Map<String, String>> tenseForms = forms.get(tenseKey);
            if (tenseForms == null) {
                throw new IOException("No tense forms for " + tenseKey);
            }
            Map<String, String> dependencyForms = tenseForms.get(dependencyKey);
            if (dependencyForms == null) {
                throw new IOException("No dependency forms for " + tenseKey + " " + dependencyKey);
            }
            boolean forceAnalytic = ulsterPl1Analytic && subject == Subject.PLURAL_1ST;
            String value = forceAnalytic ? dependencyForms.get("Base") : dependencyForms.get(personKey);
            boolean personSpecific = value != null && !"Base".equals(personKey) && !forceAnalytic;
            if (value == null && !"Base".equals(personKey)) {
                value = dependencyForms.get("Base");
            }
            if (value == null && "Pl3".equals(personKey)) {
                value = dependencyForms.get("Base");
            }
            if (value == null) {
                throw new IOException("Missing form for tense=" + tenseKey + " dependency=" + dependencyKey + " person=" + personKey);
            }
            return new TenseForm(value, personSpecific);
        }

        private String personKeyFor(Subject subject) {
            return switch (subject) {
                case SING_1ST -> "Sg1";
                case SING_2ND -> "Sg2";
                case SING_3RD_MASC, SING_3RD_FEM -> "Base";
                case PLURAL_1ST -> "Pl1";
                case PLURAL_2ND -> "Pl2";
                case PLURAL_3RD -> "Pl3";
            };
        }
    }

    private record TenseForm(String value, boolean personSpecific) {}

    private static boolean shouldIncludePronoun(PronounMode mode, Subject subject, boolean personSpecific) {
        if (mode == PronounMode.OMIT) {
            return false;
        }
        if (mode == PronounMode.ALWAYS) {
            return true;
        }
        if (!personSpecific) {
            return true;
        }
        if (mode == PronounMode.STRICT) {
            return false;
        }
        return switch (subject) {
            case SING_1ST, SING_2ND, PLURAL_1ST -> false;
            case SING_3RD_MASC, SING_3RD_FEM, PLURAL_2ND, PLURAL_3RD -> true;
        };
    }

    private static boolean usesAnNiParticlesInPast(String lemma) {
        if (lemma == null) {
            return false;
        }
        return switch (lemma) {
            case "bí", "abair", "déan", "faigh", "feic", "téigh" -> true;
            default -> false;
        };
    }
}
