/*
 * @author the-codinator
 * created on 2020/5/22
 */

package org.codi.catan.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class CatanConfiguration extends Configuration {

    @JsonProperty
    public SwaggerBundleConfiguration swagger;
}
