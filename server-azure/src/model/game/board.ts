import type { BoardRequest } from '../request/board-request';
import type { DeepWritable } from 'ts-essentials';
import type { IdentifiableEntity } from '../core';
import type { Player } from './player';
import type { Ports } from './ports';
import type { Tile } from './tile';

export interface Board extends IdentifiableEntity {
  created: number;
  completed: number;
  tiles: Tile[];
  ports: Ports;
  players: Player[];
}

export function createBoard(id: string, request: BoardRequest): Board {
  const { tiles, ports, players } = request as DeepWritable<BoardRequest>;
  return { id, created: Date.now(), completed: 0, tiles, ports, players };
}
