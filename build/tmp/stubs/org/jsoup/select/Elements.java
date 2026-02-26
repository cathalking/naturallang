package org.jsoup.select;

import java.util.ArrayList;
import org.jsoup.nodes.Element;

public class Elements extends ArrayList<Element> {
    public Elements select(String ignoredCssQuery) {
        return new Elements();
    }
}
