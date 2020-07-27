import type { AsyncOrSync } from 'ts-essentials';
import type { CatanContext } from '../core/catan-context';
import type { CatanRequest } from './request';
import type { CatanResponse } from './response';

export type RouteHandler<T extends CatanRequest, U extends CatanResponse> =
  | AsyncOrSync<U>
  | ((context: CatanContext<T>) => AsyncOrSync<U>);

export interface IdentifiableEntity {
  readonly id: string;
}

export interface StrongEntity {
  etag: string | undefined;
}
