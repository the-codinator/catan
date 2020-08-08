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
  const {args, fetch, vars, json, jsonState, log, start, pass, callWithData, callFromFile, assertEqual, assertState} = require('./utils');

  log('Starting test...');

  // Reset DB for local
  if (args.reset === 'true') {
    await fetch((args.server || 'http://localhost:8080') + '/reset');
    console.log('DB Reset');
  }

  // Create users
  if (args.signup !== 'false') {
    await callFromFile('signup');
  }

  // Login
  await callFromFile('login', resp => vars.authTokens.push(resp.access_token), data => args.admin_pwd && (data[4].body.pwd = args.admin_pwd));
  log('Token - admin', `Bearer ${vars.authTokens[4]}`);

  // Logout & Refresh
  await callFromFile('logoutAndRefresh',
      (resp, data, i) => (i === 1 && (data[2].body.refresh_token = resp.refresh_token)) || (i === 2
          && (vars.authTokens[data[0].user] = resp.access_token)));
  log('Token - user0', `Bearer ${vars.authTokens[0]}`);

  // Game
  await callFromFile('board', resp => {
    vars.id = resp.id;
    vars.board = resp.board;
  });
  log('Game id', vars.id);

  start('get game');
  await callWithData(0, '/game/id');
  pass();

  // Setup
  await callFromFile('setup');

  await assertState(jsonState('postSetupState'));

  // Validate Games API
  start('user games');
  const games = await callWithData(0, '/user/games');
  assertEqual(games, [{
    "id": vars.id + ":user0",
    "game": vars.id,
    "user": "user0",
    "color": "red",
    "myTurn": true,
    "completed": false
  }]);
  pass('user games');

  // TODO:
})();
