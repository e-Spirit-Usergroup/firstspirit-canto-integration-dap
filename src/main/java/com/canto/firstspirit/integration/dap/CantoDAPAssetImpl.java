package com.canto.firstspirit.integration.dap;

public class CantoDAPAssetImpl implements CantoDAPAsset {


    private final String _title;
    private final String _thumbnailUrl;
    private final String _previewUrl;
    private final String _description;
    private final CantoAssetPath _path;
    private final String _mdc_rendition_baseurl;
    private final String _mdc_asset_baseurl;



    public CantoDAPAssetImpl(final String schema, String identifier, String title, String thumbnailUrl, String previewUrl, String description, String mdc_rendition_baseurl, String mdc_asset_baseurl) {
        _path = new CantoAssetPath(schema, identifier);
        _title = title;
        _thumbnailUrl = thumbnailUrl;
        _previewUrl = previewUrl;
        _description = description;
        _mdc_rendition_baseurl = mdc_rendition_baseurl;
        _mdc_asset_baseurl = mdc_asset_baseurl;
    }

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public String getIdentifier() {
        return _path.getIdentifier();
    }

    @Override
    public String getPath() {
        return _path.getPath();
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public String getThumbnailUrl() {
        return _thumbnailUrl;
    }

    @Override
    public String getPreviewUrl() {
        return _previewUrl;
    }
    @Override
    public String getMDCRenditionBaseUrl() {
        return _mdc_rendition_baseurl;
    }
    @Override
    public String getMDCAssetBaseUrl() {
        return _mdc_asset_baseurl;
    }
}
