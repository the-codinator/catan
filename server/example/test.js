/*
 * @author the-codinator
 * created on 2020/5/27
 */

/**
 * Runs an entire test game to validate functionality
 * Supported args with default values:
 * - server=http://localhost:8080
 * - signup=true
 */

(async () => {
  const args = (process.argv || []).slice(2).reduce((map, arg) => {
    const keyVal = arg.split('=');
    map[keyVal[0]] = keyVal[1];
    return map;
  }, {});
  const fetch = require('node-fetch');
  const authTokens = [];
  let id = undefined;
  let etag;
  let tag;

  function json(file) {
    return require(`./${file}.json`);
  }

  function log(k, v) {
    if (v) {
      console.log();
    }
    console.log(k);
    if (v) {
      console.log(v);
      console.log();
    }
  }

  function start(t) {
    tag = t;
  }

  function pass() {
    log(`[PASS] ${tag}`);
  }

  async function callWithData(api, body) {
    api = api.replace('/id', `/${id}`);
    const data = {headers: {}};
    if (authTokens.length === 4) {
      data.headers.authorization = `Bearer ${authTokens[0]}`
    }
    if (body) {
      data.method = 'POST';
      data.headers['content-type'] = 'application/json';
      if (etag) {
        data.headers['If-Match'] = etag;
      }
      data.body = JSON.stringify(body);
    }
    const server = args.server || 'http://localhost:8080';
    const response = await fetch(`${server}${api}`, data);
    if (!response.ok) {
      log(`[FAIL] ${tag}`);
      console.log();
      console.error('API call failed', JSON.stringify({api, data, response: await response.json()}, null, 2));
      throw Error('API call failed');
    }
    etag = response.headers.get('ETag') || etag;
    return response.json();
  }

  async function callFromFile(api, file, func) {
    start(file);
    const data = json(file);

    async function callForOne(api, body) {
      const resp = await callWithData(api, body);
      func && func(resp);
    }

    if (Array.isArray(data)) {
      for (const body of data) {
        await callForOne(api, body);
        authTokens.push(authTokens.shift());
      }
    } else {
      await callForOne(api, data);
    }
    pass();
  }

  log('Starting test...');

  // Create users
  if (args.signup !== 'false') {
    await callFromFile('/user/signup', 'signup')
  }

  // Login
  await callFromFile('/user/login', 'login', resp => authTokens.push(resp.access_token));
  log('Token - user1', `Bearer ${authTokens[0]}`);

  // Game
  await callFromFile('/game', 'board', resp => id = resp.id);
  log('Game id', id);

  start('get game')
  await callWithData(`/game/${id}`);
  pass();

  // Setup 1
  // TODO:

  // Setup 2
  authTokens.reverse();
  // TODO:
  authTokens.reverse();

  // TODO:
})();
