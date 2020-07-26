import type { AuthenticatedRequest, BodyLessRequest, CatanRequest, ETagRequest, GameRequest } from '../model/request';
import type { DeepReadonly } from 'ts-essentials';
import type { Token } from '../model/user';

export type CatanLogger = Readonly<{
  requestId: string;
  info(message: string): void;
  warn(message: string, error?: Error): void;
  error(message: string, error?: Error): void;
}>;

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
