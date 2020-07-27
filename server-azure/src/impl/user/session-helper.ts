import type { Token, User } from '../../model/user';
import { base64Decode, base64Encode, generateRandomUuid } from '../../util/util';

import { CatanError } from '../../core/catan-error';
import { DAY_MILLIS } from '../../util/constants';
import { TokenType } from '../../model/user';
import { UNAUTHORIZED } from 'http-status-codes';

export function createSession(user: User, type?: TokenType): Token {
  const created = Date.now();
  const id = generateRandomUuid();
  switch (type) {
    case TokenType.refresh:
      return {
        id,
        type: TokenType.refresh,
        user: user.id,
        roles: user.roles,
        created,
        expires: created + 14 * DAY_MILLIS,
      };
    case undefined:
    case TokenType.access:
    default:
      return { id, type: TokenType.access, user: user.id, roles: user.roles, created, expires: created + DAY_MILLIS };
  }
}

export function serializeToken(token: Token): string;
export function serializeToken(token: undefined): undefined;
export function serializeToken(token: Token | undefined): string | undefined;
export function serializeToken(token: Token | undefined): string | undefined {
  if (!token) {
    return undefined;
  }
  try {
    return base64Encode(JSON.stringify(token));
  } catch (e) {
    throw new CatanError('Error serializing session to token', undefined, e);
  }
}

export function parseToken(token: string): Token;
export function parseToken(token: undefined): undefined;
export function parseToken(token: string | undefined): Token | undefined;
export function parseToken(token: string | undefined): Token | undefined {
  if (!token) {
    return undefined;
  }
  try {
    const parsed = base64Decode(token);
    // TODO: Ensure Token schema matching
    return JSON.parse(parsed) as Token;
  } catch (e) {
    throw new CatanError('Error parsing session from token', UNAUTHORIZED, e);
  }
}

export function validateRequestTokenOffline(token: Token): void {
  const now = Date.now();
  if (token.created > now) {
    throw new CatanError('Session from the future', UNAUTHORIZED);
  }
  if (token.expires < now) {
    throw new CatanError('Session has expired', UNAUTHORIZED);
  }
}
