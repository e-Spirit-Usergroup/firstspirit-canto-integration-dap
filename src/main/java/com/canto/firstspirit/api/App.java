package com.canto.firstspirit.api;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.api.model.CantoSearchResult;

import java.util.Collections;
import java.util.NoSuchElementException;

public class App {


    public static void main(String[] args) {

        final String tenant = "fernhomberg.staging.cantoflight.com";
        final String token = "ee17b5786dcc4e9b9ee9f7ee267ad039";
        final String mdc_domain = "fernhomberg.staging.cantoflight.com";
        final String mdc_account_id = "ee17b5786dcc4e9b9ee9f7ee267ad039";


        CantoApi api = new CantoApi(tenant, token, mdc_domain, mdc_account_id);

        CantoSearchResult searchResult = api.search("mia");

        if (searchResult != null && searchResult.getResults() != null) {
           System.out.println("Got " + searchResult.getFound() + " results");

           for (CantoAsset asset: searchResult.getResults()) {
                System.out.println("-> " + asset.getId() + " - " + asset.getName());

                String previewUrl = api.getPreviewImageUrl(asset);
                System.out.println("Preview: " + previewUrl);
           }
        }

        System.out.println(searchResult);

        CantoAsset asset = api.getAssets(Collections.singleton("image/0r6uoq2kd91vnbcai5rlng2v4u")).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Element with identifier not found"));

        System.out.println(asset.getId());
    }
}
