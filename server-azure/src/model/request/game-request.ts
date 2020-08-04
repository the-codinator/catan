import type { DeepReadonly, Opaque } from 'ts-essentials';
import type { Color } from '../game/color';
import type { DevCard } from '../game/dev-card';
import type { Resource } from '../game/resource';

export type _BodyLessMoveRequest = Opaque<{}, '_BodyLessMoveRequest'>;

export type SetupMoveRequest = DeepReadonly<{
  houseVertex: number;
  roadVertex: number;
}>;

export type RoadRequest = DeepReadonly<{
  vertex1: number;
  vertex2: number;
}>;

export type HouseRequest = DeepReadonly<{
  vertex: number;
}>;

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
