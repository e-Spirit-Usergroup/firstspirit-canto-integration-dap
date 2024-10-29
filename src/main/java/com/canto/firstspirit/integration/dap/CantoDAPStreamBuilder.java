package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.canto.firstspirit.service.CantoSaasServiceProjectBoundClient;
import com.canto.firstspirit.service.factory.CantoConfigurationFactory;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.canto.firstspirit.service.server.model.CantoSearchParams;
import com.canto.firstspirit.service.server.model.CantoSearchResultDTO;
import com.canto.firstspirit.util.CantoScheme;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.SpecialistsBroker;
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
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class CantoDAPStreamBuilder implements DataStreamBuilder<CantoDAPAsset>, Filterable {

  private final StreamBuilderAspectMap aspects = new StreamBuilderAspectMap();
  private final CantoSaasServiceProjectBoundClient cantoSaasServiceClient;
  private final CantoDAPFilter filter;
  private final ParameterText paramKeyword;
  private final ParameterSelect paramScheme;
  private ParameterSelect paramApprovalStatus;
  private boolean isConsumer;

  private ParameterMap parameterMap;

  public CantoDAPStreamBuilder(CantoSaasServiceProjectBoundClient cantoSaasServiceClient, CantoDAPFilter filter, SpecialistsBroker broker) {

    this.filter = filter;
    this.cantoSaasServiceClient = cantoSaasServiceClient;
    this.paramApprovalStatus = null;
    CantoConfiguration cantoConfiguration = CantoConfigurationFactory.fromProjectBroker(broker);
    String scope = cantoSaasServiceClient.fetchUserScope(cantoConfiguration.getUserId());
    isConsumer = "consumer".equals(scope.toLowerCase());

    aspects.put(Filterable.TYPE, this);
    paramKeyword = Parameter.Factory.createText("keyword", "Keyword", "");

    // Only show Scheme Selection in UI if no fixed Scheme is set
    List<SelectItem> schemeSelectList = Arrays.stream(CantoScheme.values())
        .filter(cantoScheme -> filter.getValidScheme() == null || filter.getValidScheme()
            .equals(cantoScheme))
        .map(cantoScheme -> Factory.createSelectItem(cantoScheme.getDisplayName(), cantoScheme.toString()))
        .collect(Collectors.toList());
    if (filter.getValidScheme() == null) {
      schemeSelectList.add(0, Parameter.Factory.createSelectItem("-", ""));
      paramScheme = Parameter.Factory.createSelect("scheme", schemeSelectList, "");
    } else {
      paramScheme = Parameter.Factory.createSelect("scheme",
                                                   schemeSelectList,
                                                   filter.getValidScheme()
                                                       .toString());
    }

    if (!isConsumer) {
      List<SelectItem> approvalStatus = List.of(Factory.createSelectItem("-", ""), Factory.createSelectItem("Approved", "Approved"), Factory.createSelectItem("Pending", "Pending"));

      paramApprovalStatus = Parameter.Factory.createSelect("approvalStatus", approvalStatus, "");
    }


  }

  @Override public <A> A getAspect(@NotNull StreamBuilderAspectType<A> streamBuilderAspectType) {
    return aspects.get(streamBuilderAspectType);
  }

  @NotNull @Override public DataStream<CantoDAPAsset> createDataStream() {
    return new CantoDAPDataStream();
  }

  @NotNull @Override public List<Parameter<?>> getDefinedParameters() {
    // Parameters may be set to null to hide them in UI
    return Stream.of(paramKeyword, paramScheme, paramApprovalStatus)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override public void setFilter(@NotNull ParameterMap parameterMap) {
    this.parameterMap = parameterMap;
  }

  private class CantoDAPDataStream implements DataStream<CantoDAPAsset> {

    CantoSearchParams searchParams;
    private Queue<CantoDAPAsset> fetchedAssets;
    private int total = 0;
    private int availableAssets = 0;
    private boolean hasNext = true;

    public CantoDAPDataStream() {
      searchParams = new CantoSearchParams(0,
                                           0,
                                           parameterMap.get(paramKeyword),
                                           filter.getValidScheme() != null ? filter.getValidScheme()
                                               .toString() : parameterMap.get(paramScheme),
                                           paramApprovalStatus != null ? parameterMap.get(paramApprovalStatus) : null);

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

      if (!isConsumer) {
        searchParams = new CantoSearchParams(searchParams.getStart() + searchParams.getLimit(),
                                             pageSize,
                                             parameterMap.get(paramKeyword),
                                             filter.getValidScheme() != null ? filter.getValidScheme()
                                                 .toString() : parameterMap.get(paramScheme),
                                             parameterMap.get(paramApprovalStatus));
      } else {
        searchParams = new CantoSearchParams(searchParams.getStart() + searchParams.getLimit(),
                                             pageSize,
                                             parameterMap.get(paramKeyword),
                                             filter.getValidScheme() != null ? filter.getValidScheme()
                                                 .toString() : parameterMap.get(paramScheme),
                                             null);
      }

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

      Logging.logInfo("[getNext], count=" + count + ", availableAssets=" + availableAssets + ", hasNext=" + hasNext + ", total=" + total, this.getClass());
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
