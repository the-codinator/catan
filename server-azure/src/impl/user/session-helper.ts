import { Token, TokenType, User } from '../../model/user';
import { base64Decode, base64Encode, generateRandomUuid } from '../../util/util';

import { CatanError } from '../../core/catan-error';
import { DAY_MILLIS } from '../../util/constants';
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
    const object = JSON.parse(parsed);
    if (validateTokenModel(object)) {
      return object;
    } else {
      throw new CatanError('Token format is incorrect');
    }
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

// Manually generated using the validator generator (and then tweaked). Do NOT manually edit.
/* eslint-disable */
const hasOwn = Function.prototype.call.bind(Object.prototype.hasOwnProperty);
function validateTokenModel(data: any, recursive?: any): data is Token {
  const ref1 = function validate(data: any, recursive: any) {
    if (!(typeof data === 'string')) return false;
    if (!(data === 'access' || data === 'refresh')) return false;
    return true;
  };
  const ref0 = function validate(data: any, recursive: any) {
    if (!(typeof data === 'object' && data && !Array.isArray(data))) return false;
    if (!(data.created !== undefined && hasOwn(data, 'created'))) return false;
    if (!(data.expires !== undefined && hasOwn(data, 'expires'))) return false;
    if (!(data.id !== undefined && hasOwn(data, 'id'))) return false;
    if (!(data.type !== undefined && hasOwn(data, 'type'))) return false;
    if (!(data.user !== undefined && hasOwn(data, 'user'))) return false;
    if (!(typeof data.id === 'string')) return false;
    if (!ref1(data.type, recursive)) return false;
    if (!(typeof data.user === 'string')) return false;
    if (data.roles !== undefined && hasOwn(data, 'roles')) {
      if (!Array.isArray(data.roles)) return false;
      for (let i = 0; i < data.roles.length; i++) {
        if (data.roles[i] !== undefined && hasOwn(data.roles, i)) {
          if (!(typeof data.roles[i] === 'string')) return false;
          if (!(data.roles[i] === 'ADMIN')) return false;
        }
      }
    }
    if (!Number.isInteger(data.created)) return false;
    if (!Number.isInteger(data.expires)) return false;
    if (data.linkedId !== undefined && hasOwn(data, 'linkedId')) {
      if (!(typeof data.linkedId === 'string')) return false;
    }
    for (const key0 of Object.keys(data)) {
      if (
        key0 !== 'id' &&
        key0 !== 'type' &&
        key0 !== 'user' &&
        key0 !== 'roles' &&
        key0 !== 'created' &&
        key0 !== 'expires' &&
        key0 !== 'linkedId'
      )
        return false;
    }
    return true;
  };
  return ref0(data, recursive);
}
