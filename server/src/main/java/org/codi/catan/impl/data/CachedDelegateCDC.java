/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import static org.codi.catan.util.Constants.DELEGATE;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.name.Named;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.codi.catan.core.CatanCacheLoader;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.Token;

public class CachedDelegateCDC extends DelegateCDC implements CatanDataConnector {

    private final LoadingCache<String, Token> tokens;

    @Inject
    public CachedDelegateCDC(@Named(DELEGATE) CatanDataConnector delegate) {
        super(delegate);
        tokens = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CatanCacheLoader<>(delegate::getToken));
    }

    @Override
    public Token getToken(String id) throws CatanException {
        try {
            return tokens.get(id);
        } catch (ExecutionException e) {
            throw new CatanException("Error loading token with cache", e);
        }
    }

    @Override
    public void createToken(Token token) {
        tokens.put(token.getId(), token);
    }

    @Override
    public void deleteToken(String id) {
        tokens.invalidate(id);
    }
}
