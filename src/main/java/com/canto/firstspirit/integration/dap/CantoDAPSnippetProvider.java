package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.espirit.ps.psci.magicicons.MagicIcon;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.BaseContext.Env;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import org.jetbrains.annotations.NotNull;

public class CantoDAPSnippetProvider implements DataSnippetProvider<CantoDAPAsset> {

  private static final String CANTOSAAS_RESOURCES_PATH = "cantosaas/";
  private static final String CANTOSAAS_ICONS_PATH = CANTOSAAS_RESOURCES_PATH + "icons/";
  private final BaseContext context;

  public CantoDAPSnippetProvider(BaseContext context) {
    this.context = context;
  }

  @Override public Image<?> getIcon(@NotNull CantoDAPAsset cantoDAPAsset) {
    ImageAgent imageAgent = context.requireSpecialist(ImageAgent.TYPE);
    boolean approvalStatue = "Approved".equals(cantoDAPAsset.getApprovalStatus());
    String imageName = approvalStatue ? "approved.png" : "not_approved.png";
    if (context.is(Env.WEBEDIT)) {
      return imageAgent.getImageFromUrl(CANTOSAAS_ICONS_PATH + imageName);
    }
    MagicIcon magicIcon = MagicIcon.fromResource(getClass(), "/" + CANTOSAAS_ICONS_PATH + imageName);
    return magicIcon.getImage(context);
  }

  @Override public Image<?> getThumbnail(CantoDAPAsset cantoDAPAsset, Language language) {
    return context.requireSpecialist(ImageAgent.TYPE)
        .getImageFromUrl(cantoDAPAsset.getThumbnailUrl());
  }

  @NotNull @Override public String getHeader(CantoDAPAsset cantoDAPAsset, Language language) {
    return cantoDAPAsset.getTitle();
  }

  @Override public String getExtract(@NotNull CantoDAPAsset cantoDAPAsset, Language language) {
    return cantoDAPAsset.getPath();
  }
}
