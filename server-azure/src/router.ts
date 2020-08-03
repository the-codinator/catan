import * as BoardApi from './api/game/board-api';
import * as UserApi from './api/user-api';
import * as Validator from './model/request/generated-validator';
import type {
  AuthenticatedGetGameETagRequest,
  AuthenticatedGetGameRequest,
  AuthenticatedGetRequest,
  AuthenticatedRequest,
  BodyLessRequest,
  CatanRequest,
  ETagRequest,
  GameRequest,
} from './model/request/index';
import { BAD_REQUEST, NOT_FOUND, NO_CONTENT, OK, UNSUPPORTED_MEDIA_TYPE } from 'http-status-codes';
import { BadRequestError, CatanError } from './core/catan-error';
import type { BoardResponse, GameResponse } from './model/response/game-response';
import type { CatanContext, CatanLogger } from './core/catan-context';
import type { CatanResponse, SwaggerResponse } from './model/response/index';
import type { Context, HttpRequest } from '@azure/functions';
import { HEADER_IF_NONE_MATCH, METHOD_GET, METHOD_POST } from './util/constants';
import type { LoginRequest, RefreshTokenRequest, SignUpRequest, _LogoutRequest } from './model/request/user-request';
import type { Role, Token } from './model/user';
import type { RouteHandler, StrongEntity } from './model/core';
import { accessLog, requestLog } from './filter';
import { authenticate, authorize } from './impl/auth';
import type { BoardRequest } from './model/request/board-request';
import type { DeepReadonly } from 'ts-essentials';
import type { FindUserResponse } from './model/response/find-user-response';
import type { MessageResponse } from './model/response/message-response';
import type { SessionResponse } from './model/response/session-response';
import type { StateResponse } from './model/response/state-response';
import { readFile } from 'fs';

export interface CatanHttpResponse {
  status?: number;
  headers: CatanHttpResponseHeaders;
  body: CatanResponse | SwaggerResponse;
}

export interface CatanHttpResponseHeaders {
  'x-request-id': string;
  'content-type': string;
  etag?: string;
  [_: string]: string | undefined;
}

type Route<T extends CatanRequest, U extends CatanResponse> = {
  handler: RouteHandler<T, U>;
} & DeepReadonly<
  {
    filters?: {
      etag?: {};
    };
    req?: {
      headers?: string[];
      query?: string[];
    };
  } & (T extends BodyLessRequest
    ? {}
    : {
        validator: (request: T) => boolean;
      }) &
    (T extends AuthenticatedRequest
      ? {
          filters: {
            authenticate: true;
            authorize?: Role[];
          };
        }
      : {}) &
    (T extends ETagRequest
      ? {
          filters: {
            etag: {
              request: 'If-Match' | 'If-None-Match';
            };
          };
        }
      : {}) &
    (U extends StrongEntity
      ? {
          filters: {
            etag: {
              response: true;
            };
          };
        }
      : {}) &
    (T extends GameRequest
      ? {
          filters: {
            gameId: string;
          };
        }
      : {})
>;

type RCC = Route<CatanRequest, CatanResponse>;

function createLogger(context: Context): CatanLogger {
  return {
    requestId: context.invocationId,
    info(message: string): void {
      context.log.info(`[ ${context.invocationId} ] ${message}`);
    },
    warn(message: string, error?: Error): void {
      if (error) {
        context.log.warn(`[ ${context.invocationId} ] ${message}`, error);
      } else {
        context.log.warn(`[ ${context.invocationId} ] ${message}`);
      }
    },
    error(message: string, error?: Error): void {
      if (error) {
        context.log.error(`[ ${context.invocationId} ] ${message}`, error);
      } else {
        context.log.error(`[ ${context.invocationId} ] ${message}`);
      }
    },
  };
}

/* Swagger */

const swaggerResources: Record<string, { path: string; type: string } | undefined> = {
  swagger: { path: 'index.html', type: 'text/html' },
  'swagger-static/swagger-ui.css': { path: 'ui.css', type: 'text/css' },
  'swagger-static/swagger-ui-standalone-preset.js': { path: 'standalone.js', type: 'application/javascript' },
  'swagger-static/swagger-ui-bundle.js': { path: 'bundle.js', type: 'application/javascript' },
  'swagger-static/favicon-16x16.png': { path: 'favicon16.png', type: 'image/png' },
  'swagger-static/favicon-32x32.png': { path: 'favicon32.png', type: 'image/png' },
  'swagger.json': { path: 'swagger.json', type: 'application/json' },
};

function swagger(context: Context, req: HttpRequest, segments: PathSegments): Promise<CatanHttpResponse> | undefined {
  if (segments[0] && segments[0].startsWith('swagger') && req.method === METHOD_GET && segments.length <= 2) {
    const url = segments[1] ? `${segments[0]}/${segments[1]}` : segments[0];
    if (url === 'swagger.json') {
      context.log.info(`[ ${context.invocationId} ] Swagger Request`);
    }
    const resource = swaggerResources[url];
    if (resource) {
      return new Promise((resolve, reject) =>
        readFile('swagger/' + resource.path, undefined, (error, body) => {
          if (error) {
            reject(error);
          } else {
            const response = {
              headers: {
                'x-request-id': context.invocationId,
                'content-type': resource.type,
                'cache-control': 'max-age=2592000', // Cache for 30 days
                etag: '2020-07-18_06-19', // Change this if any file changes (Date_Time)
              },
              body,
            };
            if (url === 'swagger' || url === 'swagger.json') {
              // Validate html / json version for caching
              response.headers['cache-control'] = 'no-cache';
            }
            resolve(response);
          }
        })
      );
    }
  }
  return undefined;
}

/* Route Resolution */

function resolve(req: HttpRequest, segments: PathSegments): RCC {
  return (
    routeBase(req, segments) || {
      handler: { code: NOT_FOUND, message: 'Unknown Route' },
    }
  );
}

function routeBase(req: HttpRequest, segments: PathSegments): RCC | undefined {
  switch (segments[0]) {
    case 'game':
      return routeGame(req, segments);
    case 'user':
      return routeUser(req, segments);
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

function routeUser(req: HttpRequest, segments: PathSegments): RCC | undefined {
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

function routeGame(req: HttpRequest, segments: PathSegments): RCC | undefined {
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
            gameId: '', // For obvious reasons, "create" does not have a gameId
            etag: {
              response: true,
            },
          },
        };
        return route as RCC;
      } else if (segments.length >= 3) {
        return routeMove(req, segments);
      }
    }
  }
  return undefined;
}

function routeMove(req: HttpRequest, segments: PathSegments): RCC | undefined {
  const gameId = segments[1];
  // TODO: switch (segments[2]) {
  return undefined;
}

/* Route Execution */

function isStrongEntity<T extends CatanResponse & (StrongEntity | {})>(val: T): val is T & StrongEntity {
  return typeof val === 'object' && 'etag' in val;
}

function requiresAuth(
  route: RCC | Route<AuthenticatedRequest, CatanResponse>
): route is Route<AuthenticatedRequest, CatanResponse> {
  return (
    ('filters' in route && route.filters && 'authenticate' in route.filters && route.filters.authenticate) || false
  );
}

function requiresValidation(
  route: RCC | Route<Exclude<CatanRequest, BodyLessRequest>, CatanResponse>
): route is Route<Exclude<CatanRequest, BodyLessRequest>, CatanResponse> {
  return 'validator' in route && typeof route.validator === 'function';
}

async function execute(
  route: RCC,
  segments: PathSegments,
  req: HttpRequest,
  logger: CatanLogger,
  start: [number, number]
): Promise<CatanHttpResponse> {
  // Azure Response fields
  let status = OK;
  let user = '-';
  const headers: CatanHttpResponseHeaders = {
    'x-request-id': logger.requestId,
    'content-type': 'application/json',
  };
  let body: CatanResponse;

  try {
    // Auth
    let token: Token | undefined;
    if (requiresAuth(route)) {
      // AuthN
      const authInfo = await authenticate(req.headers.authorization);
      token = authInfo.token;
      user = authInfo.user.id;

      // AuthZ
      if (route.filters.authorize) {
        authorize(authInfo?.user, route.filters.authorize);
      }
    }

    // Access Log
    accessLog(logger, user, segments.method, segments.path);

    // Input Validation
    if (requiresValidation(route) && !route.validator(req.body)) {
      throw new BadRequestError('Incorrect Input Request Format');
    }

    // ETag (for response header in NO_CONTENT case)
    let etag: string | undefined;

    if (typeof route.handler === 'function') {
      // Context vars
      const params: Record<string, string | undefined> = {};
      let gameId: string | undefined;

      // Route Filters
      if (route.filters) {
        // Game Id
        if ('gameId' in route.filters) {
          gameId = route.filters.gameId;
        }

        // ETag
        if (route.filters.etag && 'request' in route.filters.etag) {
          etag = req.headers[route.filters.etag.request.toLowerCase()];
        }
      }

      // Params
      if (route.req) {
        route.req.headers?.forEach(x => (params[x] = req.headers[x.toLowerCase()]));
        route.req.query?.forEach(x => (params[x] = req.query[x]));
      }

      // Create Context
      const context = {
        logger,
        params,
        request: req.body,
        token,
        user,
        gameId,
        etag,
      };

      // Execute Response Handler
      body = await route.handler(context as CatanContext<CatanRequest>);
    } else {
      // Resolve Response
      const x = await route.handler;
      body = x;
    }

    // Empty Response Handling
    if (!body) {
      body = '';
      status = NO_CONTENT;
    }

    // ETag Response Header
    if (route.filters?.etag && 'response' in route.filters.etag && route.filters.etag.response) {
      if (isStrongEntity(body) && body?.etag) {
        headers.etag = body.etag;
        delete (body as StrongEntity).etag;
      } else if (etag && status === NO_CONTENT) {
        headers.etag = etag;
      }
    }
  } catch (e) {
    // Error Handling
    const error = e instanceof CatanError ? e : new CatanError('Unknown Error', undefined, e);
    status = error.errorStatus;

    // Log Error
    if (status < 500) {
      logger.warn('[ Client Error ]', error);
    } else {
      logger.error('[ Server Error ]', error);
    }

    // Resolve Error Response
    body = error.asMessageResponse();
  } finally {
    // Request Log
    requestLog(
      logger,
      user,
      segments.method,
      segments.path,
      req.headers['client-ip'],
      req.headers['user-agent'],
      status,
      start
    );
  }
  // Response
  return { status, headers, body };
}

/* Http Router */

type PathSegments = Readonly<{
  [_: number]: string | undefined;
  length: number;
  method: string;
  path: string;
}>;

function createPathSegments(req: HttpRequest): PathSegments {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const segments: any = [
    req.params.pathSegment1,
    req.params.pathSegment2,
    req.params.pathSegment3,
    req.params.pathSegment4,
    req.params.pathSegment5,
    req.params.pathSegment6,
  ].filter(x => x !== undefined);
  segments.method = req.method || 'null';
  segments.path = segments.join('/');
  return segments;
}

function invalidPostRequestContentType(
  context: Context,
  req: HttpRequest,
  segments: PathSegments
): Promise<CatanHttpResponse> | undefined {
  if (segments.path === 'user/logout') {
    // Special handling for body-less POST request
    return undefined;
  } else if (req.headers['content-type']?.toLowerCase() !== 'application/json') {
    return Promise.resolve({
      status: UNSUPPORTED_MEDIA_TYPE,
      headers: { 'content-type': 'text/plain', 'x-request-id': context.invocationId },
      body: 'Request payload must be "Content-Type: application/json"',
    });
  } else if (typeof req.body !== 'object') {
    return Promise.resolve({
      status: BAD_REQUEST,
      headers: { 'content-type': 'text/plain', 'x-request-id': context.invocationId },
      body: 'Request body is not a JSON',
    });
  } else {
    return undefined;
  }
}

export function router(context: Context, req: HttpRequest): Promise<CatanHttpResponse> {
  const start = process.hrtime();
  const segments = createPathSegments(req);
  return (
    (req.method === METHOD_POST && invalidPostRequestContentType(context, req, segments)) ||
    swagger(context, req, segments) ||
    execute(resolve(req, segments), segments, req, createLogger(context), start)
  );
}
