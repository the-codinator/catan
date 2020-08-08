import type { AdminAction } from './admin-action';
import type { DeepReadonly } from 'ts-essentials';
import type { IdentifiableEntity } from '../core';
import type { State } from '../game/state';

export type AdminRequest = IdentifiableEntity &
  DeepReadonly<{
    action: AdminAction;
    state?: State;
  }>;
