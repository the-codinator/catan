/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.health;

import com.codahale.metrics.health.HealthCheck;

public class InMemoryHealthChecker extends HealthCheck {

    @Override
    protected Result check() {
        return Result.healthy();
    }
}
