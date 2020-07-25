import type { AsyncOrSync, DeepReadonly } from 'ts-essentials';
import type { AuthenticatedRequest, CatanRequest, ETagRequest, GameRequest } from './model/request/index';
import type { CatanContext, CatanLogger } from './core/catan-context';
import type { CatanResponse, SwaggerResponse } from './model/response/index';
import type { Context, HttpRequest } from '@azure/functions';
import { METHOD_GET, METHOD_POST } from './util/constants';
import { NOT_FOUND, OK } from 'http-status-codes';
import type { Role, Token } from './model/user';
import { accessLog, requestLog } from './filter/tracing';
import { authenticate, authorize } from './filter/auth';
import { CatanError } from './core/catan-error';
import { StrongEntity } from './model/core';
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

type RouteHandler<T extends CatanRequest, U extends CatanResponse> =
  | AsyncOrSync<U>
  | ((context: CatanContext<T>) => AsyncOrSync<U>);

type Route<T extends CatanRequest, U extends CatanResponse> = DeepReadonly<
  {
    handler: RouteHandler<T, U>;
    req?: {
      headers?: string[];
      query?: string[];
    };
    filters?: {
      etag?: {
        response?: boolean;
      };
    };
  } & (T extends AuthenticatedRequest
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
    (T extends GameRequest
      ? {
          filters: {
            gameId: true;
          };
        }
      : {})
>;

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

function resolve(context: Context, req: HttpRequest, segments: PathSegments): Route<CatanRequest, CatanResponse> {
  return (
    routeBase(context, req, segments) || {
      handler: { code: NOT_FOUND, message: 'Unknown Route' },
    }
  );
}

function routeBase(
  context: Context,
  req: HttpRequest,
  segments: PathSegments
): Route<CatanRequest, CatanResponse> | undefined {
  switch (segments[0]) {
    case undefined:
      return undefined;
    case 'game':
      return routeGame(context, req, segments);
    case 'user':
      return routeUser(context, req, segments);
    case 'ping':
      return {
        handler: 'pong',
      };
    case 'admin':
      // TODO
      break;
  }
  return undefined;
}

function routeUser(
  context: Context,
  req: HttpRequest,
  segments: PathSegments
): Route<CatanRequest, CatanResponse> | undefined {
  // TODO:
  return undefined;
}

function routeGame(
  context: Context,
  req: HttpRequest,
  segments: PathSegments
): Route<CatanRequest, CatanResponse> | undefined {
  // TODO:
  return undefined;
}

/* Route Execution */

function isStrongEntity<T extends CatanResponse & (StrongEntity | {})>(val: T): val is T & StrongEntity {
  return 'etag' in val;
}

function requiresAuth<T extends Route<CatanRequest, CatanResponse> | Route<AuthenticatedRequest, CatanResponse>>(
  route: T
): route is T & Route<AuthenticatedRequest, CatanResponse> {
  return (route.filters && 'authenticate' in route.filters && route.filters.authenticate) || false;
}

async function execute(
  route: Route<CatanRequest, CatanResponse>,
  segments: PathSegments,
  req: HttpRequest,
  logger: CatanLogger,
  start: [number, number]
): Promise<CatanHttpResponse> {
  // Azure Response fields
  let status = 200;
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

    if (typeof route.handler === 'function') {
      // Context vars
      const params: Record<string, string | undefined> = {};
      let gameId: string | undefined;
      let etag: string | undefined;
      // Route Filters
      if (route.filters) {
        // Game Id
        if ('gameId' in route.filters && route.filters.gameId) {
          gameId = segments[1];
        }
        // ETag
        if (route.filters.etag && 'request' in route.filters.etag) {
          etag = req.headers[route.filters.etag.request];
        }
      }
      // Params
      if (route.req) {
        route.req.headers?.forEach(x => (params[x] = req.headers[x]));
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
      body = await route.handler(context as CatanContext<CatanRequest>);
    } else {
      const x = await route.handler;
      body = x;
    }

    // ETag Response Header
    if (route.filters && route.filters.etag && route.filters.etag.response && isStrongEntity(body) && body.etag) {
      headers.etag = body.etag;
      delete (body as StrongEntity).etag;
    }
  } catch (e) {
    // Error Handling
    const error: CatanError = e instanceof CatanError ? e : new CatanError('Uncaught Error', e);
    status = error.errorStatus;
    if (status / 100 === 4) {
      // Client Error - Warning
      logger.warn('Error Handler', error);
    } else {
      // Server Error - Error
      logger.error('Error Handler', error);
    }
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

export function router(context: Context, req: HttpRequest): Promise<CatanHttpResponse> {
  const start = process.hrtime();
  const segments = createPathSegments(req);
  return (
    swagger(context, req, segments) ||
    execute(resolve(context, req, segments), segments, req, createLogger(context), start)
  );
}
