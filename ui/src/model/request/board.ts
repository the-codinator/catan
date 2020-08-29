import type { DeepReadonly } from 'ts-essentials';
import type { Player } from '../game/player';
import type { Ports } from '../game/ports';
import type { Tile } from '../game/tile';

export type BoardRequest = DeepReadonly<{
  tiles: Tile[];
  ports: Ports;
  players: Player[];
}>;
