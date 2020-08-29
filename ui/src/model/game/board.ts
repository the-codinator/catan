import { DeepReadonly } from 'ts-essentials';
import type { Player } from './player';
import type { Ports } from './ports';
import type { Tile } from './tile';

export type Board = DeepReadonly<{
  id: string;
  created: number;
  completed: number;
  tiles: Tile[];
  ports: Ports;
  players: Player[];
}>;
