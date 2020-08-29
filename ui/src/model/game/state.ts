import type { AchievementType, AchievementValue } from './achievement';

import type { Color } from './color';
import type { CurrentMove } from './current-move';
import type { DeepReadonly } from 'ts-essentials';
import type { DevCard } from './dev-card';
import type { Hand } from './hand';
import type { House } from './house';
import type { Phase } from './phase';
import type { Resource } from './resource';
import type { Road } from './road';

export type State = DeepReadonly<{
  id: string;
  phase: Phase;
  houses: Partial<Record<number, House>>;
  roads: Road[];
  thief: number;
  bank: Record<Resource, number>;
  playedDevCards: Partial<Record<Color, DevCard[]>>;
  achievements: Record<AchievementType, AchievementValue>;
  currentMove: CurrentMove;
  hand: Hand | undefined;
  playerResourceCounts: Partial<Record<Color, number>>;
  playerDevCardCounts: Partial<Record<Color, number>>;
}>;
