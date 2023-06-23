package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.canto.firstspirit.service.CantoSaasServiceProjectBoundClient;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.util.CantoScheme;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;
import de.espirit.firstspirit.client.plugin.report.Parameter;
import de.espirit.firstspirit.client.plugin.report.Parameter.Factory;
import de.espirit.firstspirit.client.plugin.report.ParameterMap;
import de.espirit.firstspirit.client.plugin.report.ParameterSelect;
import de.espirit.firstspirit.client.plugin.report.ParameterSelect.SelectItem;
import de.espirit.firstspirit.client.plugin.report.ParameterText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;


public class CantoDAPStreamBuilder implements DataStreamBuilder<CantoDAPAsset>, Filterable {

  private final StreamBuilderAspectMap aspects = new StreamBuilderAspectMap();
  private final CantoSaasServiceProjectBoundClient cantoSaasServiceClient;
  private ParameterMap parameterMap;
  private final ParameterText paramKeyword;
  private final ParameterSelect paramScheme;

  public CantoDAPStreamBuilder(CantoSaasServiceProjectBoundClient cantoSaasServiceClient) {
    this.cantoSaasServiceClient = cantoSaasServiceClient;
    aspects.put(Filterable.TYPE, this);
    paramKeyword = Parameter.Factory.createText("keyword", "Keyword", "");

    List<SelectItem> schemeSelectList = Arrays.stream(CantoScheme.values())
        .map(cantoScheme -> Factory.createSelectItem(cantoScheme.getDisplayName(), cantoScheme.toString()))
        .collect(Collectors.toList());

    schemeSelectList.add(0, Parameter.Factory.createSelectItem("-", ""));
    paramScheme = Parameter.Factory.createSelect("scheme", schemeSelectList, "");
  }

  @Override public <A> A getAspect(@NotNull StreamBuilderAspectType<A> streamBuilderAspectType) {
    return aspects.get(streamBuilderAspectType);
  }

  @NotNull @Override public DataStream<CantoDAPAsset> createDataStream() {
    return new CantoDAPDataStream();
  }

  @NotNull @Override public List<Parameter<?>> getDefinedParameters() {
    return List.of(paramKeyword, paramScheme);
  }

  @Override public void setFilter(@NotNull ParameterMap parameterMap) {
    this.parameterMap = parameterMap;
  }

  private class CantoDAPDataStream implements DataStream<CantoDAPAsset> {

    private Queue<CantoDAPAsset> fetchedAssets;

    private int total = 0;
    private int availableAssets = 0;
    private boolean hasNext = true;

    CantoSearchParams searchParams;

    public CantoDAPDataStream() {
      searchParams = new CantoSearchParams(0, 0, parameterMap.get(paramKeyword), parameterMap.get(paramScheme));
      fetchedAssets = null;
    }

    /**
     * Fetch the next page of assets if available. Sets hasNext to false, if offset is larger than total counts
     */
    private void fetchNextPage() {
      final int pageSize = 50;

      if (fetchedAssets == null) {
        fetchedAssets = new LinkedList<>();
      }

      searchParams = new CantoSearchParams(searchParams.getStart() + searchParams.getLimit(),
                                           pageSize,
                                           parameterMap.get(paramKeyword),
                                           parameterMap.get(paramScheme));

      if (searchParams.getStart() <= total) {
        Logging.logInfo("[fetchNextPage] Fetching next page, " + searchParams + ", total=" + total, this.getClass());

        CantoSearchResultDTO cantoSearchResultDTO = cantoSaasServiceClient.fetchSearch(searchParams);
        for (CantoAssetDTO assetDTO : cantoSearchResultDTO.getResults()) {
          fetchedAssets.add(CantoDAPAsset.fromCantoAssetDTO(assetDTO));
        }
        total = cantoSearchResultDTO.getTotal();
        availableAssets = fetchedAssets.size();
      } else {
        hasNext = false;
        Logging.logInfo("[fetchNextPage] no next Page available. total fetched " + total, this.getClass());
      }
    }

    @NotNull @Override public List<CantoDAPAsset> getNext(int count) {

      Logging.logInfo("[getNext], count=" + count + ", availableAssets=" + availableAssets + ", hasNext=" + hasNext + ", total=" + total,
                      this.getClass());
      if (availableAssets < count && hasNext) {
        fetchNextPage();
      }

      // To prevent buggy behaviour when FS keeps asking for 0 elements. Makes the DAP Flicker.
      // Happens, when total count and actual count do not exactly match for any reason
      if (count == 0) {
        hasNext = false;
      }

      List<CantoDAPAsset> result = new ArrayList<>();
      for (int i = 0; i < count && !fetchedAssets.isEmpty(); i++) {
        result.add(fetchedAssets.poll());
        availableAssets--;
      }

      return result;
    }

    @Override public boolean hasNext() {
      return hasNext;
    }

    @Override public int getTotal() {
      // -1 better than a count, that is not 100% sure to be correct, as it may produce buggy behaviour otherwise
      return -1;
    }

    @Override public void close() {
      // nothing
    }
  }
}
