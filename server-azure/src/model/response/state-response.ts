import type { BaseState, State } from '../game/state';
import { getDevCardCount, getResourceCount } from '../game/hand';
import { Color } from '../game/color';
import type { DeepReadonly } from 'ts-essentials';
import type { Hand } from '../game/hand';
import { createPartialEnumMap } from '../../util/util';

export type StateResponse = DeepReadonly<BaseState> &
  DeepReadonly<{
    hand: Hand | undefined;
    playerResourceCounts: Partial<Record<Color, number>>;
    playerDevCardCounts: Partial<Record<Color, number>>;
  }>;

export function createStateResponse(state: State, color: Color | undefined): StateResponse {
  // const { id, phase, houses, roads, thief, bank, playedDevCards, achievements, currentMove, etag } = state;
  const { id, thief, etag } = state;
  const hand = color && state.hands[color];
  const playerResourceCounts = createPartialEnumMap(Color, c =>
    c === color ? undefined : getResourceCount(state.hands[c])
  );
  const playerDevCardCounts = createPartialEnumMap(Color, c =>
    c === color ? undefined : getDevCardCount(state.hands[c])
  );
  return {
    id,
    // phase,
    // houses,
    // roads,
    thief,
    // bank,
    // playedDevCards,
    // achievements,
    // currentMove,
    etag,
    hand,
    playerResourceCounts,
    playerDevCardCounts,
  };
}
