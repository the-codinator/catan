/*
 * @author the-codinator
 * created on 2020/6/19
 */

package org.codi.catan.impl.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DatabaseType {
    inMemory(InMemoryCDC.class),
    dynamoDb(DynamoDbCDC.class),
    cosmosDb(CosmosDbCDC.class);

    private final Class<? extends CatanDataConnector> impl;
}
