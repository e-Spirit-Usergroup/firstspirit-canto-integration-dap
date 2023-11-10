package com.canto.firstspirit.integration.dap.gom;

import com.canto.firstspirit.util.CantoScheme;
import de.espirit.common.GomDoc;
import de.espirit.common.Mandatory;
import de.espirit.firstspirit.access.store.templatestore.gom.AbstractGomElement;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class FilterElement extends AbstractGomElement {

  // Publicly available tag name, accessed later in the "Lists of XML Tags" example.
  // This should follow the name of the getter method in the including class (see below).
  public static final String TAG = "FILTER";
  private String _value;

  @GomDoc(description = "The parameter's value", since = "1.0.0") @Mandatory @NotNull public String getValue() {
    return _value;
  }

  public void setValue(@NotNull final String value) {
    _value = value;
  }

  public void validate() throws IllegalStateException {
    if (_value.isEmpty() || CantoScheme.fromString("_value") == null) {
      throw new IllegalStateException("Value must be set to one of " + Arrays.toString(CantoScheme.values()));
    }
  }

  @Override protected String getDefaultTag() {
    return TAG;
  }
}
