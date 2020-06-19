/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.core;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;
import lombok.Setter;
import org.codi.catan.impl.data.DatabaseType;

@Setter
public class DatabaseConfig {

    @Getter
    private DatabaseType type;
    private String key;
    private String secret;
    private String connection;

    private AWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(key, secret);
    }

    private String getAzureCosmosDbConnectionString() {
        return connection;
    }
}
