package com.canto.firstspirit.integration.dap;

import com.canto.firstspirit.integration.dap.model.CantoDAPAssetIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;
class CantoDAPAssetIdentifierTest {

	@Test
	void fromJson() {
		final CantoDAPAssetIdentifier path = CantoDAPAssetIdentifier.fromIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		assertNotNull(path);
		Assertions.assertEquals("my-schema", path.getSchema());
		Assertions.assertEquals("my-identifier", path.getId());
		Assertions.assertEquals("my-schema/my-identifier", path.getPath());
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