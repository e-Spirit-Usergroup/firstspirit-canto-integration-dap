package com.canto.firstspirit.integration.dap;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;

public class CantoDAPSnippetProvider implements DataSnippetProvider<CantoDAPAsset> {
    private BaseContext context;

    public CantoDAPSnippetProvider(BaseContext context) {
        this.context = context;
    }

    @Override
    public Image<?> getIcon(CantoDAPAsset cantoDAPAsset) {
        return null;
    }

    @Override
    public Image<?> getThumbnail(CantoDAPAsset cantoDAPAsset, Language language) {
        return context.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(cantoDAPAsset.getThumbnailUrl());
    }

    @Override
    public String getHeader(CantoDAPAsset cantoDAPAsset, Language language) {
        return cantoDAPAsset.getTitle();
    }

    @Override
    public String getExtract(CantoDAPAsset cantoDAPAsset, Language language) {
        return null;
    }
}
