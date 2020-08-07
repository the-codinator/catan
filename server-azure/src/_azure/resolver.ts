import * as BoardApi from '../api/game/board-api';
import * as MoveApi from '../api/game/move-api';
import * as UserApi from '../api/user-api';
import * as Validator from '../model/request/generated-validator';
import type {
  AuthenticatedGetGameETagRequest,
  AuthenticatedGetGameRequest,
  AuthenticatedGetRequest,
  CatanRequest,
  MoveRequest,
} from '../model/request';
import type { BoardResponse, GameResponse } from '../model/response/game-response';
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
  _BodyLessMoveRequest,
} from '../model/request/game-request';
import { HEADER_IF_MATCH, HEADER_IF_NONE_MATCH, METHOD_GET, METHOD_POST } from '../util/constants';
import type { LoginRequest, RefreshTokenRequest, SignUpRequest, _LogoutRequest } from '../model/request/user-request';
import type { PathSegments, Route } from './types';

import type { BoardRequest } from '../model/request/board-request';
import type { CatanResponse } from '../model/response';
import type { FindUserResponse } from '../model/response/find-user-response';
import type { HttpRequest } from '@azure/functions';
import type { MessageResponse } from '../model/response/message-response';
import { NOT_FOUND } from 'http-status-codes';
import type { SessionResponse } from '../model/response/session-response';
import type { StateResponse } from '../model/response/state-response';

type RCC = Route<CatanRequest, CatanResponse>;
type RMS = Route<MoveRequest, StateResponse>;

export function resolve(req: HttpRequest, segments: PathSegments): RCC {
  return (
    baseRoute(req, segments) || {
      handler: { code: NOT_FOUND, message: 'Unknown Route' },
    }
  );
}

function baseRoute(req: HttpRequest, segments: PathSegments): RCC | undefined {
  switch (segments[0]) {
    case 'game':
      return gameRoute(req, segments);
    case 'user':
      return userRoute(req, segments);
    case 'ping':
      return {
        handler: 'pong',
      };
    case 'admin':
      if (segments.length === 1 && req.method === METHOD_POST) {
        // TODO: POST /admin
        return undefined;
      }
  }
  return undefined;
}

function userRoute(req: HttpRequest, segments: PathSegments): RCC | undefined {
  if (segments.length !== 2) {
    return undefined;
  }
  switch (req.method) {
    case METHOD_GET: {
      switch (segments[1]) {
        case 'find': {
          const route: Route<AuthenticatedGetRequest, FindUserResponse> = {
            handler: UserApi.find,
            req: {
              query: ['user'],
            },
            filters: {
              authenticate: true,
            },
          };
          return route as RCC;
        }
        case 'games': {
          // TODO: GET /user/games
          return undefined;
        }
      }
      break;
    }
    case METHOD_POST: {
      switch (segments[1]) {
        case 'signup': {
          const route: Route<SignUpRequest, MessageResponse> = {
            handler: UserApi.signup,
            validator: Validator.validateSignUpRequest,
          };
          return route as RCC;
        }
        case 'login': {
          const route: Route<LoginRequest, SessionResponse> = {
            handler: UserApi.login,
            validator: Validator.validateLoginRequest,
            req: {
              query: ['rememberMe'],
            },
          };
          return route as RCC;
        }
        case 'refresh': {
          const route: Route<RefreshTokenRequest, SessionResponse> = {
            handler: UserApi.refresh,
            validator: Validator.validateRefreshTokenRequest,
          };
          return route as RCC;
        }
        case 'logout': {
          const route: Route<_LogoutRequest, MessageResponse> = {
            handler: UserApi.logout,
            filters: {
              authenticate: true,
            },
          };
          return route as RCC;
        }
      }
    }
  }
  return undefined;
}

function gameRoute(req: HttpRequest, segments: PathSegments): RCC | undefined {
  switch (req.method) {
    case METHOD_GET: {
      const gameId = segments[1];
      if (!gameId) {
        return undefined;
      }
      if (segments.length <= 3) {
        switch (segments[2]) {
          case undefined: {
            const route: Route<AuthenticatedGetGameRequest, GameResponse> = {
              handler: BoardApi.get,
              filters: {
                authenticate: true,
                gameId,
                etag: {
                  response: true,
                },
              },
            };
            return route as RCC;
          }
          case 'board': {
            const route: Route<AuthenticatedGetGameRequest, BoardResponse> = {
              handler: BoardApi.board,
              filters: {
                authenticate: true,
                gameId,
              },
            };
            return route as RCC;
          }
          case 'state': {
            const route: Route<AuthenticatedGetGameETagRequest, StateResponse | ''> = {
              handler: BoardApi.state,
              filters: {
                authenticate: true,
                gameId,
                etag: {
                  request: HEADER_IF_NONE_MATCH,
                  response: true,
                },
              },
            };
            return route as RCC;
          }
        }
      }
      break;
    }
    case METHOD_POST: {
      if (segments.length === 1) {
        const route: Route<BoardRequest, GameResponse> = {
          handler: BoardApi.create,
          validator: Validator.validateBoardRequest,
          filters: {
            authenticate: true,
            etag: {
              response: true,
            },
          },
        };
        return route as RCC;
      } else if (segments[2] === 'move' && segments.length >= 4) {
        return moveRoute(segments) as RCC;
      }
    }
  }
  return undefined;
}

function moveRoute(segments: PathSegments): Route<MoveRequest, StateResponse> | undefined {
  if (segments.length > 5) {
    return undefined;
  }
  const filters: Route<MoveRequest, StateResponse>['filters'] = {
    authenticate: true,
    etag: {
      request: HEADER_IF_MATCH,
      response: true,
    },
    gameId: segments[1]!,
  };
  switch (segments[4] ? `${segments[3]}/${segments[4]}` : segments[3]) {
    case 'setup': {
      const route: Route<SetupMoveRequest, StateResponse> = {
        handler: MoveApi.setup,
        validator: Validator.validateSetupMoveRequest,
        filters,
      };
      return route as RMS;
    }
    case 'roll': {
      const route: Route<_BodyLessMoveRequest, StateResponse> = {
        handler: MoveApi.roll,
        filters,
      };
      return route as RMS;
    }
    case 'road': {
      const route: Route<RoadRequest, StateResponse> = {
        handler: MoveApi.road,
        validator: Validator.validateRoadRequest,
        filters,
      };
      return route as RMS;
    }
    case 'house': {
      const route: Route<HouseRequest, StateResponse> = {
        handler: MoveApi.house,
        validator: Validator.validateHouseRequest,
        filters,
      };
      return route as RMS;
    }
    case 'dev/buy': {
      const route: Route<_BodyLessMoveRequest, StateResponse> = {
        handler: MoveApi.devBuy,
        filters,
      };
      return route as RMS;
    }
    case 'dev/play': {
      const route: Route<DevCardRequest, StateResponse> = {
        handler: MoveApi.devPlay,
        validator: Validator.validateHouseRequest,
        filters,
      };
      return route as RMS;
    }
    case 'thief/drop': {
      const route: Route<ThiefDropRequest, StateResponse> = {
        handler: MoveApi.thiefDrop,
        validator: Validator.validateThiefDropRequest,
        filters,
      };
      return route as RMS;
    }
    case 'thief/play': {
      const route: Route<ThiefPlayRequest, StateResponse> = {
        handler: MoveApi.thiefPlay,
        validator: Validator.validateThiefPlayRequest,
        filters,
      };
      return route as RMS;
    }
    case 'trade/bank': {
      const route: Route<TradeBankRequest, StateResponse> = {
        handler: MoveApi.tradeBank,
        validator: Validator.validateTradeBankRequest,
        filters,
      };
      return route as RMS;
    }
    case 'trade/offer': {
      const route: Route<TradePlayerRequest, StateResponse> = {
        handler: MoveApi.tradePlayer,
        validator: Validator.validateTradePlayerRequest,
        filters,
      };
      return route as RMS;
    }
    case 'trade/respond': {
      const route: Route<TradeResponseRequest, StateResponse> = {
        handler: MoveApi.tradeResponse,
        validator: Validator.validateTradeResponseRequest,
        filters,
      };
      return route as RMS;
    }
    case 'end': {
      const route: Route<_BodyLessMoveRequest, StateResponse> = {
        handler: MoveApi.end,
        filters,
      };
      return route as RMS;
    }
  }
  return undefined;
}
