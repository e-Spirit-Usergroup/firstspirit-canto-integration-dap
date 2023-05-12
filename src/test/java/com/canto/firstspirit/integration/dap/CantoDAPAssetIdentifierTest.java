package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAssetIdentifier;
import com.canto.firstspirit.service.server.model.CantoAssetIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;
class CantoDAPAssetIdentifierTest {

	@Test
	void fromJson() {
		final CantoDAPAssetIdentifier path = CantoDAPAssetIdentifier.fromIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		final CantoAssetIdentifier assetIdentifier = path.getAsCantoAssetIdentifier();

		assertNotNull(path);
		Assertions.assertEquals("my-schema", assetIdentifier.getSchema());
		Assertions.assertEquals("my-identifier", assetIdentifier.getId());
		Assertions.assertEquals("my-schema/my-identifier", assetIdentifier.getPath());
		Assertions.assertNull(path.getAdditionalData(""));
	}

	@Test
	void fromJsonWithNull() {
		Assertions.assertThrowsExactly(IllegalStateException.class, () -> CantoDAPAssetIdentifier.fromIdentifier("{\"schema\": null,\"id\": null}"));
	}


	@Test
	void additionalDataSerialization() {
		final CantoDAPAssetIdentifier identifier = CantoDAPAssetIdentifier.fromIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		identifier.setAdditionalData("test", "abc");

		Assertions.assertEquals("abc", CantoDAPAssetIdentifier.fromIdentifier(identifier.getIdentifier()).getAdditionalData("test"));

	}
}