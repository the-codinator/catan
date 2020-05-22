/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import static org.codi.catan.util.Constants.DELEGATE;

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
import io.dropwizard.setup.Bootstrap;
import java.util.Collection;
import java.util.Set;
import org.codi.catan.impl.data.CachedDelegateCDC;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.data.DynamoDbCDC;
import org.codi.catan.impl.data.ImMemoryCDC;
import org.codi.catan.impl.health.AwsDynamoDbHealthChecker;
import org.codi.catan.impl.health.InMemoryHealthChecker;

public class GuiceDI extends AbstractModule {

    private static Injector injector = null;
    private final ObjectMapper mapper;

    public GuiceDI(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void configure() {
        bind(ObjectMapper.class).toInstance(mapper);
        Multibinder<HealthCheck> health = Multibinder.newSetBinder(binder(), HealthCheck.class);
        bind(CatanDataConnector.class).to(CachedDelegateCDC.class);
        if (isAwsEnabled()) {
            health.addBinding().to(AwsDynamoDbHealthChecker.class);
            bind(CatanDataConnector.class).annotatedWith(Names.named(DELEGATE)).to(DynamoDbCDC.class);
        } else {
            health.addBinding().to(InMemoryHealthChecker.class);
            bind(CatanDataConnector.class).annotatedWith(Names.named(DELEGATE)).to(ImMemoryCDC.class);
        }
    }

    private boolean isAwsEnabled() {
        return false;
    }

    public static void setup(Bootstrap<CatanConfiguration> bootstrap) {
        if (injector == null) {
            synchronized (GuiceDI.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new GuiceDI(bootstrap.getObjectMapper()));
                }
            }
        }
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
