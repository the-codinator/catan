/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import static org.codi.catan.util.Constants.ENTITY_CONFLICT;
import static org.codi.catan.util.Constants.ENTITY_NOT_FOUND;
import static org.codi.catan.util.Constants.ENTITY_PRECONDITION_FAILED;

import com.codahale.metrics.health.HealthCheck.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.core.IdentifiableEntity;
import org.codi.catan.model.core.StrongEntity;
import org.codi.catan.util.Util;

public class InMemoryCDC extends AbstractCDC implements CatanDataConnector {

    private final ObjectMapper objectMapper;
    private final LoadingCache<Class<? extends IdentifiableEntity>, Map<String, String>> db;

    @Inject
    public InMemoryCDC(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        db = CacheBuilder.newBuilder().build(CacheLoader.from((Supplier<Map<String, String>>) ConcurrentHashMap::new));
    }

    @Override
    public void init() {
        logger.warn("[ DB ] Application is using non-persistent In-Memory Database");
    }

    @Override
    public Result check() {
        return Result.healthy();
    }

    public void reset() {
        db.invalidateAll();
    }

    private String generateETag(String serialized) {
        // Make something that can easily be treated as opaque, but is a consistent algorithm (idempotent)
        return serialized == null ? null : Util.base64Encode(String.valueOf(serialized.hashCode()));
    }

    @Override
    protected <T extends IdentifiableEntity> T get(Class<T> clazz, String id, String etag) throws CatanException {
        try {
            String serialized = db.get(clazz).get(id);
            if (serialized == null) {
                throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), id), Status.NOT_FOUND);
            }
            T value = objectMapper.readValue(serialized, clazz);
            if (StrongEntity.class.isAssignableFrom(clazz)) {
                StrongEntity seValue = (StrongEntity) value;
                String generatedETag = generateETag(serialized);
                if (generatedETag.equals(etag)) {
                    value = null;
                } else {
                    seValue.setETag(generatedETag);
                }
            }
            return value;
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    @Override
    protected <T extends IdentifiableEntity> void create(Class<T> clazz, T value) throws CatanException {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            String oldValue = db.get(clazz).putIfAbsent(value.getId(), serialized);
            if (oldValue == null) {
                if (StrongEntity.class.isAssignableFrom(clazz)) {
                    ((StrongEntity) value).setETag(generateETag(serialized));
                }
            } else {
                throw new CatanException(String.format(ENTITY_CONFLICT, clazz.getSimpleName(), value.getId()),
                    Status.CONFLICT);
            }
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    @Override
    protected <T extends IdentifiableEntity> void update(Class<T> clazz, T value) throws CatanException {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            if (StrongEntity.class.isAssignableFrom(clazz)) {
                StrongEntity seValue = (StrongEntity) value;
                if (seValue.getETag() != null) {
                    String result = db.get(clazz).computeIfPresent(value.getId(), (k, oldValue) -> {
                        String oldEtag = generateETag(oldValue);
                        if (seValue.getETag().equals(oldEtag)) {
                            seValue.setETag(generateETag(serialized));
                            return serialized;
                        } else {
                            return oldValue;
                        }
                    });
                    if (result == null) {
                        throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), value.getId()),
                            Status.NOT_FOUND);
                    }
                    //noinspection StringEquality
                    if (result != serialized) {
                        throw new CatanException(
                            String.format(ENTITY_PRECONDITION_FAILED, clazz.getSimpleName(), value.getId()),
                            Status.PRECONDITION_FAILED);
                    }
                    // result == serialized
                    return;
                }
            }
            // Other cases
            if (db.get(clazz).replace(value.getId(), serialized) == null) {
                throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), value.getId()),
                    Status.NOT_FOUND);
            }
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    @Override
    protected <T extends IdentifiableEntity> void put(Class<T> clazz, T value) throws CatanException {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            db.get(clazz).put(value.getId(), serialized);
        } catch (ExecutionException | JsonProcessingException e) {
            throw new CatanException("DB error", e);
        }
    }

    @Override
    protected <T extends IdentifiableEntity> void delete(Class<T> clazz, String id) throws CatanException {
        try {
            if (db.get(clazz).remove(id) == null) {
                throw new CatanException(String.format(ENTITY_NOT_FOUND, clazz.getSimpleName(), id), Status.NOT_FOUND);
            }
        } catch (ExecutionException e) {
            throw new CatanException("DB error", e);
        }
    }
}
