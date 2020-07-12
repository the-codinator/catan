const { CosmosClient } = require('@azure/cosmos');

async function f() {
  const key = '==';
  const client = new CosmosClient({ endpoint: 'https://catan-db.documents.azure.com:443/', key });
  const test = client.database('catan').container('test');

  const options = {
    accessCondition: {
      type: 'IfMatch',
      condition: '"e0013889-0000-1800-0000-"'
    }
  };

  const t1 = await test.items.create({
    id: 'user6',
    data: '12345'
  });

  // const t = await test.item('user1', 'user1').replace(
  //   {
  //     id: 'user1',
  //     data: '333333'
  //   },
  //   options
  // );

  const t = await test.item('user6', 'user6').delete(options);

  // const t = await test.items.upsert(
  //   {
  //     id: 'user1',
  //     data: '33'
  //   },
  //   options
  // );

  // const t = test.id;

  console.log(t);

  // const x = await test.item('user1', 'user1').read(options);
  // console.log(x);
}

f().catch(e => {
  console.log(e);
  console.log('---------------');
  console.log(JSON.stringify(e));
});


