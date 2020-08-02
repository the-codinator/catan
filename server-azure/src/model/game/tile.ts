import type { Resource } from './resource';

export type Tile =
  | {
      resource: Resource;
      roll: number;
    }
  | {
      // Dessert
      resource: null;
      roll?: 0 | undefined;
    };
