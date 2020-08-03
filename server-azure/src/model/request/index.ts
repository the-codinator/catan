import type { LoginRequest, RefreshTokenRequest, SignUpRequest, _LogoutRequest } from './user-request';
import type { BoardRequest } from './board-request';
import type { Opaque } from 'ts-essentials';

// All request types requiring game id
export type GameRequest = AuthenticatedGetGameRequest | BoardRequest;

// All request types supporting ETag header
export type ETagRequest = AuthenticatedGetGameETagRequest | { temp2: string };

export type AuthenticatedGetGameETagRequest = Opaque<{}, 'AuthenticatedGetGameETagRequest'>;
export type AuthenticatedGetGameRequest = AuthenticatedGetGameETagRequest | Opaque<{}, 'AuthenticatedGetGameRequest'>;
export type AuthenticatedGetRequest = AuthenticatedGetGameRequest | Opaque<{}, 'AuthenticatedGetRequest'>;

// All request types support authentication
export type AuthenticatedRequest = ETagRequest | GameRequest | AuthenticatedGetRequest | _LogoutRequest;

// All request types which do not require authentication
export type UnauthenticatedRequest = LoginRequest | SignUpRequest | RefreshTokenRequest;

// Mainly for GET requests
export type BodyLessRequest = AuthenticatedGetRequest | _LogoutRequest;

// All request types
export type CatanRequest = AuthenticatedRequest | UnauthenticatedRequest | BodyLessRequest;
