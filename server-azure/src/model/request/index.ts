import type { DevCardRequest, HouseRequest, RoadRequest, SetupMoveRequest, _BodyLessMoveRequest } from './game-request';
import type { LoginRequest, RefreshTokenRequest, SignUpRequest, _LogoutRequest } from './user-request';
import type { BoardRequest } from './board-request';
import type { Opaque } from 'ts-essentials';

// All request types requiring game id
export type GameRequest = AuthenticatedGetGameRequest | MoveRequest;

// All request types supporting ETag header
export type ETagRequest = AuthenticatedGetGameETagRequest | MoveRequest;

export type AuthenticatedGetRequest = AuthenticatedGetGameRequest | Opaque<{}, 'AuthenticatedGetRequest'>;
export type AuthenticatedGetGameRequest = AuthenticatedGetGameETagRequest | Opaque<{}, 'AuthenticatedGetGameRequest'>;
export type AuthenticatedGetGameETagRequest = Opaque<{}, 'AuthenticatedGetGameETagRequest'>;

export type MoveRequest = SetupMoveRequest | _BodyLessMoveRequest | RoadRequest | HouseRequest | DevCardRequest;

// All request types support authentication
export type AuthenticatedRequest = AuthenticatedGetRequest | BoardRequest | MoveRequest | _LogoutRequest;

// All request types which do not require authentication
export type UnauthenticatedRequest = LoginRequest | SignUpRequest | RefreshTokenRequest;

// Mainly for GET requests
export type BodyLessRequest = AuthenticatedGetRequest | _LogoutRequest | _BodyLessMoveRequest;

// All request types
export type CatanRequest = AuthenticatedRequest | UnauthenticatedRequest;
