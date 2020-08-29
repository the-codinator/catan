import type { Color } from './color';

export interface House {
  color: Color;
  type: 'settlement' | 'city';
}
