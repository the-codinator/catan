/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.util;

import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.user.User;

public class Util {

    public static boolean shouldSkipFilters(ContainerRequestContext request) {
        return request.getMethod().equals("OPTIONS") || request.getUriInfo().getPath().equals("ping")
            || request.getUriInfo().getPath().startsWith("swagger");
    }

    /**
     * Copied from {@link io.dropwizard.jersey.filter.RequestIdFilter}
     */
    public static UUID generateRandomUuid() {
        final Random rnd = ThreadLocalRandom.current();
        long mostSig = rnd.nextLong();
        long leastSig = rnd.nextLong();

        // Identify this as a version 4 UUID, that is one based on a random value.
        mostSig &= 0xffffffffffff0fffL;
        mostSig |= 0x0000000000004000L;

        // Set the variant identifier as specified for version 4 UUID values.  The two
        // high order bits of the lower word are required to be one and zero, respectively.
        leastSig &= 0x3fffffffffffffffL;
        leastSig |= 0x8000000000000000L;

        return new UUID(mostSig, leastSig);
    }

    public static String base64Encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String base64Decode(String str) {
        return new String(Base64.getDecoder().decode(str));
    }

    public static void validateInput(Object o) throws CatanException {
        if (o == null) {
            throw new CatanException("Invalid Input", Status.BAD_REQUEST);
        }
    }

    public static String getUserIdFromSecurityContext(SecurityContext sc) {
        return ((User) sc.getUserPrincipal()).getId();
    }
}
