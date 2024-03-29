/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import com.google.common.cache.CacheLoader;
import javax.annotation.ParametersAreNonnullByDefault;

public class CatanCacheLoader<K, V> extends CacheLoader<K, V> {

    private final CatanFunction<K, V> computingFunction;

    public CatanCacheLoader(CatanFunction<K, V> function) {
        super();
        this.computingFunction = function;
    }

    @Override
    @ParametersAreNonnullByDefault
    public V load(K key) throws CatanException {
        return computingFunction.apply(key);
    }
}
