/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.impl.auth;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.auth.CachingAuthenticator;
import javax.inject.Inject;
import org.codi.catan.core.CatanConfiguration;
import org.codi.catan.model.user.Token;
import org.codi.catan.model.user.User;

public class CachingCatanAuthenticator extends CachingAuthenticator<Token, User> {

    @Inject
    public CachingCatanAuthenticator(MetricRegistry metricRegistry, CatanAuthenticator authenticator,
        CatanConfiguration configuration) {
        super(metricRegistry, authenticator, configuration.getAuthenticationCachePolicy());
    }
}
