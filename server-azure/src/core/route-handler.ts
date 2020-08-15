import type { AsyncOrSync } from 'ts-essentials';
import type { CatanContext } from './catan-context';
import type { CatanRequest } from '../model/request';
import type { CatanResponse } from '../model/response';

export type RouteHandler<T extends CatanRequest, U extends CatanResponse> =
  | AsyncOrSync<U>
  | ((context: CatanContext<T>) => AsyncOrSync<U>);
