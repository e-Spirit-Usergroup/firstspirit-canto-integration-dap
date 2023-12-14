package com.canto.firstspirit.integration.dap;

import static com.canto.firstspirit.config.CantoProjectAppConfiguration.PARAM_TENANT;

import com.canto.firstspirit.config.CantoProjectApp;
import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.espirit.moddev.components.annotations.PublicComponent;
import com.espirit.ps.psci.genericconfiguration.Values;
import com.espirit.ps.psci.magicicons.MagicIcon;
import com.espirit.ps.psci.magicicons.usages.ReportIcon;
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
  private String tenant;

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

      Values config = CantoProjectApp.getConfig(baseContext);
      this.tenant = config.getString(PARAM_TENANT, "");

    }
  }

  @Override public void tearDown() {

  }

  @Override public Image<?> getReportIcon(final boolean active) {
    final ImageAgent imageAgent = context.requireSpecialist(ImageAgent.TYPE);
    if (context.is(BaseContext.Env.WEBEDIT)) {
      return imageAgent.getImageFromUrl(CANTOSAAS_ICONS_PATH + "canto_logo_small.png");
    }
    final MagicIcon magicIcon = MagicIcon.fromResource(getClass(), '/' + CANTOSAAS_ICONS_PATH + "canto_logo.png");
    return ReportIcon.create(magicIcon, context)
        .getIcon(active);
  }


  @Override public ReportItem<CantoDAPAsset> getClickItem() {
    return new OpenInCantoDialogItem(tenant);
  }

  @NotNull @Override public Collection<? extends ReportItem<CantoDAPAsset>> getItems() {
    return Collections.emptyList();
  }

  private static class OpenInCantoDialogItem implements ClientScriptProvidingReportItem<CantoDAPAsset> {

    static final String ICON_URL = null; // MagicIcon.fromResource(CantoDAP.class, '/' + CANTOSAAS_ICONS_PATH + "openInNewWindow.png").base64url();
    private final String tenant;

    private OpenInCantoDialogItem(String tenant) {
      this.tenant = tenant;
    }

    @Override public boolean isVisible(@NotNull final ReportContext<CantoDAPAsset> reportContext) {
      return true;
    }

    @Override public boolean isEnabled(@NotNull final ReportContext<CantoDAPAsset> reportContext) {
      return true;
    }

    @NotNull @Override public String getLabel(@NotNull final ReportContext<CantoDAPAsset> cantoDAPAssetReportContext) {
      return "Goto Canto";
    }

    @Override public String getIconPath(@NotNull final ReportContext<CantoDAPAsset> cantoDAPAssetReportContext) {
      return ICON_URL;
    }

    @Override public String getScript(final ReportContext<CantoDAPAsset> cantoDAPAssetReportContext) {
      if (tenant != null && !tenant.isBlank()) {
        final CantoDAPAsset cantoDAPAsset = cantoDAPAssetReportContext.getObject();
        final String url = "https://" + tenant + "/allfiles?column=" + cantoDAPAsset.getSchema() + "&id=" + cantoDAPAsset.getId();
        return "window.open('" + url + "', '_blank')";
      }
      return null;
    }

  }
}
