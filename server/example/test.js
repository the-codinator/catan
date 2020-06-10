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
  const {args, deepEqual, vars, json, jsonState, log, prettyStringify, start, pass, callWithData, callFromFile, assertEqual, assertState} = require(
      './utils');

  log('Starting test...');

  // Create users
  if (args.signup !== 'false') {
    await callFromFile('signup');
  }

  // Login
  await callFromFile('login', resp => vars.authTokens.push(resp.access_token));
  log('Token - user0', `Bearer ${vars.authTokens[0]}`);

  // Logout & Refresh

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
  const expected = json('postSetupGames');
  expected.ongoing[0].id = vars.id;
  expected.ongoing[0].created = games.ongoing[0].created;
  assertEqual(games, expected);
  pass('user games');

  // TODO:
})();
