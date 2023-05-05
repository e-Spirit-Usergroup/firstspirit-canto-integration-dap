package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoAssetPath;
import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;

import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.HandlerHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.SupplierHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import de.espirit.firstspirit.json.values.JsonStringValue;
import de.espirit.firstspirit.ui.gadgets.aspects.transfer.TransferType;

import com.canto.firstspirit.service.CantoServiceProjectAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

public class CantoDAPSession implements DataAccessSession<CantoDAPAsset>, TransferHandling<CantoDAPAsset>, TransferSupplying<CantoDAPAsset>, DataTemplating<CantoDAPAsset> {
    private final BaseContext context;

    final private SessionAspectMap sessionAspectMap = new SessionAspectMap();
    private final CantoServiceProjectAdapter cantoServiceProjectAdapter;

    public CantoDAPSession(BaseContext baseContext) {

        Logging.logInfo("CantoDapSession Created", this.getClass());
        this.context = baseContext;

        cantoServiceProjectAdapter = CantoServiceProjectAdapter.fromProjectBroker(context);

        sessionAspectMap.put(TransferHandling.TYPE, this);
        sessionAspectMap.put(TransferSupplying.TYPE, this);
        sessionAspectMap.put(DataTemplating.TYPE, this);
        sessionAspectMap.put(JsonSupporting.TYPE, new CantoDAPJsonSupportingAspect());


    }

    @Override
    public <A> A getAspect(@NotNull SessionAspectType<A> sessionAspectType) {
        return this.sessionAspectMap.get(sessionAspectType);
    }

    @NotNull
    @Override
    public CantoDAPAsset getData(@NotNull final String s) throws NoSuchElementException {
        return getData(Collections.singleton(s))
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Element with identifier " + s + " not found"));
    }

    @NotNull
    @Override
    public List<CantoDAPAsset> getData(@NotNull final Collection<String> identifiers) {
        Logging.logInfo("getData: " + Strings.implode(identifiers, ", "), getClass());
        final List<String> assetPaths = identifiers.stream()
                .map(CantoAssetPath::fromIdentifier).filter(Objects::nonNull)
                .map(CantoAssetPath::getPath)
                .collect(Collectors.toList());
        Logging.logInfo("getData: " + Strings.implode(assetPaths, ", "), getClass());
        return cantoServiceProjectAdapter.getAssets(assetPaths).stream()
                .map(CantoDAPAsset::fromCantoAssetDTO)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public String getIdentifier(@NotNull final CantoDAPAsset cantoDAPAsset) throws NoSuchElementException {
        return cantoDAPAsset.getIdentifier();
    }

    public static class CantoDAPJsonSupportingAspect implements JsonSupporting<CantoDAPAsset> {

        @NotNull
        @Override
        public JsonElement<?> handle(@NotNull JsonGenerationContext jsonGenerationContext, CantoDAPAsset cantoDAPAsset) {
            final JsonObject jsonResult = JsonObject.create();
            jsonResult.put(JsonPair.of("title", JsonStringValue.of(cantoDAPAsset.getTitle())));
            jsonResult.put(JsonPair.of("thumbnailUrl", JsonStringValue.of(cantoDAPAsset.getThumbnailUrl())));
            jsonResult.put(JsonPair.of("path", JsonStringValue.of(cantoDAPAsset.getPath())));
            jsonResult.put(JsonPair.of("description", JsonStringValue.of(cantoDAPAsset.getDescription())));
            jsonResult.put(JsonPair.of("mdc_rendition_baseurl", JsonStringValue.of(cantoDAPAsset.getMDCRenditionBaseUrl())));
            jsonResult.put(JsonPair.of("mdc_asset_baseurl", JsonStringValue.of(cantoDAPAsset.getMDCAssetBaseUrl())));
            return jsonResult;
        }

        @NotNull
        @Override
        public Class<CantoDAPAsset> getSupportedClass() {
            return CantoDAPAsset.class;
        }
    }

    @NotNull
    @Override
    public DataSnippetProvider<CantoDAPAsset> createDataSnippetProvider() {
        return new CantoDAPSnippetProvider(context);
    }

    @NotNull
    @Override
    public DataStreamBuilder<CantoDAPAsset> createDataStreamBuilder() {
        return new CantoDAPStreamBuilder(cantoServiceProjectAdapter);
    }

    @Override
    public void registerHandlers(HandlerHost<CantoDAPAsset> handlerHost) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        TransferType<CantoDAPAsset> rawValueType = transferAgent.getRawValueType(CantoDAPAsset.class);
        handlerHost.registerHandler(rawValueType, list -> list);
    }

    @Override
    public void registerSuppliers(SupplierHost<CantoDAPAsset> supplierHost) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        TransferType<CantoDAPAsset> rawValueType = transferAgent.getRawValueType(CantoDAPAsset.class);
        supplierHost.registerSupplier(rawValueType, Collections::singletonList);
    }

    @Override
    public String getTemplate(@NotNull CantoDAPAsset cantoDAPAsset, @NotNull Language language) {

        return "<h2>${title}</h2>" +
                "<div><img src=\"${image}\" /></div>";
    }

    @Override
    public void registerParameters(ParameterSet parameterSet, CantoDAPAsset cantoDAPAsset, @NotNull Language language) {
        parameterSet.addText("title", cantoDAPAsset.getTitle());
        parameterSet.addText("image", cantoDAPAsset.getThumbnailUrl());
        /*
        parameterSet.addText("tags", Strings.implode(asset.getTags(),", "));
        parameterSet.addText("kbytes", FILE_SIZE_FORMAT.format(asset.getFileSize().doubleValue() / 1000));
        parameterSet.addText("width", String.valueOf(asset.getWidth()));
        parameterSet.addText("height", String.valueOf(asset.getHeight()));
        parameterSet.addText("date", dateFormat.format(asset.getCreationDate()));

         */
    }
}
