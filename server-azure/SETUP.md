# Infrastructure Setup

For initial setup of Cosmos DB Account run the following commands \
For the purpose of this deployment, we have use `LOCATION=southeastasia`

## Cosmos DB Account Creation

```sh
# Azure Login
az login
az account --subscription $SUBSCRIPTION # Ensure you are using the correct subscription if you have access to multiple

# Create Resource Group
az group create --name catan --location $LOCATION --tags project=catan url=https://github.com/codinators/catan

# Create Cosmos DB Account
az cosmosdb create --name catan-db --resource-group catan --locations regionName=$LOCATION failoverPriority=0 isZoneRedundant=False
```

## Cosmos DB Database and Containers setup

After executing the above, grab the Connection String (Read / Write) from the [Azure Portal](https://portal.azure.com). \
Export the connection string as an environment variable before proceeding - `export "CONNECTION=<connection-string>"`

For setting up the Databases and Containers inside the Cosmos DB Account save the JS script below as `setup.js` \
Run `npm install @azure/cosmos` to pull in the Cosmos DB JS SDK, and `node setup.js` to setup the account.
You can delete the `setup.js` file once done.

```js
// setup.js
const { CosmosClient } = require('@azure/cosmos');

const DB = 'catan';
const CONTAINERS = ['board', 'state', 'token', 'user'];
const THROUGHPUT = 400;
const PARTITION = '/id';

async function setup() {
  const client = new CosmosClient(process.env.CONNECTION);
  const dbResponse = await client.databases.createIfNotExists({ id: DB, throughput: THROUGHPUT });
  if (dbResponse.statusCode === 201) {
    console.log(`Database [${dbResponse.database.id}] created successfully!`);
  } else if (dbResponse.statusCode === 200) {
    console.log(`Database [${dbResponse.database.id}] already exists!`);
  } else {
    throw new Error('Error creating Database - status=' + dbResponse.statusCode);
  }
  for (const container of CONTAINERS) {
    const contResponse = await client.database(DB).containers.createIfNotExists({ id: container, partitionKey: PARTITION });
    if (contResponse.statusCode === 201) {
      console.log(`Container [${contResponse.container.id}] created successfully!`);
    } else if (contResponse.statusCode === 200) {
      console.log(`Container [${contResponse.container.id}] already exists!`);
    } else {
      throw new Error('Error creating Container - status=' + contResponse.statusCode);
    }
  }
}

setup().catch(e => console.log('Error in setup', e));
```
