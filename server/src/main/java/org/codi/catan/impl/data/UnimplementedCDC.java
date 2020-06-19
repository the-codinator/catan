/*
 * @author the-codinator
 * created on 2020/6/19
 */

package org.codi.catan.impl.data;

import com.codahale.metrics.health.HealthCheck.Result;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;

public class UnimplementedCDC extends AbstractCDC implements CatanDataConnector {

    @Override
    public void init() throws CatanException {
        throw new CatanException("Provided Database type is not yet supported");
    }

    @Override
    public Result check() {
        return Result.unhealthy("Unsupported");
    }

    @Override
    protected <T extends IdentifiableEntity> T get(Class<T> clazz, String id, String etag) {
        return null;
    }

    @Override
    protected <T extends IdentifiableEntity> void create(Class<T> clazz, T value) {
    }

    @Override
    protected <T extends IdentifiableEntity> void update(Class<T> clazz, T value) {
    }

    @Override
    protected <T extends IdentifiableEntity> void put(Class<T> clazz, T value) {
    }

    @Override
    protected <T extends IdentifiableEntity> void delete(Class<T> clazz, String id) {
    }
}
