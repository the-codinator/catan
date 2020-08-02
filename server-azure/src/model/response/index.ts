import type { FindUserResponse } from './find-user-response';
import type { GameResponse } from './game-response';
import type { MessageResponse } from './message-response';
import type { SessionResponse } from './session-response';
import type { StateResponse } from './state-response';

export type SwaggerResponse = Buffer;
export type StringResponse = string;
export type UserResponse = SessionResponse | FindUserResponse;
export type GamePlayResponse = GameResponse | StateResponse;

export type CatanResponse = MessageResponse | GamePlayResponse | UserResponse | StringResponse;
