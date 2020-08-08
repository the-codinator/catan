import type { AdminAction } from './admin-action';
import type { DeepReadonly } from 'ts-essentials';
import type { State } from '../game/state';

export type AdminRequest = DeepReadonly<{
  id: string;
  action: AdminAction;
  state?: State;
}>;
