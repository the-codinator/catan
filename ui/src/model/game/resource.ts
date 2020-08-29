export enum Resource {
  wood = 'wood',
  brick = 'brick',
  hay = 'hay',
  sheep = 'sheep',
  rock = 'rock',
}

export const RESOURCES = Object.freeze(Object.values(Resource).sort());

export const TILE_COUNT: Readonly<Record<Resource, number>> = Object.freeze({
  wood: 4,
  brick: 3,
  hay: 4,
  sheep: 4,
  rock: 3,
});
