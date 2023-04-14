package com.canto.firstspirit;

import de.espirit.firstspirit.module.WebApp;
import de.espirit.firstspirit.module.WebEnvironment;
import de.espirit.firstspirit.module.descriptor.WebAppDescriptor;

import com.espirit.moddev.components.annotations.WebAppComponent;
import com.espirit.moddev.components.annotations.WebResource;


@SuppressWarnings("unused")
@WebAppComponent(
		name = "Canto SaaS WebApp",
		description = "Web component for Canto SaaS Connector",
		webXml = "WEB-INF/web.xml",
		webResources = {
				@WebResource(name = "", version = "", targetPath = "/", path = "cantosaas/"),
				@WebResource(name = "com.canto:${project.name}-cantosaas", version = "${project.version}", path = "cantosaas"),
				@WebResource(name = "com.espirit.ps.psci.module:generic-configuration", version = "2.8.0", path = "lib/generic-configuration-2.8.0.jar"),
				@WebResource(name = "com.espirit.ps.psci.module:magic-icons", version = "2.1.0", minVersion = "2.1.0", path = "lib/magic-icons-2.1.0.jar"),
				@WebResource(name = "com.squareup.okhttp3:okhttp", version = "4.1.0", minVersion = "4.1.0", path = "lib/okhttp-4.1.0.jar"),
				@WebResource(name = "com.squareup.moshi:moshi", version = "1.8.0", minVersion = "1.8.0", path = "lib/moshi-1.8.0.jar"),
				@WebResource(name = "net.java.dev.designgridlayout:designgridlayout", version = "1.11", minVersion = "1.11", path = "lib/designgridlayout-1.11.jar"),
				@WebResource(name = "com.squareup.okio:okio", version = "2.2.2", minVersion = "2.2.2", path = "lib/okio-2.2.2.jar"),
				@WebResource(name = "org.jetbrains.kotlin:kotlin-stdlib", version = "1.3.41", minVersion = "1.3.41", path = "lib/kotlin-stdlib-1.3.41.jar"),
				@WebResource(name = "org.jetbrains.kotlin:kotlin-stdlib-common", version = "1.3.41", minVersion = "1.3.41", path = "lib/kotlin-stdlib-common-1.3.41.jar"),
				@WebResource(name = "org.jetbrains:annotations", version = "13.0", minVersion = "13.0", path = "lib/annotations-13.0.jar")
		}
)
public class CantoSaasWebApp implements WebApp {

	@Override
	public void createWar() {
		// stub
	}


	@Override
	public void init(final WebAppDescriptor webAppDescriptor, final WebEnvironment webEnvironment) {
		// stub
	}


	@Override
	public void installed() {
		// stub
	}


	@Override
	public void uninstalling() {
		// stub
	}


	@Override
	public void updated(final String s) {
		// stub
	}
}
