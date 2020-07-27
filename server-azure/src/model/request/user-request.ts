import { DeepReadonly } from 'ts-essentials';

export type LoginRequest = DeepReadonly<{
  id: string;
  pwd: string;
}>;

export type SignUpRequest = LoginRequest &
  DeepReadonly<{
    name: string;
  }>;
