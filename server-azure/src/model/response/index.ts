import type { BoardResponse, GameResponse } from './game-response';
import type { FindUserResponse } from './find-user-response';
import type { MessageResponse } from './message-response';
import type { SessionResponse } from './session-response';
import type { StateResponse } from './state-response';
import type { UserGamesResponse } from './user-games-response';

export type SwaggerResponse = Buffer;
export type StringResponse = string;
export type UserResponse = SessionResponse | FindUserResponse | UserGamesResponse;
export type GamePlayResponse = GameResponse | StateResponse | BoardResponse;

export type CatanResponse = MessageResponse | GamePlayResponse | UserResponse | StringResponse;
