import type { Color } from './color';

export enum AchievementType {
  longest_road = 'longest_road',
  largest_army = 'largest_army',
}

export const ACHIEVEMENTS = Object.freeze(Object.values(AchievementType));

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function getVictoryPoints(type: AchievementType): 2 {
  return 2;
}

export function getThreshold(type: AchievementType): 2 | 4 {
  return type === AchievementType.longest_road ? 4 : 2;
}

export interface AchievementValue {
  color: Color | undefined;
  count: number;
}
