import type {
  DevCardRequest,
  HouseRequest,
  RoadRequest,
  SetupMoveRequest,
  ThiefDropRequest,
  ThiefPlayRequest,
  TradeBankRequest,
  TradePlayerRequest,
  TradeResponseRequest,
  _DevBuyRequest,
  _EndTurnRequest,
  _RollRequest,
} from './game-request';
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

export type MoveRequest =
  | BodyLessMoveRequest
  | SetupMoveRequest
  | RoadRequest
  | HouseRequest
  | DevCardRequest
  | ThiefDropRequest
  | ThiefPlayRequest
  | TradeBankRequest
  | TradePlayerRequest
  | TradeResponseRequest;

export type BodyLessMoveRequest = _RollRequest | _DevBuyRequest | _EndTurnRequest;

// All request types support authentication
export type AuthenticatedRequest = AuthenticatedGetRequest | BoardRequest | MoveRequest | _LogoutRequest;

// All request types which do not require authentication
export type UnauthenticatedRequest = LoginRequest | SignUpRequest | RefreshTokenRequest;

// Mainly for GET requests
export type BodyLessRequest = AuthenticatedGetRequest | _LogoutRequest | BodyLessMoveRequest;

// All request types
export type CatanRequest = AuthenticatedRequest | UnauthenticatedRequest;
