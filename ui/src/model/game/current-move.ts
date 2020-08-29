import type { Color } from './color';
import type { DevCard } from './dev-card';
import type { Roll } from './roll';
import type { Trade } from './trade';

export interface CurrentMove {
  color: Color;
  roll?: Roll;
  devCard?: DevCard;
  activeTrades: Partial<Record<string, Trade>>;
  acceptedTrades: Trade[];
  thieved?: Color[];
}
