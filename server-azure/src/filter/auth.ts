import type { Role, Token, User } from '../model/user';
import { CatanError } from '../core/catan-error';
import { UNAUTHORIZED } from 'http-status-codes';

export async function authenticate(
  authorizationHeader: Readonly<string | undefined>
): Promise<Readonly<{ user: User; token: Token }>> {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return undefined as any;
  // TODO: auth impl
}

export function authorize(user: Readonly<User | undefined>, roles: Readonly<Role[] | undefined>): void {
  if (!user || !roles || !roles.find(role => authorizeSingle(user, role))) {
    throw new CatanError('Authorization failed', UNAUTHORIZED);
  }
}

function authorizeSingle(user: User, role: Role): boolean {
  return user.roles && user.roles.includes(role);
}
