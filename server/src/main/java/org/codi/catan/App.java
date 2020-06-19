/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.codi.catan.api.admin.AdminApi;
import org.codi.catan.api.admin.InMemDbResetApi;
import org.codi.catan.api.game.BoardApi;
import org.codi.catan.api.game.DevCardApi;
import org.codi.catan.api.game.MoveApi;
import org.codi.catan.api.game.TradeApi;
import org.codi.catan.api.health.Health;
import org.codi.catan.api.health.Ping;
import org.codi.catan.api.misc.Favicon;
import org.codi.catan.api.user.UserApi;
import org.codi.catan.core.CatanConfiguration;
import org.codi.catan.core.CatanConfigurationSourceProvider;
import org.codi.catan.core.CatanException;
import org.codi.catan.core.CatanExceptionMapper;
import org.codi.catan.core.GuiceDI;
import org.codi.catan.filter.CatanAuthFilter;
import org.codi.catan.filter.ETagHeaderFilter;
import org.codi.catan.filter.RequestIdAndAccessLogFilter;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.data.DatabaseType;
import org.codi.catan.model.user.User;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application<CatanConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private Environment environment;

    public static void main(String[] args) throws Exception {
        logger.debug("--- Starting Application ---");
        new App().run(args);
    }

    @Override
    public String getName() {
        return "catan-server";
    }

    @Override
    public void initialize(Bootstrap<CatanConfiguration> bootstrap) {
        logger.debug("[ BOOT ] Starting init");

        // Config Source
        CatanConfigurationSourceProvider.setup(bootstrap);
        logger.debug("[ BOOT ] DropWizard ConfigSource configured");

        // Swagger
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(CatanConfiguration configuration) {
                return configuration.getSwagger();
            }
        });
        logger.debug("[ BOOT ] Swagger configured");

        logger.info("[ BOOT ] Init complete");
    }

    @Override
    public void run(CatanConfiguration configuration, Environment environment) {
        this.environment = environment;

        // Guice DI
        GuiceDI.setup(configuration, environment);
        logger.debug("[ BOOT ] Guice bindings configured");

        // Database
        try {
            GuiceDI.get(CatanDataConnector.class).init();
        } catch (CatanException e) {
            throw new RuntimeException("Failed to initialize DB", e);
        }
        logger.debug("[ BOOT ] Database initialized");

        // Exception Mapper
        registerJerseyDI(CatanExceptionMapper.class);
        logger.debug("[ BOOT ] Default ErrorHandlers configured");

        // Health Check
        for (var hc : GuiceDI.getMulti(HealthCheck.class)) {
            environment.healthChecks().register(hc.getClass().getSimpleName().split("Health")[0], hc);
        }
        logger.debug("[ BOOT ] Health Checks configured");

        // Filters
        registerJerseyDI(ETagHeaderFilter.class);
        registerJerseyDI(RequestIdAndAccessLogFilter.class);
        logger.debug("[ BOOT ] Filters configured");

        // Auth
        environment.jersey().register(new AuthDynamicFeature(GuiceDI.get(CatanAuthFilter.class)));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        logger.debug("[ BOOT ] Auth configured");

        // APIs
        // Core
        registerJerseyDI(Ping.class);
        registerJerseyDI(Health.class);
        registerJerseyDI(Favicon.class);
        // Admin
        registerJerseyDI(AdminApi.class);
        if (configuration.getDatabase().getType() == DatabaseType.inMemory) {
            registerJerseyDI(InMemDbResetApi.class);
        }
        // User
        registerJerseyDI(UserApi.class);
        // Game
        registerJerseyDI(BoardApi.class);
        registerJerseyDI(MoveApi.class);
        registerJerseyDI(DevCardApi.class);
        registerJerseyDI(TradeApi.class);
        logger.debug("[ BOOT ] APIs configured");

        logger.info("[ BOOT ] Application Configured!");
    }

    private void registerJerseyDI(Class<?> clazz) {
        environment.jersey().register(GuiceDI.get(clazz));
    }
}
