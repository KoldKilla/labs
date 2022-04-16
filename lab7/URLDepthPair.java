package com.company;


public class URLDepthPair {

    // префикс
    public static final String URL_PREFIX = "https://";

    // URL и его глубина
    private String url;
    private int depth;

    // создание объекта, который хранит URL и его глубину
    public URLDepthPair(String url, int depth) {

        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    //красивый вывод
    @Override
    public String toString() {
        return depth + " - " + url;
    }

    //сравнение URL
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof URLDepthPair) {
            return url.equals(((URLDepthPair) obj).url);
        }
        return false;
    }
}