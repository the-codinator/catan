import { INIT_BANK_RESOURCE_COUNT } from '../../util/constants';
import { createEnumMap } from '../../util/util';

export enum Resource {
  wood = 'wood',
  brick = 'brick',
  hay = 'hay',
  sheep = 'sheep',
  rock = 'rock',
}

export const RESOURCES = Object.freeze(Object.values(Resource).sort());

const tileCount: Record<Resource, number> = {
  wood: 4,
  brick: 3,
  hay: 4,
  sheep: 4,
  rock: 3,
};

export function getTileCount(resource: Resource): number {
  return tileCount[resource];
}

export function createNewBank(): Record<Resource, number> {
  return createEnumMap(Resource, () => INIT_BANK_RESOURCE_COUNT);
}
