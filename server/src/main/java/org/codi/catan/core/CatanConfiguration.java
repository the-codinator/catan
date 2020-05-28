/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import com.github.benmanes.caffeine.cache.CaffeineSpec;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Getter;
import org.codi.catan.model.core.DynamoDbCreds;

@Getter
public class CatanConfiguration extends Configuration {

    private SwaggerBundleConfiguration swagger;
    private DynamoDbCreds dynamodb;
    private CaffeineSpec authenticationCachePolicy;
}
