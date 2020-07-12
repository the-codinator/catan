import type { LoginRequest } from './user-request';

// All request types requiring game id
export type GameCatanRequest = void;

// All request types supporting ETag header
export type ETagCatanRequest = void;

// All request types support authentication
export type AuthenticatedCatanRequest = ETagCatanRequest | GameCatanRequest;

// All request types which do not require authentication
export type UnauthenticatedCatanRequest = LoginRequest;

// All request types
export type CatanRequest = AuthenticatedCatanRequest | UnauthenticatedCatanRequest;
