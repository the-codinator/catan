/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.util;

import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import javax.ws.rs.container.ContainerRequestContext;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;

public class Util {

    public static boolean shouldSkipFilters(ContainerRequestContext request) {
        return request.getMethod().equals("OPTIONS") || request.getUriInfo().getPath().equals("ping")
            || request.getUriInfo().getPath().startsWith("swagger");
    }

    /**
     * Copied from {@link io.dropwizard.jersey.filter.RequestIdFilter}
     */
    public static String generateRandomUuid() {
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

        return new UUID(mostSig, leastSig).toString();
    }

    public static String base64Encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String base64Decode(String str) {
        return new String(Base64.getDecoder().decode(str));
    }

    public static void validateInput(Object o) throws CatanException {
        if (o == null) {
            throw new BadRequestException("Some input fields are missing");
        }
    }

    public static boolean isOkStatus(int status) {
        return status / 100 == 2;
    }

    /**
     * Update the count of a [key -> count] frequency map by adding delta
     * Seed count value = 0 for missing keys
     */
    public static <T> void addToFrequencyMap(Map<T, Integer> map, T key, int delta) {
        map.merge(key, delta, Integer::sum);
    }

    /**
     * Search utility
     *
     * @return element in {@param iterable} matching {@param predicate}
     */
    public static <T> T find(Iterable<T> iterable, Predicate<T> predicate) {
        if (iterable != null) {
            for (T t : iterable) {
                if (predicate.test(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Search utility
     *
     * @return element in {@param iterable} identified by {@param id}
     */
    public static <T extends IdentifiableEntity> T find(Iterable<T> iterable, String id) {
        return id == null ? null : find(iterable, t -> id.equals(t.getId()));
    }

    /**
     * Search utility
     *
     * @return index of element in {@param arr} matching {@param predicate}
     */
    public static <T> int find(T[] arr, Predicate<T> predicate) {
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                if (predicate.test(arr[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Search utility
     *
     * @return index of element in {@param arr} identified by {@param id}
     */
    public static <T extends IdentifiableEntity> int find(T[] arr, String id) {
        return id == null ? -1 : find(arr, t -> id.equals(t.getId()));
    }

    /**
     * Count utility
     *
     * @return number of elements in {@param iterable} matching {@param predicate}
     */
    public static <T> int count(Iterable<T> iterable, Predicate<T> predicate) {
        int count = 0;
        if (iterable != null) {
            for (T t : iterable) {
                if (predicate.test(t)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Search utility
     *
     * @return element in {@param arr} matching {@param predicate}
     */
    public static <T> int count(T[] arr, Predicate<T> predicate) {
        int count = 0;
        if (arr != null) {
            for (T t : arr) {
                if (predicate.test(t)) {
                    count++;
                }
            }
        }
        return count;
    }

    public static <T extends Enum<T>> EnumMap<T, Integer> arrayToEnumMap(Class<T> clazz, T... arr) {
        EnumMap<T, Integer> map = new EnumMap<>(clazz);
        for (T t : arr) {
            map.put(t, 1 + map.getOrDefault(t, 0));
        }
        return map;
    }
}
