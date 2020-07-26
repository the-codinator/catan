import { CatanError } from '../../core/catan-error';
import type { Token } from '../../model/user';
import { UNAUTHORIZED } from 'http-status-codes';
import { base64Decode } from '../../util/util';

export function parseToken(token: string): Token {
  try {
    const parsed = base64Decode(token);
    // TODO: Ensure Token schema matching
    return parsed as Token;
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
