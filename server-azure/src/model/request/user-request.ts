import { DeepReadonly } from 'ts-essentials';

export type SignUpRequest = LoginRequest &
  DeepReadonly<{
    name: string;
  }>;

export type LoginRequest = DeepReadonly<{
  id: string;
  pwd: string;
}>;
