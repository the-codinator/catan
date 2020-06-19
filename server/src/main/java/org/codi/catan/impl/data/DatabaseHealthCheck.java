/*
 * @author the-codinator
 * created on 2020/6/19
 */

package org.codi.catan.impl.data;

import com.codahale.metrics.health.HealthCheck;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseHealthCheck extends HealthCheck {

    private final CatanDataConnector dataConnector;

    @Inject
    public DatabaseHealthCheck(CatanDataConnector dataConnector) {
        this.dataConnector = dataConnector;
    }

    @Override
    protected Result check() throws Exception {
        return dataConnector.check();
    }
}
