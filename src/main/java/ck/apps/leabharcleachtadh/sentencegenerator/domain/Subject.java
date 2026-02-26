package ck.apps.leabharcleachtadh.sentencegenerator.domain;

public enum Subject {
    SING_1ST("I", 1, 1),
    SING_2ND("you", 2, 1),
    SING_3RD_MASC("he", 3, 1),
    SING_3RD_FEM("she", 3, 1),
    PLURAL_1ST("we", 1, 2),
    PLURAL_2ND("you(pl)", 2, 2),
    PLURAL_3RD("they", 3, 2);

    private final String text;
    private final int person;
    private final int multiplicity;

    Subject(String text, int person, int multiplicity) {
        this.text = text;
        this.person = person;
        this.multiplicity = multiplicity;
    }

    public String getText() {
        return text;
    }

    public int getPerson() {
        return person;
    }

    public int getMultiplicity() {
        return multiplicity;
    }

}
