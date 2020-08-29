import type { DeepReadonly } from 'ts-essentials';

export type User = Readonly<{
  id: string;
  name: string;
}>;

export type UserSession = DeepReadonly<{
  id: string;
  name: string;
  roles?: string[];
  created: number;
  // eslint-disable-next-line camelcase, @typescript-eslint/naming-convention
  access_token: string;
  // eslint-disable-next-line camelcase, @typescript-eslint/naming-convention
  refresh_token?: string;
}>;

export const ACCESS_TOKEN_VALIDITY = 86400000;
export const REFRESH_TOKEN_VALIDITY = 86400000 * 14; // 2 weeks
