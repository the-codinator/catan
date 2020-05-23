/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.api.misc;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;

@Path("/favicon.ico")
@Produces("image/png")
public class Favicon {

    private byte[] favicon = null;
    private boolean loaded;

    private synchronized void load() throws CatanException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("favicon.ico");
        if (stream == null) {
            throw new CatanException("Could not find/load favicon", Status.NOT_FOUND);
        }
        try {
            favicon = ByteStreams.toByteArray(stream);
        } catch (IOException e) {
            throw new CatanException("Failed to parse favicon", e);
        }
        loaded = true;
    }

    @GET
    public Response favicon() throws CatanException {
        if (!loaded) {
            load();
        }
        if (favicon != null) {
            return Response.ok(favicon).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
