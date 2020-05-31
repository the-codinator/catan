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
  let board = undefined;
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

  async function callWithData(user, api, body) {
    api = api.replace('/id', `/${id}`);
    const data = {headers: {accept: 'application/json'}};
    if (typeof user === 'number') {
      data.headers.authorization = `Bearer ${authTokens[user]}`
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
      console.error('API call failed', JSON.stringify({user, api, data, response: await response.json()}, null, 2));
      throw Error('API call failed');
    }
    etag = response.headers.get('ETag') || etag;
    return response.json();
  }

  async function callFromFile(file, func) {
    start(file);
    for (const data of json(file)) {
      const resp = await callWithData(data.user, data.api, data.body);
      func && func(resp);
    }
    pass();
  }

  function assertState(state, file) {
    const expected = json(file);
    expected.id = id;
    if (JSON.stringify(state) !== JSON.stringify(expected)) {
      throw Error('State did not match expected');
    }
    log('[PASS] State Match Check');
  }

  log('Starting test...');

  // Create users
  if (args.signup !== 'false') {
    await callFromFile('signup')
  }

  // Login
  await callFromFile('login', resp => authTokens.push(resp.access_token));
  log('Token - user0', `Bearer ${authTokens[0]}`);

  // Game
  await callFromFile('board', resp => {
    id = resp.id;
    board = resp.board;
  });
  log('Game id', id);

  start('get game')
  await callWithData(0, '/game/id');
  pass();

  // Setup
  await callFromFile('setup');

  assertState(await callWithData(0, '/game/id/state'), 'postSetupState');

  // TODO:
})();
