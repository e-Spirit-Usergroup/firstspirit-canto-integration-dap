package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.service.CantoClientApiInstance;
import com.canto.firstspirit.service.CantoSearchParams;
import com.canto.firstspirit.service.CantoSearchParamsBuilder;
import com.canto.firstspirit.service.CantoSearchResultDTO;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;
import de.espirit.firstspirit.client.plugin.report.Parameter;
import de.espirit.firstspirit.client.plugin.report.ParameterMap;
import de.espirit.firstspirit.client.plugin.report.ParameterText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class CantoDAPStreamBuilder implements DataStreamBuilder<CantoDAPAsset>, Filterable {
    private final StreamBuilderAspectMap aspects = new StreamBuilderAspectMap();
    private final CantoClientApiInstance cantoApi;
    private ParameterMap parameterMap;
    private final ParameterText paramKeyword;
    private final ParameterText paramTag;




    public CantoDAPStreamBuilder(CantoClientApiInstance cantoApi) {
        this.cantoApi = cantoApi;
        aspects.put(Filterable.TYPE,this);
        paramKeyword = Parameter.Factory.createText("keyword", "Keyword","");
        paramTag = Parameter.Factory.createText("tag", "Tag","");
    }

    @Override
    public <A> A getAspect(StreamBuilderAspectType<A> streamBuilderAspectType) {
        return aspects.get(streamBuilderAspectType);
    }

    @Override
    public DataStream<CantoDAPAsset> createDataStream() {
        return new CantoDAPDataStream();
    }

    @Override
    public List<Parameter<?>> getDefinedParameters() {

        // return Collections.singletonList(paramKeyword);
        return Arrays.asList(paramKeyword, paramTag);
    }

    @Override
    public void setFilter(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    private class CantoDAPDataStream implements DataStream<CantoDAPAsset> {

        private final Iterator<CantoDAPAsset> cantoDAPAssets;
        private final int total;


        public CantoDAPDataStream(){
            CantoSearchParams searchParams = new CantoSearchParamsBuilder().keyword(parameterMap.get(paramKeyword)).create();
            CantoSearchResultDTO cantoSearchResultDTO = cantoApi.findAssets(searchParams);

            total = cantoSearchResultDTO.getTotal();
            cantoDAPAssets = cantoSearchResultDTO.getResults().stream().map(CantoDAPAsset::fromCantoAssetDTO).iterator();
        }

        @Override
        public List<CantoDAPAsset> getNext(int count) {
            List<CantoDAPAsset> result = new ArrayList<>();
            for (int i = 0; i < count && cantoDAPAssets.hasNext(); i++) {
                result.add(cantoDAPAssets.next());
            }
            return result;
        }

        @Override
        public boolean hasNext() {
            return cantoDAPAssets.hasNext();
        }

        @Override
        public int getTotal() {
            return total;
        }

        @Override
        public void close() {
            // nothing
        }
    }
}
