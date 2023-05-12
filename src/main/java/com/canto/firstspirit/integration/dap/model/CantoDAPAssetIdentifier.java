package com.canto.firstspirit.integration.dap.model;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;


/**
 * Handles Serialization and Deserialization of Canto DAP Asset Identifiers from and to JSON.
 * Identifiers must include schema and id to fetch asset from Canto
 * Identifiers may include additionalData, that is saved within a key-value String Map
 */
public class CantoDAPAssetIdentifier {

	private final String schema;
	private final String id;
	@NotNull
	private final Map<String, String> additionalData;

	/**
	 * Sets Default Value for Moshi
	 */
	@SuppressWarnings("unused")
	private CantoDAPAssetIdentifier() {
		additionalData = new HashMap<>();
		schema = "";
		id = "";
	}

	public CantoDAPAssetIdentifier(final String schema, final String id) {
		this(schema, id, null);
	}

	public CantoDAPAssetIdentifier(final String schema, final String id, @Nullable Map<String, String> additionalData) {
		this.schema = schema;
		this.id = id;
		this.additionalData = additionalData != null ? additionalData : new HashMap<>();
	}

	public @NotNull String getSchema() {
		return schema;
	}


	public @NotNull String getId() {
		return id;
	}

	public String getIdentifier() {
		final Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<CantoDAPAssetIdentifier> jsonAdapter = moshi.adapter(CantoDAPAssetIdentifier.class);

		return jsonAdapter.toJson(this);
	}


	public String getPath() {
		return schema + '/' + id;
	}

	public void setAdditionalData(@NotNull String name, @Nullable String value) {
		if(value != null) {
			this.additionalData.put(name, value);
		} else {
			this.additionalData.remove(name);
		}
	}

	public @Nullable String getAdditionalData(@NotNull String name) {
		return this.additionalData.get(name);
	}

	private static void verifyIdentifierValidity(CantoDAPAssetIdentifier dapIdentifier) {
		if(dapIdentifier == null
				|| dapIdentifier.id == null || dapIdentifier.id.isBlank()
				|| dapIdentifier.schema == null || dapIdentifier.schema.isBlank()) {
			throw new IllegalStateException("Identifier not correctly Initialized." +
					(dapIdentifier == null ?
							"CantoDAPAssetIdentifier itself is null"
							: "Id: [" + dapIdentifier.id + "], schema: [" + dapIdentifier.id + "]"));
		}
	}

	public static CantoDAPAssetIdentifier fromIdentifier(@NotNull final String identifier) {
		final Moshi moshi = new Moshi.Builder().build();
		final JsonAdapter<CantoDAPAssetIdentifier> jsonAdapter = moshi.adapter(CantoDAPAssetIdentifier.class).nonNull();
		try {
			CantoDAPAssetIdentifier cantoDAPAssetIdentifier = jsonAdapter.fromJson(identifier);
			verifyIdentifierValidity(cantoDAPAssetIdentifier);
			return cantoDAPAssetIdentifier;
		} catch (final Exception e) {
			throw new IllegalStateException("Error creating CantoDapAssetIdentifier from JSON: " + identifier, e);
		}
	}

}
