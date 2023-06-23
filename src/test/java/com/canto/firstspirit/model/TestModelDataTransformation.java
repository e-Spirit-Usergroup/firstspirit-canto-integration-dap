package com.canto.firstspirit.model;

import com.canto.firstspirit.api.model.CantoAsset;
import com.canto.firstspirit.integration.dap.model.CantoDAPAsset;
import com.canto.firstspirit.service.factory.CantoAssetDTOFactory;
import com.canto.firstspirit.service.server.model.CantoAssetDTO;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestModelDataTransformation {

  static private final Moshi moshi = new Moshi.Builder().build();
  private final JsonAdapter<CantoAsset> cantoAssetJsonAdapter = moshi.adapter(CantoAsset.class);

  /**
   * Tests correct data transformation without any integration of Api, Service or DAP
   * Read JSON -> Asset -> AssetDTO -> DapAsset
   */
  @Test void testFromJSONtoDAPAsset() throws IOException, URISyntaxException {

    CantoAsset cantoAsset = cantoAssetJsonAdapter.fromJson(getImageJSON());
    CantoAssetDTO assetDTO = CantoAssetDTOFactory.fromAsset(cantoAsset);
    assert assetDTO != null;
    CantoDAPAsset cantoDAPAsset = CantoDAPAsset.fromCantoAssetDTO(assetDTO);

    // MDC IMAGE
    Assertions.assertEquals("https://cloudfront_id.cloudfront.net/image/CF_TENANT_ID/image_IMAGE_ID/", cantoDAPAsset.getMDCImageUrl());
    Assertions.assertEquals("https://cloudfront_id.cloudfront.net/image/CF_TENANT_ID/image_IMAGE_ID/-S200x200",
                            cantoDAPAsset.getMDCImageUrl("-S200x200"));
    Assertions.assertEquals("https://cloudfront_id.cloudfront.net/image/CF_TENANT_ID/image_IMAGE_ID/-FJPG", cantoDAPAsset.getUrl());

    // MDC ASSET
    Assertions.assertEquals("https://cloudfront_id.cloudfront.net/asset/CF_TENANT_ID/image_IMAGE_ID", cantoDAPAsset.getMDCAssetUrl());
    Assertions.assertEquals("https://cloudfront_id.cloudfront.net/asset/CF_TENANT_ID/image_IMAGE_ID/filename",
                            cantoDAPAsset.getMDCAssetUrl("filename"));

    // DIRECT IMAGE URLS
    Assertions.assertEquals("https://tenant.canto.de/direct/image/IMAGE_ID/IMAGE_HASH/m800/", cantoDAPAsset.getPreviewBaseUrl());
    Assertions.assertEquals("https://tenant.canto.de/direct/image/IMAGE_ID/IMAGE_HASH/m800/800", cantoDAPAsset.getPreviewUrl());
    Assertions.assertEquals("https://tenant.canto.de/direct/image/IMAGE_ID/IMAGE_HASH/m800/100", cantoDAPAsset.getThumbnailUrl());
    Assertions.assertEquals("https://tenant.canto.de/direct/image/IMAGE_ID/IMAGE_HASH/m800/2000", cantoDAPAsset.getPreviewUrl(2000));

    // DIRECT ASSET URLS
    Assertions.assertEquals(
        "https://tenant.canto.de/direct/image/IMAGE_ID/ASSET_HASH/original?content-type=image%2Fjpeg&name=lifestyle-lake-view-man.jpg",
        cantoDAPAsset.getOriginalAssetUrl());

    // METADATA
    Assertions.assertEquals("jpg", cantoDAPAsset.getFileExtension());

    Assertions.assertEquals(2976538L, cantoDAPAsset.getByteSize());
    Assertions.assertEquals(
        "Campaign,  Lifestyle,  Great Outdoors Go,  lifestyle,  Human,  Mountain,  Mountain Range,  Nature,  Outdoors,  Person,  Water,  Wilderness",
        cantoDAPAsset.getDescription());

    // IDENTIFIER
    Assertions.assertEquals("SCHEME", cantoDAPAsset.getSchema());
    Assertions.assertEquals("IMAGE_ID", cantoDAPAsset.getId());


  }


  static String getImageJSON() throws IOException, URISyntaxException {
    Path path = Paths.get(Objects.requireNonNull(TestModelDataTransformation.class.getClassLoader()
                                                     .getResource("ExampleJson.json"))
                              .toURI());
    return Files.readString(path);
  }

}
