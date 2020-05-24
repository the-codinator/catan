/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import static org.codi.catan.util.Constants.DELEGATE;

import com.google.inject.name.Named;
import javax.inject.Inject;

public class CachedDelegateCDC extends DelegateCDC implements CatanDataConnector {

    // private final LoadingCache<String, Token> tokens;

    @Inject
    public CachedDelegateCDC(@Named(DELEGATE) CatanDataConnector delegate) {
        super(delegate);
        // tokens = CacheBuilder.newBuilder()
        //     .maximumSize(100)
        //     .expireAfterWrite(10, TimeUnit.MINUTES)
        //     .build(new CatanCacheLoader<>(delegate::getToken));
    }
}
