/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.codi.catan.api.health.Health;
import org.codi.catan.api.health.Ping;
import org.codi.catan.api.misc.Favicon;
import org.codi.catan.core.CatanConfiguration;
import org.codi.catan.core.CatanConfigurationSourceProvider;
import org.codi.catan.core.GuiceDI;
import org.codi.catan.filter.RequestIdAndAccessLogFilter;
import org.codi.catan.filter.RequestTimingFilter;
import org.codi.catan.filter.ResponseLogAndTimingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends io.dropwizard.Application<CatanConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        logger.debug("--- Starting Application ---");
        new Application().run(args);
    }

    @Override
    public String getName() {
        return "catan-server";
    }

    @Override
    public void initialize(Bootstrap<CatanConfiguration> bootstrap) {
        logger.debug("[ BOOT ] Starting init");

        CatanConfigurationSourceProvider.setup(bootstrap);
        logger.debug("[ BOOT ] DropWizard ConfigSource configured");

        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(CatanConfiguration configuration) {
                return configuration.swagger;
            }
        });
        logger.debug("[ BOOT ] Swagger configured");

        GuiceDI.setup(bootstrap);
        logger.debug("[ BOOT ] Guice bindings configured");

        logger.info("[ BOOT ] Init complete");
    }

    @Override
    public void run(CatanConfiguration configuration, Environment environment) {
        // Health Check
        for (var hc : GuiceDI.getMulti(HealthCheck.class)) {
            environment.healthChecks().register(hc.getClass().getSimpleName().split("Health")[0], hc);
        }
        logger.debug("[ BOOT ] Health Check configured");

        // Filters
        environment.jersey().register(RequestTimingFilter.class);
        environment.jersey().register(RequestIdAndAccessLogFilter.class);
        environment.jersey().register(ResponseLogAndTimingFilter.class);
        logger.debug("[ BOOT ] Filters configured");

        // APIs
        environment.jersey().register(Ping.class);
        environment.jersey().register(Health.class);
        environment.jersey().register(Favicon.class);
        logger.debug("[ BOOT ] APIs configured");

        logger.info("[ BOOT ] Server ready!");
    }
}
