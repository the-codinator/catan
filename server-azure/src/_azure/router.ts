import type { CatanHttpResponse, PathSegments } from './types';
import type { Context, HttpRequest } from '@azure/functions';

import type { CatanLogger } from '../core/catan-context';
import { METHOD_POST } from '../util/constants';
import { UNSUPPORTED_MEDIA_TYPE } from 'http-status-codes';
import { execute } from './executor';
import { resolve } from './resolver';
import { swagger } from './swagger';

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

function invalidPostRequestContentType(context: Context, req: HttpRequest): Promise<CatanHttpResponse> | undefined {
  if (req.method === METHOD_POST && req.body && req.headers['content-type']?.toLowerCase() !== 'application/json') {
    return Promise.resolve({
      status: UNSUPPORTED_MEDIA_TYPE,
      headers: { 'content-type': 'text/plain', 'x-request-id': context.invocationId },
      body: 'Request must be "Content-Type: application/json"',
    });
  } else {
    return undefined;
  }
}

export function router(context: Context, req: HttpRequest): Promise<CatanHttpResponse> {
  const start = process.hrtime();
  const segments = createPathSegments(req);
  return (
    invalidPostRequestContentType(context, req) ||
    swagger(context, req, segments) ||
    execute(resolve(req, segments), segments, req, createLogger(context), start)
  );
}
