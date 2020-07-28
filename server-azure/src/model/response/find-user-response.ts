import type { DeepReadonly } from 'ts-essentials';

export type FindUserResponse = DeepReadonly<
  Array<{
    id: string;
    name: string;
  }>
>;
