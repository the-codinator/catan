import type { HouseRequest, RoadRequest } from '../../model/request/game-request';
import { MAX_HOUSES_PER_PLAYER, MAX_ROADS_PER_PLAYER } from '../../util/constants';
import { getAdjacentVertexListForVertex, isAdjacentVertices, validateVertex } from './graph-helper';
import { transferResources, transferResourcesList } from './game-utility';
import { BadRequestError } from '../../core/catan-error';
import { HouseType } from '../../model/game/house-type';
import type { PlayOptions } from './move-api-helper';
import { Resource } from '../../model/game/resource';
import type { State } from '../../model/game/state';
import { handleLongestRoad } from './achievement-helper';
import { isSetupPhase } from '../../model/game/phase';

export function road({ state, request }: PlayOptions<RoadRequest>): void {
  ensureCanPlaceRoad(state, request.vertex1, request.vertex2);
  const color = state.currentMove.color;
  transferResourcesList(state, color, undefined, Resource.wood, Resource.brick);
  const road = { color, vertex1: request.vertex1, vertex2: request.vertex2 };
  state.roads.push(road);
  handleLongestRoad(state);
}

export function ensureCanPlaceRoad(state: State, vertex1: number, vertex2: number): void {
  if (!isAdjacentVertices(vertex1, vertex2)) {
    throw new BadRequestError('Cannot place road between non-adjacent vertices');
  }
  const color = state.currentMove.color;
  let valid = isSetupPhase(state.phase);
  let count = 0;
  for (const road of state.roads) {
    // Max road count
    if (road.color === color) {
      count++;
    }
    // Check existing road
    if (
      (road.vertex1 === vertex1 && road.vertex2 === vertex2) ||
      (road.vertex1 === vertex2 && road.vertex2 === vertex1)
    ) {
      valid = false;
      break;
    }
    // Check connecting road
    if (
      !valid &&
      road.color === color &&
      (road.vertex1 === vertex1 || road.vertex1 === vertex2 || road.vertex2 === vertex1 || road.vertex2 === vertex2)
    ) {
      valid = true;
      // Don't break, need to test all roads for "existing road"
    }
  }
  if (count >= MAX_ROADS_PER_PLAYER) {
    throw new BadRequestError(`Cannot create more than ${MAX_ROADS_PER_PLAYER} roads`);
  }
  if (!valid) {
    throw new BadRequestError('Invalid location for road');
  }
}

export function house({ state, request }: PlayOptions<HouseRequest>): void {
  const type = state.houses[request.vertex] ? HouseType.city : HouseType.settlement;
  ensureCanPlaceHouse(state, request.vertex, type);
  const color = state.currentMove.color;
  switch (type) {
    case HouseType.settlement:
      transferResourcesList(state, color, undefined, Resource.wood, Resource.brick, Resource.hay, Resource.sheep);
      state.houses[request.vertex] = { color, type };
      break;
    case HouseType.city:
      transferResources(state, color, undefined, Resource.hay, 2);
      transferResources(state, color, undefined, Resource.rock, 3);
      state.houses[request.vertex]!.type = HouseType.city;
      break;
  }
}

export function ensureCanPlaceHouse(state: State, vertex: number, type: HouseType): void {
  validateVertex(vertex);
  const color = state.currentMove.color;
  const houses = state.houses;
  const count = Object.values(houses).reduce<number>((count, house) => count + (house?.color === color ? 1 : 0), 0);
  if (count >= MAX_HOUSES_PER_PLAYER) {
    throw new BadRequestError(`Cannot create more than ${MAX_HOUSES_PER_PLAYER} houses`);
  }
  switch (type) {
    case HouseType.settlement: {
      // Validate no house on vertex
      if (houses[vertex]) {
        throw new BadRequestError('Cannot create settlement on existing building');
      }
      // Validate no adjacent house
      for (const adjVertex of getAdjacentVertexListForVertex(vertex)) {
        if (houses[adjVertex]) {
          throw new BadRequestError('Cannot create settlement adjacent to other buildings');
        }
      }
      break;
    }
    case HouseType.city: {
      const house = houses[vertex];
      if (!house || house.type !== HouseType.settlement || house.color !== color) {
        throw new BadRequestError('Cannot upgrade to city without own settlement');
      }
      break;
    }
  }
}
