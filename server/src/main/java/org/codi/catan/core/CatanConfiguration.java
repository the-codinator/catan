/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import lombok.Getter;
import org.codi.catan.model.misc.DynamoDbCreds;

public class CatanConfiguration extends Configuration {

    @Getter
    private SwaggerBundleConfiguration swagger;

    @Getter
    private DynamoDbCreds dynamodb;
}
