import type { LoginRequest } from './user-request';

// All request types requiring game id
export type GameCatanRequest = { temp1: string };

// All request types supporting ETag header
export type ETagCatanRequest = { temp2: string };

// All request types support authentication
export type AuthenticatedCatanRequest = ETagCatanRequest | GameCatanRequest;

// All request types which do not require authentication
export type UnauthenticatedCatanRequest = LoginRequest;

// Mainly for GET requests
export type BodyLessRequest = null;

// All request types
export type CatanRequest = AuthenticatedCatanRequest | UnauthenticatedCatanRequest | BodyLessRequest;
