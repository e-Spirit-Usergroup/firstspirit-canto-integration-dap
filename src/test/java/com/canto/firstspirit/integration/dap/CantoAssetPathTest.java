package com.canto.firstspirit.integration.dap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;

class CantoAssetPathTest {

	@Test
	void fromJson() {
		final CantoAssetPath path = CantoAssetPath.fromIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		assertNotNull(path);
		Assertions.assertEquals("my-schema", path.getSchema());
		Assertions.assertEquals("my-identifier", path.getIdentifier());
		Assertions.assertEquals("my-schema/my-identifier", path.getPath());

	}
}