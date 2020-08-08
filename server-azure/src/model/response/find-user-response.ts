import type { DeepReadonly } from 'ts-essentials';
import type { User } from '../user';

export type FindUserResponse = DeepReadonly<Array<Pick<User, 'id' | 'name'>>>;
