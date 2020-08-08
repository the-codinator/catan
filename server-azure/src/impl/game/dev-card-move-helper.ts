import * as AchievementHelper from './achievement-helper';
import { COLORS, Color } from '../../model/game/color';
import type { DevCardRequest, RoadRequest, _DevBuyRequest } from '../../model/request/game-request';
import { arrayRemove, count, getFrequencyMapTotalCount } from '../../util/util';
import { transferResources, transferResourcesList } from './game-utility';
import { BadRequestError } from '../../core/catan-error';
import { DevCard } from '../../model/game/dev-card';
import { MAX_ROADS_PER_PLAYER } from '../../util/constants';
import type { PlayOptions } from './move-api-helper';
import { Resource } from '../../model/game/resource';
import type { State } from '../../model/game/state';
import { ensureCanPlaceRoad } from './build-move-helper';
import { thiefPlayInternal } from './thief-move-helper';

export function buy({ state }: PlayOptions<_DevBuyRequest>): void {
  if (state.bankDevCards.length === 0) {
    throw new BadRequestError('No Dev Cards available in Bank');
  }
  const color = state.currentMove.color;
  transferResourcesList(state, color, undefined, Resource.hay, Resource.sheep, Resource.rock);
  state.hands[color].devCards.push(state.bankDevCards.shift()!);
}

export function play({ state, request }: PlayOptions<DevCardRequest>): void {
  if (!state.currentMove.devCard) {
    throw new BadRequestError('Can play at most 1 dev card per turn');
  }
  const devCard = request.type;
  const color = state.currentMove.color;
  if (!arrayRemove(state.hands[color].devCards, devCard)) {
    throw new BadRequestError('Dev Card not present in hand');
  }
  state.currentMove.devCard = devCard;
  const playedDevCards = state.playedDevCards[color];
  if (!playedDevCards) {
    state.playedDevCards[color] = [devCard];
  } else {
    playedDevCards.push(devCard);
  }
  switch (devCard) {
    case DevCard.knight:
      knight(state, request.hex, request.victim);
      break;
    case DevCard.road_building:
      roadBuilding(state, request.road1, request.road2);
      break;
    case DevCard.year_of_plenty:
      yearOfPlenty(state, request.resource1, request.resource2);
      break;
    case DevCard.monopoly:
      monopoly(state, request.resource);
      break;
    default:
      throw new BadRequestError(
        'Cannot play Victory cards, they are revealed on game end. If you are winning, end your turn!'
      );
  }
}

function knight(state: State, hex: number | undefined, victim: Color | undefined): void {
  if (hex === undefined || !victim) {
    throw new BadRequestError('Missing required input fields "hex" & "victim"');
  }
  thiefPlayInternal(state, { hex, victim });
  AchievementHelper.handleLargestArmy(state);
}

function roadBuilding(state: State, r1: RoadRequest | undefined, r2: RoadRequest | undefined): void {
  const color = state.currentMove.color;
  const roadCount = count(state.roads, road => road.color === color);
  if (MAX_ROADS_PER_PLAYER - roadCount >= 1) {
    if (!r1) {
      throw new BadRequestError('Missing required input field "road1"');
    }
    const road1 = { color, vertex1: r1.vertex1, vertex2: r1.vertex2 };
    ensureCanPlaceRoad(state, road1.vertex1, road1.vertex2);
    state.roads.push(road1);
    if (MAX_ROADS_PER_PLAYER - roadCount >= 2) {
      if (!r2) {
        throw new BadRequestError('Missing required input field "road2"');
      }
      const road2 = { color, vertex1: r2.vertex1, vertex2: r2.vertex2 };
      ensureCanPlaceRoad(state, road2.vertex1, road2.vertex2);
      state.roads.push(road2);
    }
    AchievementHelper.handleLongestRoad(state);
  }
}

function yearOfPlenty(state: State, r1: Resource | undefined, r2: Resource | undefined): void {
  const bankCount = getFrequencyMapTotalCount(state.bank);
  if (bankCount >= 1) {
    if (!r1) {
      throw new BadRequestError('Missing required input field "resource1"');
    }
  } else {
    r1 = undefined;
  }
  if (bankCount >= 2) {
    if (!r2) {
      throw new BadRequestError('Missing required input field "resource2"');
    }
  } else {
    r2 = undefined;
  }
  transferResourcesList(state, undefined, state.currentMove.color, r1, r2);
}

function monopoly(state: State, resource: Resource | undefined): void {
  if (!resource) {
    throw new BadRequestError('Missing required input field "resource"');
  }
  const turn = state.currentMove.color;
  for (const color of COLORS) {
    if (color !== turn) {
      transferResources(state, color, turn, resource, state.hands[color].resources[resource] ?? 0);
    }
  }
}
