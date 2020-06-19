/*
 * @author the-codinator
 * created on 2020/6/12
 */

package org.codi.catan.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.lang.reflect.Field;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.codi.catan.impl.data.CatanDataConnector;
import org.codi.catan.impl.data.DelegateCDC;
import org.codi.catan.impl.data.InMemoryCDC;

@Api(AdminApi.API_GROUP)
@Path("reset")
public class InMemDbResetApi {

    private final InMemoryCDC imcdc;

    @Inject
    public InMemDbResetApi(CatanDataConnector dataConnector) {
        if (dataConnector instanceof DelegateCDC) {
            try {
                Field field = DelegateCDC.class.getDeclaredField("delegate");
                field.setAccessible(true);
                imcdc = (InMemoryCDC) field.get(dataConnector);
            } catch (Exception e) {
                throw new IllegalStateException("Error locating InMemory DB", e);
            }
        } else {
            imcdc = (InMemoryCDC) dataConnector;
        }
    }

    @GET
    @ApiOperation("In Memory Local Dev Only")
    public String get() {
        imcdc.reset();
        return "OK";
    }
}
