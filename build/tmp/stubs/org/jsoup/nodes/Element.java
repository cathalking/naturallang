package org.jsoup.nodes;

import org.jsoup.select.Elements;

public class Element {
    public String text() {
        return "";
    }

    public String attr(String ignoredAttribute) {
        return "";
    }

    public Elements select(String ignoredCssQuery) {
        return new Elements();
    }
}
