import type { Resource } from './resource';

export type Tile =
  | {
      resource: Resource;
      roll: number;
    }
  | {
      // Dessert
      // eslint-disable-next-line @typescript-eslint/ban-types
      resource: null;
      roll?: 0 | undefined;
    };
