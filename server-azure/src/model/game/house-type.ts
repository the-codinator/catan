export enum HouseType {
  settlement = 'settlement',
  city = 'city',
}

export function getResourceMultiplier(type: HouseType): 1 | 2 {
  return type === HouseType.city ? 2 : 1;
}

export function getVictoryPoints(type: HouseType): 1 | 2 {
  return type === HouseType.city ? 2 : 1;
}
