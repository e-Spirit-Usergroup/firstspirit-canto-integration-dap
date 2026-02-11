package com.canto.firstspirit.integration.dap.gom;

import com.canto.firstspirit.util.CantoScheme;
import de.espirit.common.GomDoc;
import de.espirit.common.Mandatory;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.store.templatestore.gom.AbstractGomElement;
import de.espirit.firstspirit.access.store.templatestore.gom.GomCheckable;
import de.espirit.firstspirit.access.store.templatestore.gom.GomElement;
import de.espirit.firstspirit.access.store.templatestore.gom.GomValidationError;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.GomConfigurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GomConfigurableAspect implements GomConfigurable {

  private CumulusGomConfigurable _gom;

  public GomConfigurableAspect() {
  }

  public @NotNull GomElement createConfiguration() {
    return new CumulusGomConfigurable();
  }

  public void setConfiguration(@NotNull GomElement configuration) {
    this._gom = (CumulusGomConfigurable) configuration;
  }

  @Nullable public CantoScheme getScheme() {
    return this._gom != null && this._gom.getFilter() != null && this._gom.getFilter()
        .getScheme() != null ? this._gom.getFilter()
        .getScheme() : null;
  }

  @SuppressWarnings("unused")
  public static class CumulusGomConfigurable extends AbstractGomElement implements GomCheckable {

    private static final long serialVersionUID = -5112037660541802640L;
    private Filter _filter;

    public CumulusGomConfigurable() {
    }

    protected String getDefaultTag() {
      return "CANTO_CONFIG";
    }

    @GomDoc(description = "Filter the available Assets.", since = "1.0.0") public Filter getFilter() {
      return this._filter;
    }

    public void setFilter(Filter filter) {
      this._filter = filter;
    }

    public void verify() throws IllegalStateException {
    }

    public void validate(GomCheckable.Context context) throws GomValidationError {
      Logging.logInfo("[VALIDATE]" + context.getElement() + context.getProvider(), this.getClass());
      this.verify();
    }

    public static class Filter extends AbstractGomElement {

      private static final long serialVersionUID = -4066644293406623904L;
      private CantoScheme scheme;

      public Filter() {
      }

      protected String getDefaultTag() {
        return "FILTER";
      }

      @GomDoc(description = "Filter for Scheme.", since = "1.0.0") @Mandatory public CantoScheme getScheme() {
        return this.scheme;
      }

      public void setScheme(CantoScheme scheme) {
        this.scheme = scheme;
      }
    }
  }
}
