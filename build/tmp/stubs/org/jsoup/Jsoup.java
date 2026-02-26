package org.jsoup;

import org.jsoup.nodes.Document;

public final class Jsoup {
    private Jsoup() {}

    public static Connection connect(String ignoredUrl) {
        return new Connection();
    }

    public static final class Connection {
        public Connection data(String... ignoredKeyValues) {
            return this;
        }

        public Connection userAgent(String ignoredUserAgent) {
            return this;
        }

        public Document get() {
            return new Document();
        }

        public Document post() {
            return new Document();
        }
    }
}
