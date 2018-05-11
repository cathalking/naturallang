package ck.apps.leabharcleachtadh.sentencegenerator.domain;

public enum Tense {
    PAST("past"),
    PRESENT("present"),
    FUTURE("future"),
    CONDITIONAL("condi");

    private final String htmlText;

    Tense(String htmlText) {
        this.htmlText = htmlText;
    }

    public String getHtmlText() {
        return htmlText;
    }

}
