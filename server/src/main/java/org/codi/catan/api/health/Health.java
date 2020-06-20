/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.api.health;

import static org.codi.catan.util.Constants.API_HEALTH_AND_PING;
import static org.codi.catan.util.Constants.PATH_HEALTH;

import com.codahale.metrics.health.HealthCheck.Result;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.swagger.annotations.Api;
import java.util.Map.Entry;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(API_HEALTH_AND_PING)
@Path(PATH_HEALTH)
@Produces(MediaType.APPLICATION_JSON)
public class Health {

    private final HealthCheckRegistry registry;

    @Inject
    public Health(HealthCheckRegistry registry) {
        this.registry = registry;
    }

    @GET
    public Set<Entry<String, Result>> health() {
        return registry.runHealthChecks().entrySet();
    }
}
