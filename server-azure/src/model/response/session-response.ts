import type { DeepReadonly } from 'ts-essentials';

export type SessionResponse = DeepReadonly<{
  id: string;
  name: string;
  roles: string[];
  created: number;
  access_token: string;
  refresh_token?: string;
}>;
