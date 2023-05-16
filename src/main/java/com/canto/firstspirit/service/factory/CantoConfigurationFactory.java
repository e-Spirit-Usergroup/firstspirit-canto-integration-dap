package com.canto.firstspirit.service.factory;

import com.canto.firstspirit.config.CantoProjectApp;
import com.canto.firstspirit.config.CantoProjectAppConfiguration;
import com.canto.firstspirit.service.server.model.CantoConfiguration;
import com.espirit.ps.psci.genericconfiguration.Values;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import org.jetbrains.annotations.NotNull;

public class CantoConfigurationFactory {
    /**
     * Get a configuration from a broker with project binding
     * @param broker project bound broker
     * @return Canto Configuration based on ProjectApp Config
     */
    @NotNull
    public static CantoConfiguration fromProjectBroker(SpecialistsBroker broker){
        Values config = CantoProjectApp.getConfig(broker);
        String tenant = config.getString(CantoProjectAppConfiguration.PARAM_TENANT);
        String token = config.getString(CantoProjectAppConfiguration.PARAM_TOKEN);
        return new CantoConfiguration(tenant,token);
    }

}
