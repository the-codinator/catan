const { CosmosClient } = require('@azure/cosmos');

const DB = 'catan';
const CONTAINERS = ['board', 'games', 'state', 'token', 'user'];
const THROUGHPUT = 400;
const PARTITION = { games: '/user' };
const PARTITION_DEFAULT = '/id';
const TTL = {
  board: 86400 * 365,
  state: 86400 * 365,
  games: 86400 * 365,
  token: 86400 * 14,
};
const INDEXING_POLICY = {
  games: {
    indexingMode: 'consistent',
    automatic: true,
    includedPaths: [],
    excludedPaths: [
      {
        path: '/*',
      },
      {
        path: '/"_etag"/?',
      },
    ],
    compositeIndexes: [
      [
        {
          path: '/completed',
          order: 'ascending',
        },
        {
          path: '/user',
          order: 'ascending',
        },
        {
          path: '/_ts',
          order: 'descending',
        },
      ],
    ],
  },
};
const INDEXING_POLICY_DEFAULT = {
  indexingMode: 'consistent',
  automatic: true,
  includedPaths: [],
  excludedPaths: [
    {
      path: '/*',
    },
    {
      path: '/"_etag"/?',
    },
  ],
};

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
    const contResponse = await client.database(DB).containers.createIfNotExists({
      id: container,
      partitionKey: PARTITION[container] || PARTITION_DEFAULT,
      defaultTtl: TTL[container],
      indexingPolicy: INDEXING_POLICY[container] || INDEXING_POLICY_DEFAULT,
    });
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
