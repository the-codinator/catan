import type { Resource } from './resource';

export interface Ports {
  ports21: Record<Resource, number>;
  ports31: number[];
}
