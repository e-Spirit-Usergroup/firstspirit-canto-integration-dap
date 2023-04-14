package com.canto.firstspirit.service;

public class CantoSearchParamsBuilder {

    private String keyword;


    public CantoSearchParamsBuilder keyword(final String keyword) {
        this.keyword = keyword;
        return this;
    }

    public CantoSearchParamsImpl create() {
        return new CantoSearchParamsImpl(0, Integer.MAX_VALUE, keyword);
    }
}