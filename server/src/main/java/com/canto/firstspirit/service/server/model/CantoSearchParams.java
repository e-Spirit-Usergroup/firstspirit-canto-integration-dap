package com.canto.firstspirit.service.server.model;

import java.io.Serializable;

public class CantoSearchParams implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int start;
    private final int limit;


    public String getKeyword() {
        return keyword;
    }

    private final String keyword;

    public CantoSearchParams(int start, int limit, String keyword) {
        this.start = start;
        this.limit = limit;
        this.keyword = keyword;
    }

    public CantoSearchParams(String keyword) {
        this(0, Integer.MAX_VALUE, keyword);
    }

    @Override
    public String toString() {
        return "CantoSearchParams{" +
                "start=" + start +
                ", limit=" + limit +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
