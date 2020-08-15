import { AchievementType, getThreshold } from './achievement';
import { DevCard, createRandomDevCards } from './dev-card';
import type { BaseState } from './base-state';
import { Color } from './color';
import type { Hand } from './hand';
import { Phase } from './phase';
import { createCurrentMove } from './current-move';
import { createEnumMap } from '../../util/util';
import { createNewBank } from './resource';

export interface State extends BaseState {
  bankDevCards: DevCard[];
  hands: Record<Color, Hand>;
}

export function createState(id: string, color: Color, thief: number): State {
  return {
    id,
    phase: Phase.setup1,
    houses: {},
    roads: [],
    thief,
    bank: createNewBank(),
    playedDevCards: {},
    achievements: createEnumMap(AchievementType, type => ({ color: undefined, count: getThreshold(type) })),
    currentMove: createCurrentMove(color),
    bankDevCards: createRandomDevCards(),
    hands: createEnumMap(Color, () => ({ resources: {}, devCards: [] })),
  };
}
