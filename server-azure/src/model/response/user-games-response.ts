import type { DeepReadonly } from 'ts-essentials';
import type { UserGame } from '../game/user-game';

export type UserGamesResponse = DeepReadonly<Array<Omit<UserGame, 'id' | 'user'>>>;
