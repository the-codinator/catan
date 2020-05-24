/*
 * @author the-codinator
 * created on 2020/5/24
 */

package org.codi.catan.api.misc;

import static org.codi.catan.util.Constants.BEARER_AUTHORIZATION_KEY;
import static org.codi.catan.util.Constants.HEADER_AUTHORIZATION;

import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {
    @ApiKeyAuthDefinition(key = BEARER_AUTHORIZATION_KEY, name = HEADER_AUTHORIZATION, in = ApiKeyLocation.HEADER)}))
public class SwaggerSecurityDefinition {

}
