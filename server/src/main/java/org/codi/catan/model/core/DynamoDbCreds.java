/*
 * @author the-codinator
 * created on 2020/5/23
 */

package org.codi.catan.model.core;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamoDbCreds {

    private String key;
    private String secret;

    private AWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(key, secret);
    }
}
