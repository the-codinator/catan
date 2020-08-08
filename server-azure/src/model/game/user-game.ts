import type { Board } from './board';
import { CatanError } from '../../core/catan-error';
import type { Color } from './color';
import type { Player } from './player';
import type { Writable } from 'ts-essentials';

export type UserGame = Readonly<{
  id: string;
  game: string;
  user: string;
  color: Color;
  myTurn: boolean;
  completed: boolean;
}>;

export function createUserGame(board: Board, color: Color): UserGame {
  return createUserGameInternal(board, getPlayer(board, color), false);
}

function createUserGameInternal(board: Board, player: Player, completed: boolean): UserGame {
  return {
    id: generateUserGameId(board.id, player.id),
    game: board.id,
    user: player.id,
    color: player.color,
    myTurn: false,
    completed,
  };
}

export function generateUserGameId(game: string, user: string): string {
  return `${game}:${user}`;
}

export function createUserGamesFromBoard(board: Board): UserGame[] {
  const completed = board.completed !== 0;
  const ugs = board.players.map(player => createUserGameInternal(board, player, completed));
  if (!completed) {
    (ugs[0] as Writable<UserGame>).myTurn = true;
  }
  return ugs;
}

function getPlayer(board: Board, color: Color): Player {
  const player = board.players.find(player => player.color === color);
  if (!player) {
    throw new CatanError('Got null color for UserGame');
  }
  return player;
}
