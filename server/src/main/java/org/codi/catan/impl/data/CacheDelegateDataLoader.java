package org.codi.catan.impl.data;

import static org.codi.catan.util.Constants.DELEGATE;

import com.google.inject.name.Named;
import javax.inject.Inject;

public class CacheDelegateDataLoader implements CatanDataLoader {

    private final CatanDataLoader delegate;

    @Inject
    public CacheDelegateDataLoader(@Named(DELEGATE) CatanDataLoader delegate) {
        this.delegate = delegate;
    }
}
