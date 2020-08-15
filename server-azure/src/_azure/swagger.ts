import type { CatanHttpResponse, PathSegments } from './types';
import type { Context, HttpRequest } from '@azure/functions';

import { METHOD_GET } from '../util/constants';
import { readFile } from 'fs';

const swaggerResources: Record<string, { path: string; type: string } | undefined> = {
  swagger: { path: 'index.html', type: 'text/html' },
  'swagger-static/swagger-ui.css': { path: 'ui.css', type: 'text/css' },
  'swagger-static/swagger-ui-standalone-preset.js': { path: 'standalone.js', type: 'application/javascript' },
  'swagger-static/swagger-ui-bundle.js': { path: 'bundle.js', type: 'application/javascript' },
  'swagger-static/favicon-16x16.png': { path: 'favicon16.png', type: 'image/png' },
  'swagger-static/favicon-32x32.png': { path: 'favicon32.png', type: 'image/png' },
  'swagger.json': { path: 'swagger.json', type: 'application/json' },
};

export function swagger(
  context: Context,
  req: HttpRequest,
  segments: PathSegments
): Promise<CatanHttpResponse> | undefined {
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
                'cache-control': 'max-age=7776000', // Cache for 90 days
                etag: '2020-08-08_19-30', // Change this if any file changes (Date_Time)
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
