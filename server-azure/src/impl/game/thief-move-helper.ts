import { COLORS, Color } from '../../model/game/color';
import type { ThiefDropRequest, ThiefPlayRequest } from '../../model/request/game-request';
import { chooseRandomlyStolenCard, transferResourcesList } from './game-utility';
import { getVerticesAroundHex, validateHex } from './graph-helper';
import { BadRequestError } from '../../core/catan-error';
import { DROP_CARDS_FOR_THIEF_THRESHOLD } from '../../util/constants';
import { Phase } from '../../model/game/phase';
import type { PlayOptions } from './move-api-helper';
import type { State } from '../../model/game/state';
import { getResourceCount } from '../../model/game/hand';

export function handleThiefRoll(state: State): void {
  state.phase = Phase.thief;
  const thieved: Color[] = [];
  for (const color of COLORS) {
    if (getResourceCount(state.hands[color]) > DROP_CARDS_FOR_THIEF_THRESHOLD) {
      thieved.push(color);
    }
  }
  if (thieved.length) {
    state.currentMove.thieved = thieved;
  }
}

export function thiefDrop({ state, color, request }: PlayOptions<ThiefDropRequest>): void {
  const hand = state.hands[color];
  if (request.resources.length !== Math.floor(getResourceCount(hand) / 2)) {
    throw new BadRequestError('Incorrect number of resource cards - need ' + getResourceCount(hand) / 2);
  }
  transferResourcesList(state, color, undefined, ...request.resources);
  const thieved = state.currentMove.thieved!; // From MoveApiHelper
  thieved.splice(thieved.indexOf(color), 1);
  if (thieved.length === 0) {
    delete state.currentMove.thieved;
  }
}

export function thiefPlay({ state, request }: PlayOptions<ThiefPlayRequest>): void {
  if (state.currentMove.thieved?.length) {
    throw new BadRequestError(
      'Please wait until players with 8+ cards have dropped half - ' + state.currentMove.thieved
    );
  }
  if (request.hex === state.thief) {
    throw new BadRequestError('Thief MUST be moved to a different tile');
  }
  const color = state.currentMove.color;
  if (color === request.victim) {
    throw new BadRequestError('Cannot steal from self');
  }
  validateHex(request.hex);
  const vertices = getVerticesAroundHex(request.hex);

  let hasHouse = false;
  for (const vertex of vertices) {
    const house = state.houses[vertex];
    if (house && house.color !== color) {
      if (!request.victim && getResourceCount(state.hands[house.color])) {
        throw new BadRequestError('Must steal from a player if possible (missing field - victim)');
      }
      if (house.color === request.victim) {
        hasHouse = true;
        break;
      }
    }
  }
  if (!request.victim) {
    return;
  }
  if (!hasHouse) {
    throw new BadRequestError('Cannot steal from player without house on thief tile');
  }
  const resource = chooseRandomlyStolenCard(state, request.victim);
  transferResourcesList(state, request.victim, color, resource);
}
