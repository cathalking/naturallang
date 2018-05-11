package ck.apps.leabharcleachtadh.audio;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SpeechLookup {

    public enum Speed {
        SLOWER("Níos+moille"),
        NORMAL("Gnáthluas");

        private final String text;

        Speed(String text) {
            this.text = text;
        }

        String getText() {
            return text;
        }
    }

    /**
     * @param sentence
     * @param speed
     * @return URL of audio file
     * @throws IOException
     */
    public static String toSpeech(String sentence, Speed speed) throws IOException {
        String countdown = String.valueOf(2000 - sentence.length());
        Document doc = Jsoup.connect("http://www.abair.tcd.ie/?view=files&lang=gle&page=synthesis&synth=gd&xpos=&ypos=&speed=Gnáthluas&pitch=1.0&colors=default")
                .data("input", sentence,
                      "submit", "Déan sintéis",
                      "lang", "gle",
                      "voice", "",
                      "view", "files",
                      "synth", "gd",
                      "xpos", "0",
                      "ypos", "392",
                      "page", "synthesis",
                      "xmlfile", "20170528_110218.xml",
                      "colors", "default",
                      "speed", speed.getText(),
                      "countdown", countdown)
                      // and other hidden fields which are being passed in post request.
                .userAgent("Mozilla")
                .post();
        Elements elements = doc.select("a[href$=mp3]");
        if (elements.isEmpty()) {
            return null;
        }
        Element element = elements.get(0);
        String href = element.attr("href");
        return href;
    }

    /*
curl 'http://www.abair.tcd.ie/' \
-XPOST \
-H 'Origin: http//www.abair.tcd.ie' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-H 'Referer: http://www.abair.tcd.ie/?view=files&lang=gle&page=synthesis&synth=gd&xpos=0&ypos=372&speed=Gn%C3%A1thluas&pitch=1.0&input=An+gceann%C3%B3idh+t%C3%BA+carr+nua%3F&xmlfile=20170528_110022.xml&colors=default' \
-H 'Upgrade-Insecure-Requests: 1' \
-H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,;q=0.8' \
-H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4' \
--data '
input=An+gceann%C3%B3idh+siad+carr+nua%3F&
submit=D%C3%A9an+sint%C3%A9is
lang=gle
voice=
view=files
synth=gd
xpos=0
ypos=392
page=synthesis
xmlfile=20170528_110218.xml
colors=default
speed=Gn%C3%A1thluas
countdown=1972
'

     */
}
