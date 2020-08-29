import type { Color } from './color';

export type AchievementType = 'longest_road' | 'largest_army';

export interface AchievementValue {
  color: Color | undefined;
  count: number;
}
