/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import static org.codi.catan.util.Constants.DELEGATE;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import io.dropwizard.setup.Environment;
import java.util.Collection;
import java.util.Set;
import org.codi.catan.impl.data.CachedDelegateCDC;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.data.DynamoDbCDC;
import org.codi.catan.impl.data.InMemoryCDC;
import org.codi.catan.impl.health.AwsDynamoDbHealthChecker;
import org.codi.catan.impl.health.InMemoryHealthChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiceDI extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(GuiceDI.class);

    private static Injector injector = null;
    private final ObjectMapper mapper;
    private final MetricRegistry metrics;
    private final CatanConfiguration configuration;

    private GuiceDI(Environment environment, CatanConfiguration configuration) {
        this.mapper = environment.getObjectMapper();
        this.metrics = environment.metrics();
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(ObjectMapper.class).toInstance(mapper);
        bind(MetricRegistry.class).toInstance(metrics);
        bind(CatanConfiguration.class).toInstance(configuration);
        Multibinder<HealthCheck> health = Multibinder.newSetBinder(binder(), HealthCheck.class);
        if (isAwsEnabled()) {
            health.addBinding().to(AwsDynamoDbHealthChecker.class);
            bind(CatanDataConnector.class).annotatedWith(Names.named(DELEGATE)).to(DynamoDbCDC.class);
        } else {
            logger.warn("AWS Credentials not found, using an In-Memory data store instead");
            health.addBinding().to(InMemoryHealthChecker.class);
            bind(CatanDataConnector.class).annotatedWith(Names.named(DELEGATE)).to(InMemoryCDC.class);
        }
        bind(CatanDataConnector.class).to(CachedDelegateCDC.class).asEagerSingleton();
    }

    private boolean isAwsEnabled() {
        String key = configuration.getDynamodb().getKey();
        return key != null && !key.isBlank() && !key.startsWith("$");
    }

    /**
     * Create an injector for DI using Google Guice
     * Fail on attempting to re-create
     */
    public static void setup(CatanConfiguration configuration, Environment environment) {
        if (injector == null) {
            synchronized (GuiceDI.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new GuiceDI(environment, configuration));
                    return;
                }
            }
        }
        throw new IllegalStateException("Attempted to re-initialize Guice DI");
    }

    public static <T> T get(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    public static <T> Collection<T> getMulti(Class<T> clazz) {
        Key<Set<T>> key = Key.get(setOf(clazz));
        return injector.getInstance(key);
    }

    @SuppressWarnings("unchecked")
    private static <T> TypeLiteral<Set<T>> setOf(Class<T> type) {
        return (TypeLiteral<Set<T>>) TypeLiteral.get(Types.setOf(type));
    }
}
