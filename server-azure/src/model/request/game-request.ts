import type { DeepReadonly, Opaque } from 'ts-essentials';
import type { Color } from '../game/color';
import type { DevCard } from '../game/dev-card';
import type { Resource } from '../game/resource';

export type SetupMoveRequest = DeepReadonly<{
  houseVertex: number;
  roadVertex: number;
}>;

export type _RollRequest = Opaque<{}, 'RollRequest'>;

export type RoadRequest = DeepReadonly<{
  vertex1: number;
  vertex2: number;
}>;

export type HouseRequest = DeepReadonly<{
  vertex: number;
}>;

export type _DevBuyRequest = Opaque<{}, '_DevBuyRequest'>;

export type DevCardRequest = DeepReadonly<{
  type: DevCard;

  // Monopoly
  resource?: Resource;

  // Year of Plenty
  resource1?: Resource;
  resource2?: Resource;

  // Road Building
  road1?: RoadRequest;
  road2?: RoadRequest;

  // Knight
  hex?: number;
  victim?: Color;
}>;

export type ThiefDropRequest = DeepReadonly<{
  resources: Resource[];
}>;

export type ThiefPlayRequest = DeepReadonly<{
  hex: number;
  victim?: Color;
}>;

export type TradeBankRequest = DeepReadonly<{
  offer: Resource;
  count: number;
  ask: Color;
}>;

export type TradePlayerRequest = DeepReadonly<{
  partner: Color;
  offer: Resource[];
  ask: Resource[];
}>;

export type TradeResponseRequest = DeepReadonly<{
  id: string;
  accepted: Resource;
}>;

export type _EndTurnRequest = Opaque<{}, '_EndTurnRequest'>;
