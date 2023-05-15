package com.canto.firstspirit.service;

import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


/**
 * Handles Serialization and Deserialization of Canto DAP Asset Identifiers from and to JSON.
 * Identifiers must include schema and id to fetch asset from Canto
 * Identifiers may include additionalData, that is saved within a key-value String Map
 */
public class CantoAssetIdentifierSerializer implements Serializable {
	static final Moshi moshi = new Moshi.Builder().build();
	static JsonAdapter<CantoAssetIdentifier> jsonAdapter = moshi.adapter(CantoAssetIdentifier.class);


	public static String toJsonIdentifier(CantoAssetIdentifier cantoAssetIdentifier) {
		return jsonAdapter.toJson(cantoAssetIdentifier);
	}

	@SuppressWarnings("")
	private static void verifyIdentifierValidity(CantoAssetIdentifier cantoAssetIdentifier) {
		if(cantoAssetIdentifier == null
				|| cantoAssetIdentifier.getId() == null || cantoAssetIdentifier.getId().isBlank()
				|| cantoAssetIdentifier.getSchema() == null || cantoAssetIdentifier.getSchema().isBlank()) {
			throw new IllegalStateException("Identifier not correctly Initialized." +
					(cantoAssetIdentifier == null ?
							"CantoDAPAssetIdentifier itself is null"
							: "Id: [" + cantoAssetIdentifier.getId() + "], schema: [" + cantoAssetIdentifier.getSchema() + "]"));
		}
	}

	public static CantoAssetIdentifier fromJsonIdentifier(@NotNull final String identifier) {
		try {
			CantoAssetIdentifier cantoAssetIdentifier = jsonAdapter.fromJson(identifier);
			verifyIdentifierValidity(cantoAssetIdentifier);
			return cantoAssetIdentifier;
		} catch (final Exception e) {
			throw new IllegalStateException("Error creating CantoDapAssetIdentifier from JSON: " + identifier, e);
		}
	}

}
