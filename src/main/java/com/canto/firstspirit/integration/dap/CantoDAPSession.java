package com.canto.firstspirit.integration.dap;

import static com.canto.firstspirit.util.JsonUtils.getMapAsJsonObject;
import static com.canto.firstspirit.util.JsonUtils.getNullOrNumberJsonValue;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.canto.firstspirit.service.CantoSaasServiceProjectBoundClient;
import com.canto.firstspirit.service.factory.CantoAssetIdentifierSerializer;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.HandlerHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.SupplierHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import de.espirit.firstspirit.json.values.JsonStringValue;
import de.espirit.firstspirit.ui.gadgets.aspects.transfer.TransferType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class CantoDAPSession implements DataAccessSession<CantoDAPAsset>, TransferHandling<CantoDAPAsset>, TransferSupplying<CantoDAPAsset>,
    DataTemplating<CantoDAPAsset>, JsonSupporting<CantoDAPAsset> {

  private final BaseContext context;
  private final CantoDAPFilter filter;

  final private SessionAspectMap sessionAspectMap = new SessionAspectMap();
  private final CantoSaasServiceProjectBoundClient cantoSaasServiceClient;

  CantoDAPSession(BaseContext baseContext, CantoDAPFilter filter) {

    Logging.logDebug("CantoDapSession Created", this.getClass());
    this.context = baseContext;
    this.filter = filter;

    cantoSaasServiceClient = new CantoSaasServiceProjectBoundClient(context);

    sessionAspectMap.put(TransferHandling.TYPE, this);
    sessionAspectMap.put(TransferSupplying.TYPE, this);
    sessionAspectMap.put(DataTemplating.TYPE, this);
    sessionAspectMap.put(JsonSupporting.TYPE, this);

  }

  @Override public <A> A getAspect(@NotNull SessionAspectType<A> sessionAspectType) {
    return this.sessionAspectMap.get(sessionAspectType);
  }

  @NotNull @Override public CantoDAPAsset getData(@NotNull final String identifier) throws NoSuchElementException {
    Logging.logInfo("[getData] Single: " + identifier, getClass());
    List<CantoDAPAsset> list = getData(Collections.singleton(identifier));
    if (list.isEmpty() || list.get(0) == null) {
      throw new NoSuchElementException("Element with identifier " + identifier + " not found");
    }
    return list.get(0);
  }

  @NotNull @Override public List<CantoDAPAsset> getData(@NotNull final Collection<String> identifiers) {
    Logging.logInfo("[getData] Multi: [" + Strings.implode(identifiers, ", ") + "]", getClass());
    final List<CantoAssetIdentifier> assetIdentifiers = identifiers.stream()
        .map(CantoAssetIdentifierSerializer::fromJsonIdentifier)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    if (assetIdentifiers.isEmpty()) {
      Logging.logInfo("[getData] Empty Identifierlist. Returning empty List", getClass());
      return Collections.emptyList();
    }

    return cantoSaasServiceClient.fetchAssetDTOs(assetIdentifiers)
        .stream()
        .map(cantoAssetDTO -> cantoAssetDTO != null ? CantoDAPAsset.fromCantoAssetDTO(cantoAssetDTO) : null)
        .collect(Collectors.toList());
  }

  @NotNull @Override public String getIdentifier(@NotNull final CantoDAPAsset cantoDAPAsset) throws NoSuchElementException {
    return cantoDAPAsset.getJsonIdentifier();
  }


  @NotNull @Override public JsonElement<?> handle(@NotNull JsonGenerationContext jsonGenerationContext, CantoDAPAsset cantoDAPAsset) {
    final JsonObject jsonResult = JsonObject.create();

    JsonElement<?> additionalInfoJSON = getMapAsJsonObject(cantoDAPAsset.getAdditionalInfo());

    JsonElement<?> additionalDataJSON = getMapAsJsonObject(cantoDAPAsset.getAdditionalIdentifierData());

    jsonResult.put(JsonPair.of("title", JsonStringValue.ofNullable(cantoDAPAsset.getTitle())));
    jsonResult.put(JsonPair.of("previewBaseUrl", JsonStringValue.ofNullable(cantoDAPAsset.getPreviewBaseUrl())));
    jsonResult.put(JsonPair.of("originalAssetUrl", JsonStringValue.ofNullable(cantoDAPAsset.getOriginalAssetUrl())));
    jsonResult.put(JsonPair.of("mdcImageUrl", JsonStringValue.ofNullable(cantoDAPAsset.getMDCImageUrl())));
    jsonResult.put(JsonPair.of("mdcAssetUrl", JsonStringValue.ofNullable(cantoDAPAsset.getMDCAssetUrl())));
    jsonResult.put(JsonPair.of("path", JsonStringValue.ofNullable(cantoDAPAsset.getPath())));
    jsonResult.put(JsonPair.of("id", JsonStringValue.ofNullable(cantoDAPAsset.getId())));
    jsonResult.put(JsonPair.of("scheme", JsonStringValue.ofNullable(cantoDAPAsset.getSchema())));
    jsonResult.put(JsonPair.of("description", JsonStringValue.ofNullable(cantoDAPAsset.getDescription())));
    jsonResult.put(JsonPair.of("byteSize", getNullOrNumberJsonValue(cantoDAPAsset.getByteSize())));
    jsonResult.put(JsonPair.of("width", getNullOrNumberJsonValue(cantoDAPAsset.getWidth())));
    jsonResult.put(JsonPair.of("height", getNullOrNumberJsonValue(cantoDAPAsset.getHeight())));
    jsonResult.put(JsonPair.of("copyright", JsonStringValue.ofNullable(cantoDAPAsset.getCopyright())));
    jsonResult.put(JsonPair.of("fileExtension", JsonStringValue.ofNullable(cantoDAPAsset.getFileExtension())));
    jsonResult.put(JsonPair.of("additionalInfo", additionalInfoJSON));
    jsonResult.put(JsonPair.of("additionalData", additionalDataJSON));

    return jsonResult;
  }


  @NotNull @Override public Class<CantoDAPAsset> getSupportedClass() {
    return CantoDAPAsset.class;
  }


  @NotNull @Override public DataSnippetProvider<CantoDAPAsset> createDataSnippetProvider() {
    return new CantoDAPSnippetProvider(context);
  }

  @NotNull @Override public DataStreamBuilder<CantoDAPAsset> createDataStreamBuilder() {
    return new CantoDAPStreamBuilder(cantoSaasServiceClient, filter);
  }

  @Override public void registerHandlers(HandlerHost<CantoDAPAsset> handlerHost) {
    TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
    TransferType<CantoDAPAsset> rawValueType = transferAgent.getRawValueType(CantoDAPAsset.class);

    Logging.logInfo("Registering Transferhandler", this.getClass());
    // Apply Filter to given assetList
    handlerHost.registerHandler(rawValueType,
                                list -> list.stream()
                                    .filter(filter::isValid)
                                    .collect(Collectors.toList()));
  }

  @Override public void registerSuppliers(SupplierHost<CantoDAPAsset> supplierHost) {
    TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
    TransferType<CantoDAPAsset> rawValueType = transferAgent.getRawValueType(CantoDAPAsset.class);
    supplierHost.registerSupplier(rawValueType, Collections::singletonList);
  }

  @Override public String getTemplate(@NotNull CantoDAPAsset cantoDAPAsset, @NotNull Language language) {
    return "<div style=\"padding: 20px;\"><h2>${title}</h2>" + "<div><img src=\"${image}\" /></div>"
        + "<a style=\"display: inline-block; padding: 6px 15px; border-radius: 50px; margin-top: 10px; background: #fa9100; color: white; font-weight: 600;\" target=\"_blank\" href=\"${cantoUrl}\">goto Canto</a></div>";
  }

  @Override public void registerParameters(ParameterSet parameterSet, CantoDAPAsset cantoDAPAsset, @NotNull Language language) {
    parameterSet.addText("title", cantoDAPAsset.getTitle());
    parameterSet.addText("image", cantoDAPAsset.getMDCImageUrl("-FPNG-S300"));
    parameterSet.addText("cantoUrl", "https://reply.canto.de/allfiles?column=" + cantoDAPAsset.getSchema() + "&id=" + cantoDAPAsset.getId());
  }
}
