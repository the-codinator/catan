import { INIT_BANK_RESOURCE_COUNT } from '../../util/constants';

export enum Resource {
  wood = 'wood',
  brick = 'brick',
  hay = 'hay',
  sheep = 'sheep',
  rock = 'rock',
}

export function createNewBank(): Record<Resource, number> {
  return {
    wood: INIT_BANK_RESOURCE_COUNT,
    brick: INIT_BANK_RESOURCE_COUNT,
    hay: INIT_BANK_RESOURCE_COUNT,
    sheep: INIT_BANK_RESOURCE_COUNT,
    rock: INIT_BANK_RESOURCE_COUNT,
  };
}
