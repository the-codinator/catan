package org.codi.catan.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceDI extends AbstractModule {

    private static Injector injector = null;

    @Override
    protected void configure() {
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
}
