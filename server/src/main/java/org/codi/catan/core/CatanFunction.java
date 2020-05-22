/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

@FunctionalInterface
public interface CatanFunction<K, V> {

    V apply(K k) throws CatanException;
}
