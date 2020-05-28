/*
 * @author the-codinator
 * created on 2020/5/27
 */

(async () => {
  process = process || {argv: []};
  const server = process.argv[2] || 'http://localhost:8080';
  const json = file => require(`./${file}.json`);
  const fetch = require('node-fetch');
  let authorization;

  function log(k, v) {
    console.log();
    console.log(k);
    console.log(v);
  }

  async function call(api, body) {
    const data = {headers: {authorization}};
    if (body) {
      data.method = 'POST';
      data.headers['content-type'] = 'application/json';
      data.body = JSON.stringify(body);
    }
    const response = await fetch(`${server}${api}`, data);
    if (!response.ok) {
      console.error('API call failed', {api, body, response: await response.text()});
      throw Error('API call failed');
    }
    return response.json();
  }

  // Create users
  await call('/user/signup', json('signup'));
  await call('/user/signup', {id: 'user2', name: 'User Two', pwd: 'us3r2'});
  await call('/user/signup', {id: 'user3', name: 'User Three', pwd: 'us3r3'});
  await call('/user/signup', {id: 'user4', name: 'User Four', pwd: 'us3r4'});

  // Login
  const login = await call('/user/login', json('login'));
  authorization = `Bearer ${login.access_token}`;
  log('Authorization', authorization);

  // Create Board
  const id = (await call('/game', json('board'))).id;
  log('Game id', id);

  await call(`/game/${id}`);
})();
