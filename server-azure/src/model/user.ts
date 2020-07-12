import { IdentifiableEntity } from './core';

export type User = IdentifiableEntity &
  Readonly<{
    name: string;
    pwd: string;
    roles: Set<Role>;
  }>;

export enum Role {
  ADMIN = 'ADMIN'
}

export enum TokenType {
  access = 'access',
  refresh = 'refresh'
}

export type Token = IdentifiableEntity &
  Readonly<{
    type: TokenType;
    user: string;
    roles?: Set<Role>;
    created: number;
    expires: number;
    linkedId: string;
  }>;
