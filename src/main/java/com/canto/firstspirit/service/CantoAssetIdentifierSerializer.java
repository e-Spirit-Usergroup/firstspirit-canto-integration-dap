package com.canto.firstspirit.service;

import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


/**
 * Handles Serialization and Deserialization of {@link CantoAssetIdentifier} from and to JSON.
 * Identifiers must include schema and id to fetch asset from Canto
 * Identifiers may include additionalData, that is saved within a key-value String Map
 * The {@link com.canto.firstspirit.integration.dap.CantoDAP} uses the stringified JSON as id for its FS_INDEX Elements
 */
public class CantoAssetIdentifierSerializer implements Serializable {
	static final Moshi moshi = new Moshi.Builder().build();
	static JsonAdapter<CantoAssetIdentifier> jsonAdapter = moshi.adapter(CantoAssetIdentifier.class);

	/**
	 * Generate stringified JSON of given Asset. Used as Identifier of Canto DAP
	 * @param cantoAssetIdentifier Asset to convert to JSON
	 * @return stringified JSON
	 */
	public static String toJsonIdentifier(CantoAssetIdentifier cantoAssetIdentifier) {
		return jsonAdapter.toJson(cantoAssetIdentifier);
	}

	/**
	 * Verifies that id and schema are Set for a CantoAsset Identifier.
	 * Used after parsing JSON to CantoAssetIdentifier to ensure validity.
	 * Throws IllegalStateException if invalid
	 * @param cantoAssetIdentifier identifier to check
	 */
	private static void verifyIdentifierValidity(CantoAssetIdentifier cantoAssetIdentifier) {
		//noinspection ConstantValue
		if(cantoAssetIdentifier == null
				|| cantoAssetIdentifier.getId() == null || cantoAssetIdentifier.getId().isBlank()
				|| cantoAssetIdentifier.getSchema() == null || cantoAssetIdentifier.getSchema().isBlank()) {
			throw new IllegalStateException("Identifier not correctly Initialized." +
					(cantoAssetIdentifier == null ?
							"CantoDAPAssetIdentifier itself is null"
							: "Id: [" + cantoAssetIdentifier.getId() + "], schema: [" + cantoAssetIdentifier.getSchema() + "]"));
		}
	}

	/**
	 * Creates CantoAssetIdentifier based on stringified JSON. JSON must include the
	 * JSON must include schema and id
	 * JSON may include additionalData Object
	 * If schema or id is missing in created AssetId, IllegalStateException is thrown
	 * @param identifier json string with schema, id and optional additionalData
	 * @return CantoAssetIdentifier created based on JSON
	 */
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
