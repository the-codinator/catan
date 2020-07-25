import { IdentifiableEntity, StrongEntity } from '../core';
import type { Color } from './color';
import type { DevCard } from './dev-card';
import type { Hand } from './hand';

export interface BaseState extends IdentifiableEntity, StrongEntity {
  thief: number; // TODO
}

export interface State extends BaseState {
  bankDevCards: DevCard[];
  hands: Record<Color, Hand>;
}
