import type { BodyLessRequest, CatanRequest } from './model/request/index';
import type { CatanContext, CatanLogger } from './core/catan-context';
import type { Context, HttpRequest } from '@azure/functions';
import { METHOD_GET, METHOD_POST } from './util/constants';
import { NOT_FOUND, OK } from 'http-status-codes';
import type { CatanResponse } from './model/response/index';
import { readFile } from 'fs';

export interface AzureHttpResponse {
  status?: number;
  headers?: Record<string, string | undefined>;
  body: CatanResponse;
}

type RouteHandler<T extends CatanRequest, U extends CatanResponse> =
  | U
  | Promise<U>
  | ((context: CatanContext<T>) => U | Promise<U>);

type Route<T extends CatanRequest, U extends CatanResponse> = Readonly<{
  handler: RouteHandler<T, U>;
  context: CatanContext<T>;
  authenticate?: boolean;
  authorize?: string[];
}>;

function createLogger(context: Context): CatanLogger {
  return {
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

function swagger(context: Context, req: HttpRequest, segments: string[]): Promise<AzureHttpResponse> | undefined {
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

function resolve(context: Context, req: HttpRequest, segments: string[]): Route<CatanRequest, CatanResponse> {
  return (
    routeBase(context, req, segments) || {
      handler: { code: NOT_FOUND, message: 'Unknown Route' },
      context: { logger: createLogger(context) },
    }
  );
}

function routeBase(
  context: Context,
  req: HttpRequest,
  segments: string[]
): Route<CatanRequest, CatanResponse> | undefined {
  const segment = segments.shift();
  if (!segment) {
    return undefined;
  }
  switch (segment) {
    case 'game':
      return routeGame(context, req, segments);
    case 'user':
      return routeUser(context, req, segments);
    case 'ping':
      return {
        handler: 'pong',
        context: { logger: createLogger(context) },
      };
    case 'admin':
      break;
  }
  return undefined;
}

function routeUser(
  context: Context,
  req: HttpRequest,
  segments: string[]
): Route<CatanRequest, CatanResponse> | undefined {
  // TODO:
  return undefined;
}

function routeGame(
  context: Context,
  req: HttpRequest,
  segments: string[]
): Route<CatanRequest, CatanResponse> | undefined {
  // TODO:
  return undefined;
}

/* Route Execution */

async function execute(route: Route<CatanRequest, CatanResponse>): Promise<AzureHttpResponse> {
  return { status: 501, body: { code: 501, message: 'Not yet Implemented' } };
}

/* Http Router */

export function router(context: Context, req: HttpRequest): Promise<AzureHttpResponse> {
  const segments: string[] = [
    req.params.pathSegment1,
    req.params.pathSegment2,
    req.params.pathSegment3,
    req.params.pathSegment4,
    req.params.pathSegment5,
    req.params.pathSegment6,
  ].filter(x => x !== undefined);

  return swagger(context, req, segments) || execute(resolve(context, req, segments));
}
