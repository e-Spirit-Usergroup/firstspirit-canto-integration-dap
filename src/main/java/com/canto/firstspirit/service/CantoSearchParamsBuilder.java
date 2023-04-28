package com.canto.firstspirit.service;

import com.canto.firstspirit.service.server.CantoSearchParams;

public class CantoSearchParamsBuilder {

    private String keyword;


    public CantoSearchParamsBuilder keyword(final String keyword) {
        this.keyword = keyword;
        return this;
    }

    public CantoSearchParams create() {
        return new CantoSearchParams(0, Integer.MAX_VALUE, keyword);
    }
}