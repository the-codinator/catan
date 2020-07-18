import type {
  AuthenticatedCatanRequest,
  BodyLessRequest,
  CatanRequest,
  ETagCatanRequest,
  GameCatanRequest
} from '../model/request';
import { CatanError } from './catan-error';
import type { Token } from '../model/user';
import { createNamespace } from 'cls-hooked';

export type CatanLogger = Readonly<{
  info(message: string): void;
  warn(message: string, error?: Error): void;
  error(message: string, error?: Error): void;
}>;

/*
export type CatanContext<T extends CatanRequest> = Readonly<{
  logger: CatanLogger;
  token: T extends AuthenticatedCatanRequest ? Token : never;
  user: T extends AuthenticatedCatanRequest ? string : never;
  gameId: T extends GameCatanRequest ? string : never;
  etag: T extends ETagCatanRequest ? string | undefined : never;
  request: T extends BodyLessRequest ? never : T;
  params?: Record<string, string | undefined>;
}>;
*/

export type CatanContext<T extends CatanRequest> = Readonly<
  {
    logger: CatanLogger;
    params?: Record<string, string | undefined>;
  } & (T extends AuthenticatedCatanRequest
    ? {
        token: Token;
        user: string;
      }
    : {}) &
    (T extends GameCatanRequest
      ? {
          gameId: string;
        }
      : {}) &
    (T extends ETagCatanRequest
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

export function setContext(context: CatanContext<CatanRequest>, callback: () => void) {
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
