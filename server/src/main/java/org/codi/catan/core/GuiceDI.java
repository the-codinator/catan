package org.codi.catan.core;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.codi.catan.impl.health.AwsDynamoDbHealthChecker;

public class GuiceDI extends AbstractModule {

    private static Injector injector = null;

    @Override
    protected void configure() {
        Multibinder<HealthCheck> health = Multibinder.newSetBinder(binder(), HealthCheck.class);
        health.addBinding().to(AwsDynamoDbHealthChecker.class);
    }

    public static void setup() {
        if (injector == null) {
            synchronized (GuiceDI.class) {
                if (injector == null) {
                    injector = Guice.createInjector(new GuiceDI());
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
