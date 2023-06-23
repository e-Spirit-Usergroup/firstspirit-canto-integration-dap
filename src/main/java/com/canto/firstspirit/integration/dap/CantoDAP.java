package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.config.CantoProjectApp;
import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.espirit.moddev.components.annotations.PublicComponent;
import com.espirit.ps.psci.magicicons.MagicIcon;
import com.espirit.ps.psci.magicicons.usages.ReportIcon;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Streams;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessPlugin;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ReportItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Reporting;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.client.plugin.report.ReportItem;
import de.espirit.firstspirit.webedit.plugin.report.ClientScriptProvidingReportItem;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

@PublicComponent(name = "CantoSaasDAP", displayName = "CantoSaaS Connector DAP")
public class CantoDAP implements DataAccessPlugin<CantoDAPAsset>, Reporting, ReportItemsProviding<CantoDAPAsset> {

  private static final String CANTOSAAS_RESOURCES_PATH = "cantosaas/";
  private static final String CANTOSAAS_ICONS_PATH = CANTOSAAS_RESOURCES_PATH + "icons/";
  private static final String CANTOSAAS_JS_PATH = CANTOSAAS_RESOURCES_PATH + "js/";

  private final DataAccessAspectMap aspectMap = new DataAccessAspectMap();
  private BaseContext context;

  @Override public <A> A getAspect(@NotNull final DataAccessAspectType<A> dataAccessAspectType) {
    return aspectMap.get(dataAccessAspectType);
  }

  @NotNull @Override public String getLabel() {
    return "Canto";
  }

  @Override public Image<?> getIcon() {
    return null;
  }

  @NotNull @Override public DataAccessSessionBuilder<CantoDAPAsset> createSessionBuilder() {
    return new CantoDAPSessionBuilder();
  }

  @Override public void setUp(@NotNull final BaseContext baseContext) {
    this.context = baseContext;
    if (CantoProjectApp.isInstalled(context)) {
      aspectMap.put(Reporting.TYPE, this);
      aspectMap.put(ReportItemsProviding.TYPE, this);

    }
  }

  @Override public void tearDown() {

  }

  @Override public Image<?> getReportIcon(final boolean b) {
    final ImageAgent imageAgent = context.requireSpecialist(ImageAgent.TYPE);
    if (context.is(BaseContext.Env.WEBEDIT)) {
      return imageAgent.getImageFromUrl(CANTOSAAS_ICONS_PATH + "canto_logo_small.png");
    }
    final MagicIcon magicIcon = MagicIcon.fromResource(getClass(), '/' + CANTOSAAS_ICONS_PATH + "canto_logo.png");
    return ReportIcon.create(magicIcon, context)
        .getIcon(b);
  }


  @Override public ReportItem<CantoDAPAsset> getClickItem() {
    return null;
  }

  @NotNull @Override public Collection<? extends ReportItem<CantoDAPAsset>> getItems() {
    return Collections.emptyList();
  }

  @SuppressWarnings("unused")
  private static class OpenImageInDialogItem implements ClientScriptProvidingReportItem<CantoDAPAsset> {

    static final String ICON_URL = MagicIcon.fromResource(CantoDAP.class, '/' + CANTOSAAS_ICONS_PATH + "openInNewWindow.png")
        .base64url();
    static final String SCRIPT_TEMPLATE = getScriptSrc();

    @Override public boolean isVisible(@NotNull final ReportContext<CantoDAPAsset> reportContext) {
      return true;
    }

    @Override public boolean isEnabled(@NotNull final ReportContext<CantoDAPAsset> reportContext) {
      return true;
    }

    @NotNull @Override public String getLabel(@NotNull final ReportContext<CantoDAPAsset> cantoDAPAssetReportContext) {
      return "Show Image";
    }

    @Override public String getIconPath(@NotNull final ReportContext<CantoDAPAsset> cantoDAPAssetReportContext) {
      return ICON_URL;
    }

    @Override public String getScript(final ReportContext<CantoDAPAsset> cantoDAPAssetReportContext) {
      final CantoDAPAsset cantoDAPAsset = cantoDAPAssetReportContext.getObject();
      return SCRIPT_TEMPLATE.replace("${url}", cantoDAPAsset.getThumbnailUrl())
          .replace("${title}", cantoDAPAsset.getTitle());

    }

    private static String getScriptSrc() {
      final String scriptName = "openInNewWindow.js";
      final Class<CantoDAP> LOGGER = CantoDAP.class;
      final InputStream is = LOGGER.getResourceAsStream('/' + CANTOSAAS_JS_PATH + scriptName);
      if (is != null) {

        try {
          return Streams.toString(is, StandardCharsets.UTF_8.name());
        } catch (final IOException e) {
          Logging.logWarning("Could not get JS resource file " + scriptName, e, LOGGER);
        }
      }
      return "";
    }
  }
}
