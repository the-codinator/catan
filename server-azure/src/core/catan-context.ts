import type { AuthenticatedRequest, BodyLessRequest, CatanRequest, ETagRequest, GameRequest } from '../model/request';
import { CatanError } from './catan-error';
import type { DeepReadonly } from 'ts-essentials';
import type { Token } from '../model/user';
import { createNamespace } from 'cls-hooked';

export type CatanLogger = Readonly<{
  requestId: string;
  info(message: string): void;
  warn(message: string, error?: Error): void;
  error(message: string, error?: Error): void;
}>;

/*
export type CatanContext<T extends CatanRequest> = Readonly<{
  // Core
  logger: CatanLogger;
  params: Record<string, string | undefined>;
  request: T extends BodyLessRequest ? undefined : T;
  // Auth
  token: T extends AuthenticatedRequest ? Token : undefined;
  user: T extends AuthenticatedRequest ? string : undefined;
  // Game API
  gameId: T extends GameRequest ? string : undefined;
  etag: T extends ETagRequest ? string | undefined : undefined;
}>;
*/

export type CatanContext<T extends CatanRequest> = DeepReadonly<
  {
    logger: CatanLogger;
    params: Record<string, string | undefined>;
  } & (T extends AuthenticatedRequest
    ? {
        token: Token;
        user: string;
      }
    : {}) &
    (T extends GameRequest
      ? {
          gameId: string;
        }
      : {}) &
    (T extends ETagRequest
      ? {
          etag: string;
        }
      : {}) &
    (T extends BodyLessRequest
      ? {}
      : {
          request: T;
        })
>;

const ns = createNamespace('catan');
const key = 'context';

export function setContext(context: CatanContext<CatanRequest>, callback: () => void): void {
  ns.run(() => {
    ns.set(key, context);
    callback();
  });
}

export default function getContext<T extends CatanRequest>(): CatanContext<T> {
  if (ns && ns.active) {
    const context = ns.get(key);
    if (!context) {
      throw new CatanError('Context not defined');
    }
    return context;
  } else {
    throw new CatanError('Cannot access context outside call chain');
  }
}
