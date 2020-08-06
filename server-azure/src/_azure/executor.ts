import type { AuthenticatedRequest, BodyLessRequest, CatanRequest } from '../model/request';
import { BadRequestError, CatanError } from '../core/catan-error';
import type { CatanHttpResponse, CatanHttpResponseHeaders, PathSegments, Route } from './types';
import { NO_CONTENT, OK } from 'http-status-codes';
import { accessLog, requestLog } from '../filter';
import { authenticate, authorize } from '../impl/auth';

import type { CatanLogger } from '../core/catan-context';
import type { CatanResponse } from '../model/response';
import type { HttpRequest } from '@azure/functions';
import type { StrongEntity } from '../model/core';
import type { Token } from '../model/user';

function isStrongEntity<T extends CatanResponse & (StrongEntity | {})>(val: T): val is T & StrongEntity {
  return typeof val === 'object' && 'etag' in val;
}

function requiresAuth(
  route: Route<CatanRequest, CatanResponse> | Route<AuthenticatedRequest, CatanResponse>
): route is Route<AuthenticatedRequest, CatanResponse> {
  return (
    ('filters' in route && route.filters && 'authenticate' in route.filters && route.filters.authenticate) || false
  );
}

function requiresValidation(
  route: Route<CatanRequest, CatanResponse> | Route<Exclude<CatanRequest, BodyLessRequest>, CatanResponse>
): route is Route<Exclude<CatanRequest, BodyLessRequest>, CatanResponse> {
  return 'validator' in route && typeof route.validator === 'function';
}

export async function execute(
  route: Route<CatanRequest, CatanResponse>,
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
      body = await route.handler(context);
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
