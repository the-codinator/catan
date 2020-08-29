import type { DevCard } from './dev-card';
import type { Resource } from './resource';

export interface Hand {
  resources: Partial<Record<Resource, number>>;
  devCards: DevCard[];
}
