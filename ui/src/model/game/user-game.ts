import type { Color } from './color';

export type UserGame = Readonly<{
  id: string;
  game: string;
  user: string;
  color: Color;
  myTurn: boolean;
  completed: boolean;
}>;
