package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import org.jetbrains.annotations.NotNull;

public class CantoDAPSnippetProvider implements DataSnippetProvider<CantoDAPAsset> {

  private final BaseContext context;

  public CantoDAPSnippetProvider(BaseContext context) {
    this.context = context;
  }

  @Override public Image<?> getIcon(@NotNull CantoDAPAsset cantoDAPAsset) {
    return null;
  }

  @Override public Image<?> getThumbnail(CantoDAPAsset cantoDAPAsset, Language language) {
    return context.requireSpecialist(ImageAgent.TYPE)
        .getImageFromUrl(cantoDAPAsset.getThumbnailUrl());
  }

  @NotNull @Override public String getHeader(CantoDAPAsset cantoDAPAsset, Language language) {
    return cantoDAPAsset.getTitle();
  }

  @Override public String getExtract(@NotNull CantoDAPAsset cantoDAPAsset, Language language) {
    return null;
  }
}
