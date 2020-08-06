import type { AuthenticatedRequest, BodyLessRequest, CatanRequest, ETagRequest, GameRequest } from '../model/request';
import type { CatanResponse, SwaggerResponse } from '../model/response';
import type { RouteHandler, StrongEntity } from '../model/core';

import type { DeepReadonly } from 'ts-essentials';
import type { Role } from '../model/user';

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

export type Route<T extends CatanRequest, U extends CatanResponse> = {
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
  } & (T extends Exclude<CatanRequest, BodyLessRequest>
    ? {
        validator: (request: T) => boolean;
      }
    : {}) &
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

export type PathSegments = Readonly<{
  [_: number]: string | undefined;
  length: number;
  method: string;
  path: string;
}>;
