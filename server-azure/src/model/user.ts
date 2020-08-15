import type { DeepReadonly } from 'ts-essentials';
import type { IdentifiableEntity } from './core';
import { arrayEquals } from '../util/util';

export type User = IdentifiableEntity &
  DeepReadonly<{
    name: string;
    pwd: string;
    roles?: Role[];
  }>;

export enum Role {
  ADMIN = 'ADMIN',
}

export enum TokenType {
  access = 'access',
  refresh = 'refresh',
}

export type Token = IdentifiableEntity &
  DeepReadonly<{
    type: TokenType;
    user: string;
    roles?: Role[];
    created: number;
    expires: number;
    linkedId?: string;
  }>;

export function tokenEquals(input: Token | undefined, expected: Token | undefined): boolean {
  return (
    (input &&
      expected &&
      input.id === expected.id &&
      input.type === expected.type &&
      input.user === expected.user &&
      arrayEquals(input.roles, expected.roles) &&
      input.created === expected.created &&
      input.expires === expected.expires &&
      input.linkedId === expected.linkedId) ||
    false
  );
}
