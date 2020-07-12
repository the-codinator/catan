import { IdentifiableEntity, StrongEntity } from '../core';

export interface BaseState extends IdentifiableEntity, StrongEntity {
  x: '1';
}

export interface State extends BaseState {
  x: '1';
}
