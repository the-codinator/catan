import type { DeepReadonly, Opaque } from 'ts-essentials';

export type SignUpRequest = LoginRequest &
  DeepReadonly<{
    name: string;
  }>;

export type LoginRequest = DeepReadonly<{
  id: string;
  pwd: string;
}>;

export type RefreshTokenRequest = DeepReadonly<{
  refresh_token: string;
}>;

export type _LogoutRequest = Opaque<{}, '_LogoutRequest'>;
