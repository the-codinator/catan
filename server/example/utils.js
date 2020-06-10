/*
 * @author the-codinator
 * created on 2020/6/10
 */
const args = (process.argv || []).slice(2).reduce((map, arg) => {
  const keyVal = arg.split('=');
  map[keyVal[0]] = keyVal[1];
  return map;
}, {});

const fetch = require('node-fetch');
const deepEqual = require('deep-equal');

const vars = {
  authTokens: [],
  id: undefined,
  board: undefined,
  etag: undefined,
  tag: undefined
};

function json(file) {
  return require(`./${file}.json`);
}

function jsonState(file) {
  return Object.assign(json(file), {id: vars.id});
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

function prettyStringify(json) {
  return JSON.stringify(json, null, 2);
}

function start(t) {
  vars.tag = t;
}

function pass() {
  log(`[PASS] ${vars.tag}`);
}

async function callWithData(user, api, body) {
  api = api.replace('/id', `/${vars.id}`);
  const data = {headers: {accept: 'application/json'}};
  if (typeof user === 'number') {
    data.headers.authorization = `Bearer ${vars.authTokens[user]}`
  }
  if (body) {
    data.method = 'POST';
    data.headers['content-type'] = 'application/json';
    if (vars.etag) {
      data.headers['If-Match'] = vars.etag;
    }
    data.body = JSON.stringify(body);
  }
  const server = args.server || 'http://localhost:8080';
  const response = await fetch(`${server}${api}`, data);
  if (!response.ok) {
    log(`[FAIL] ${vars.tag}`);
    console.log();
    console.error('API call failed', prettyStringify({user, api, data, response: await response.json()}));
    throw Error('API call failed');
  }
  vars.etag = response.headers.get('ETag') || vars.etag;
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

function assertEqual(actual, expected, error = 'Objects were not equal') {
  if (!deepEqual(actual, expected)) {
    console.log('GOT: ' + prettyStringify(actual));
    console.log('EXPECTED: ' + prettyStringify(expected));
    throw Error(error);
  }
}

async function assertState(expected, stateMapper = state => state, user = 0) {
  const state = await callWithData(user, '/game/id/state');
  assertEqual(stateMapper(state), expected, 'State did not match expected');
  log('[PASS] State Match Check');
}

module.exports = {
  args,
  fetch,
  deepEqual,
  vars,
  json,
  jsonState,
  log,
  prettyStringify,
  start,
  pass,
  callWithData,
  callFromFile,
  assertEqual,
  assertState
};
