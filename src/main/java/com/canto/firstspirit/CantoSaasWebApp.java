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
				@WebResource(name = "", version = "", targetPath = "/", path = "cantosaas/")
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
