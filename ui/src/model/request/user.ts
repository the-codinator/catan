import type { DeepReadonly } from 'ts-essentials';

export type SignUpRequest = LoginRequest &
  DeepReadonly<{
    name: string;
  }>;

export type LoginRequest = DeepReadonly<{
  id: string;
  pwd: string;
}>;

export type RefreshTokenRequest = DeepReadonly<{
  // eslint-disable-next-line camelcase, @typescript-eslint/naming-convention
  refresh_token: string;
}>;
