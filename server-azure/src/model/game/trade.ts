import type { Color } from './color';
import type { Resource } from './resource';

export interface Trade {
  partner: Color;
  offeredByPartner: boolean;
  partnerResources: Partial<Record<Resource, number>>;
  turnResources: Partial<Record<Resource, number>>;
}
