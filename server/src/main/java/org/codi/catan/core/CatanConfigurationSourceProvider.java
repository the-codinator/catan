package org.codi.catan.core;

import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;

public class CatanConfigurationSourceProvider extends SubstitutingSourceProvider {

    public CatanConfigurationSourceProvider() {
        super(new ResourceConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false));
    }

    public static void setup(Bootstrap<?> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new CatanConfigurationSourceProvider());
    }
}
