import { CatanContext, CatanLogger } from './core/catan-context';
import { CatanRequest } from './model/request/index';
import { CatanResponse } from './model/response/index';
import { Context } from '@azure/functions';
import { HttpRequest } from '@azure/functions';

export type RouteHandler<T extends CatanRequest, U extends CatanResponse> = (
  context: CatanContext<T>,
  params?: Record<string, string>
) => U | Promise<U>;

export type Route<T extends CatanRequest, U extends CatanResponse> = Readonly<{
  handler: RouteHandler<T, U>;
  context: CatanContext<T>;
  params?: Record<string, string | undefined>;
  authenticate?: boolean;
  authorize?: string[];
}>;

function createLogger(context: Context): CatanLogger {
  return {
    info(message: string) {
      context.log.info(`[ ${context.invocationId} ] ${message}`);
    },
    warn(message: string, error?: Error) {
      if (error) {
        context.log.warn(`[ ${context.invocationId} ] ${message}`, error);
      } else {
        context.log.warn(`[ ${context.invocationId} ] ${message}`);
      }
    },
    error(message:string, error?: Error) {
      if (error) {
        context.log.warn(`[ ${context.invocationId} ] ${message}`, error);
      } else {
        context.log.warn(`[ ${context.invocationId} ] ${message}`);
      }
    }
  }
}


export function route(context: Context, req: HttpRequest): CatanResponse {
  const segments: string[] = [
    req.params.pathSegment1,
    req.params.pathSegment2,
    req.params.pathSegment3,
    req.params.pathSegment4,
    req.params.pathSegment5,
    req.params.pathSegment6
  ].filter(x => x !== undefined);

  switch(req.method)
}
