import type { AchievementType, AchievementValue } from './achievement';
import type { IdentifiableEntity, StrongEntity } from '../core';
import type { Color } from './color';
import type { CurrentMove } from './current-move';
import type { DevCard } from './dev-card';
import type { House } from './house';
import type { Phase } from './phase';
import type { Resource } from './resource';
import type { Road } from './road';

export interface BaseState extends IdentifiableEntity, StrongEntity {
  phase: Phase;
  houses: Partial<Record<number, House>>;
  roads: Road[];
  thief: number;
  bank: Record<Resource, number>;
  playedDevCards: Partial<Record<Color, DevCard[]>>;
  achievements: Record<AchievementType, AchievementValue>;
  currentMove: CurrentMove;
}
