/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.api.misc;

import com.google.common.io.ByteStreams;
import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/favicon.ico")
@Produces("image/png")
public class Favicon {

    private static final Logger logger = LoggerFactory.getLogger(Favicon.class);
    private byte[] favicon = null;
    private boolean loaded;

    private synchronized void load() {
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("favicon.ico");
            favicon = ByteStreams.toByteArray(stream);
            loaded = true;
        } catch (Exception e) {
            logger.error("Could not load favicon", e);
        }
    }

    @GET
    public Response favicon() {
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
