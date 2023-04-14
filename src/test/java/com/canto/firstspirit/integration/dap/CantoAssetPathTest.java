package com.canto.firstspirit.integration.dap;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class CantoAssetPathTest {

	@Test
	void fromJson() {
		final CantoAssetPath path = CantoAssetPath.fromIdentifier("{\"schema\":\"my-schema\",\"id\":\"my-identifier\"}");

		assertThat(path).isNotNull();
		assertThat(path.getSchema()).isEqualTo("my-schema");
		assertThat(path.getId()).isEqualTo("my-identifier");
		assertThat(path.getPath()).isEqualTo("my-schema/my-identifier");
	}
}