package com.canto.firstspirit.integration.dap.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


public class CantoAssetPath {

	private final String schema;
	private final String id;


	public CantoAssetPath(final String schema, final String id) {
		this.schema = schema;
		this.id = id;
	}

	public String getSchema() {
		return schema;
	}


	public String getId() {
		return id;
	}

	public String getIdentifier() {
		return "{\"schema\":\"" + schema + "\",\"id\":\"" + id + "\"}";
	}


	public String getPath() {
		return schema + '/' + id;
	}


	@Nullable
	public static CantoAssetPath fromIdentifier(@NotNull final String identifier) {
		final Moshi moshi = new Moshi.Builder().build();
		final JsonAdapter<CantoAssetPath> jsonAdapter = moshi.adapter(CantoAssetPath.class);
		try {
			return jsonAdapter.fromJson(identifier);
		} catch (final IOException e) {
			return null;
		}
	}

}
