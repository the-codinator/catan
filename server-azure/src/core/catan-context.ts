import type { AuthenticatedRequest, BodyLessRequest, CatanRequest, ETagRequest, GameRequest } from '../model/request';
import type { DeepReadonly } from 'ts-essentials';
import type { Token } from '../model/user';

export interface CatanLogger {
  readonly requestId: string;
  info(message: string): void;
  warn(message: string, error?: Error): void;
  error(message: string, error?: Error): void;
}

// export type CatanContext<T extends CatanRequest> = DeepReadonly<
//   {
//     logger: CatanLogger;
//     params: Record<string, string | undefined>;
//   } & (T extends AuthenticatedRequest
//     ? DeepReadonly<{
//         token: Token;
//         user: string;
//       }>
//     : {}) &
//     (T extends GameRequest
//       ? DeepReadonly<{
//           gameId: string;
//         }>
//       : {}) &
//     (T extends ETagRequest
//       ? DeepReadonly<{
//           etag: string | undefined;
//         }>
//       : {}) &
//     (T extends BodyLessRequest
//       ? {}
//       : DeepReadonly<{
//           request: T;
//         }>)
// >;

export type CatanContext<T extends CatanRequest> = DeepReadonly<{
  logger: CatanLogger;
  params: Record<string, string | undefined>;
  token: T extends AuthenticatedRequest ? Token : undefined;
  user: T extends AuthenticatedRequest ? string : undefined;
  gameId: T extends GameRequest ? string : undefined;
  etag: T extends ETagRequest ? string | undefined : undefined;
  request: T extends BodyLessRequest ? undefined : T;
}>;
