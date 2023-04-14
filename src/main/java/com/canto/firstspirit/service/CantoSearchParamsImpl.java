package com.canto.firstspirit.service;

public class CantoSearchParamsImpl implements CantoSearchParams {
    private static final long serialVersionUID = 1L;

    private final int start;
    private final int limit;


    @Override
    public String getKeyword() {
        return keyword;
    }

    private final String keyword;

    public CantoSearchParamsImpl(int start, int limit, String keyword) {
        this.start = start;
        this.limit = limit;
        this.keyword = keyword;
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
