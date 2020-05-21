package org.codi.catan.impl.health;

import com.codahale.metrics.health.HealthCheck;
import org.codi.catan.core.CatanException;

public class AwsDynamoDbHealthChecker extends HealthCheck {

    public Result check() throws CatanException {
        try {
            if (false) {
                return Result.unhealthy("AWS DynamoDB Health Check Failed");
            }
            return Result.healthy();
        } catch (Exception e) {
            throw new CatanException("AWS DynamoDB Health Check Errored", e);
        }
    }
}
