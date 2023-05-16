package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.service.factory.CantoAssetIdentifierSerializer;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;
class CantoAssetIdentifierUtilsTest {

	@Test
	void fromJson() {
		final CantoAssetIdentifier identifier = CantoAssetIdentifierSerializer.fromJsonIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		final CantoAssetIdentifier assetIdentifier = CantoAssetIdentifierSerializer.fromJsonIdentifier(CantoAssetIdentifierSerializer.toJsonIdentifier(identifier));

		assertNotNull(identifier);
		Assertions.assertEquals("my-schema", assetIdentifier.getSchema());
		Assertions.assertEquals("my-identifier", assetIdentifier.getId());
		Assertions.assertEquals("my-schema/my-identifier", assetIdentifier.getPath());
		Assertions.assertNull(identifier.getAdditionalData(""));
	}

	@Test
	void fromJsonWithNull() {
		Assertions.assertThrowsExactly(IllegalStateException.class, () -> CantoAssetIdentifierSerializer.fromJsonIdentifier("{\"schema\": null,\"id\": null}"));
	}


	@Test
	void additionalDataSerialization() {
		final CantoAssetIdentifier identifier = CantoAssetIdentifierSerializer.fromJsonIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		identifier.setAdditionalData("test", "abc");
		CantoAssetIdentifier deserializedIdentifier = CantoAssetIdentifierSerializer.fromJsonIdentifier(
				CantoAssetIdentifierSerializer.toJsonIdentifier(identifier));

		Assertions.assertEquals("abc", deserializedIdentifier.getAdditionalData("test"));

	}
}