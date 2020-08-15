import { NOT_FOUND, UNAUTHORIZED } from 'http-status-codes';
import { Role, Token, TokenType, tokenEquals } from '../model/user';
import { parseToken, validateRequestTokenOffline } from './user/session-helper';

import { BEARER_PREFIX } from '../util/constants';
import { CatanError } from '../core/catan-error';
import type { User } from '../model/user';
import dataConnector from './data/catan-data-connector';

export async function authenticate(
  authorizationHeader: Readonly<string | undefined>
): Promise<Readonly<{ user: User; token: Token }>> {
  if (typeof authorizationHeader !== 'string') {
    throw new CatanError('Missing authorization header', UNAUTHORIZED);
  }
  if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
    throw new CatanError('Bad authorization header format', UNAUTHORIZED);
  }
  const token = parseToken(authorizationHeader.substring(BEARER_PREFIX.length));
  if (token.type !== TokenType.access) {
    throw new CatanError('Incorrect Token Type', UNAUTHORIZED);
  }
  validateRequestTokenOffline(token);
  let dbToken: Token;
  try {
    dbToken = await dataConnector.getToken(token.id);
  } catch (e) {
    if (e.errorStatus !== NOT_FOUND) {
      throw new CatanError('Error reading token data store', undefined, e);
    } else {
      throw e;
    }
  }
  if (!tokenEquals(token, dbToken)) {
    throw new CatanError('Invalid Access Token', UNAUTHORIZED);
  }
  const user = { id: token.user, name: '', roles: token.roles, pwd: '' };
  return { token, user };
}

export function authorize(user: Readonly<User | undefined>, roles: Readonly<Role[] | undefined>): void {
  if (!user || !roles || !roles.find(role => authorizeSingle(user, role))) {
    throw new CatanError('Authorization failed', UNAUTHORIZED);
  }
}

function authorizeSingle(user: User, role: Role): boolean {
  return (user.roles && user.roles.includes(role)) ?? false;
}
