package org.codi.catan;

import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.codi.catan.api.health.Ping;
import org.codi.catan.api.misc.Favicon;
import org.codi.catan.core.CatanConfiguration;
import org.codi.catan.core.CatanConfigurationSourceProvider;
import org.codi.catan.core.GuiceDI;
import org.codi.catan.filter.RequestIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends io.dropwizard.Application<CatanConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) throws Exception {
        logger.debug("--- Starting Application ---");
        new Application().run(args);
    }

    @Override
    public String getName() {
        return "catan-server";
    }

    @Override
    public void initialize(final Bootstrap<CatanConfiguration> bootstrap) {
        logger.debug("[ BOOT ] Starting init");

        CatanConfigurationSourceProvider.setup(bootstrap);

        GuiceDI.setup();
        logger.debug("[ BOOT ] Guice Bindings Configured");

        logger.info("[ BOOT ] Init complete");
    }

    @Override
    public void run(final CatanConfiguration configuration, final Environment environment) {
        // Filters
        environment.jersey().register(RequestIdFilter.class);

        // APIs
        environment.jersey().register(GuiceDI.get(Ping.class));
        environment.jersey().register(GuiceDI.get(Favicon.class));
    }
}
