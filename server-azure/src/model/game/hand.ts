import type { DevCard } from './dev-card';
import type { Resource } from './resource';
import { getFrequencyMapTotalCount } from '../../util/util';

export interface Hand {
  resources: Record<Resource, number>;
  devCards?: DevCard[];
}

export function getResourceCount(hand: Hand): number {
  return getFrequencyMapTotalCount(hand.resources);
}

export function getDevCardCount(hand: Hand): number {
  return hand.devCards?.length || 0;
}
