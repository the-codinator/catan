/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.impl.data;

import static org.codi.catan.util.Constants.DELEGATE;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.codi.catan.core.CatanCacheLoader;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;

public class CachedDelegateCDC extends DelegateCDC implements CatanDataConnector {

    private final LoadingCache<String, Board> boards;

    @Inject
    public CachedDelegateCDC(@Named(DELEGATE) CatanDataConnector delegate) {
        super(delegate);
        boards = CacheBuilder.newBuilder()
            .maximumSize(20)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(new CatanCacheLoader<>(delegate::getBoard));
    }

    @Override
    public Board getBoard(String id) throws CatanException {
        try {
            return boards.get(id);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof CatanException) {
                throw (CatanException) e.getCause();
            } else {
                throw new CatanException("Error getting Board with cache", e);
            }
        }
    }

    @Override
    public void createBoard(Board board) throws CatanException {
        super.createBoard(board);
        boards.put(board.getId(), board);
    }

    @Override
    public void deleteBoard(String id) throws CatanException {
        super.deleteBoard(id);
        boards.invalidate(id);
    }
}
