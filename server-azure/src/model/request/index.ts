import type { LoginRequest, RefreshTokenRequest, SignUpRequest, _LogoutRequest } from './user-request';
import type { Opaque } from 'ts-essentials';

// All request types requiring game id
export type GameRequest = { temp1: string };

// All request types supporting ETag header
export type ETagRequest = { temp2: string };

export type AuthenticatedGetRequest = Opaque<{}, 'AuthenticatedGetRequest'>;

// All request types support authentication
export type AuthenticatedRequest = ETagRequest | GameRequest | AuthenticatedGetRequest | _LogoutRequest;

// All request types which do not require authentication
export type UnauthenticatedRequest = LoginRequest | SignUpRequest | RefreshTokenRequest;

// Mainly for GET requests
export type BodyLessRequest = AuthenticatedGetRequest | _LogoutRequest;

// All request types
export type CatanRequest = AuthenticatedRequest | UnauthenticatedRequest | BodyLessRequest;
