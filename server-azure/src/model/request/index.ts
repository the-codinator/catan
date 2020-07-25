import type { LoginRequest } from './user-request';

// All request types requiring game id
export type GameRequest = { temp1: string };

// All request types supporting ETag header
export type ETagRequest = { temp2: string };

// All request types support authentication
export type AuthenticatedRequest = ETagRequest | GameRequest;

// All request types which do not require authentication
export type UnauthenticatedRequest = LoginRequest;

// Mainly for GET requests
export type BodyLessRequest = null;

// All request types
export type CatanRequest = AuthenticatedRequest | UnauthenticatedRequest | BodyLessRequest;
