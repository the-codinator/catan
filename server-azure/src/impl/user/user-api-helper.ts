import * as SessionHelper from './session-helper';
import { BadRequestError, CatanError } from '../../core/catan-error';
import { CONFLICT, INTERNAL_SERVER_ERROR, NOT_FOUND, UNAUTHORIZED } from 'http-status-codes';
import type { LoginRequest, SignUpRequest } from '../../model/request/user-request';
import { NAME_REGEX, USER_ID_REGEX } from '../../util/constants';
import { Role, Token, TokenType, User } from '../../model/user';
import type { Buildable } from 'ts-essentials';
import type { SessionResponse } from '../../model/response/session-response';
import dataConnector from '../data/catan-data-connector';

let newUserEventListener: undefined | ((user: User) => void);

export function setNewUserEventListener(listener: (user: User) => void): void {
  newUserEventListener = listener;
}

function validateUserId(id: string): void {
  if (!USER_ID_REGEX.test(id)) {
    throw new BadRequestError(
      'User Id must only contain (english) alphabets, (arabic) numerals, hyphen (-), underscore(_), and must be between 3 and 12 characters'
    );
  }
}

function validatePwd(pwd: string): void {
  // Note: `pwd` here is the password hash
  if (pwd.length < 3 || pwd.length > 100) {
    throw new BadRequestError('Please use a reasonable passwords');
  }
}

function validateName(name: string): void {
  if (!NAME_REGEX.test(name)) {
    throw new BadRequestError('Name does not belong to a human');
  }
}

function validateCredentials(actual: User, expected: User): void {
  if (actual.id !== expected.id) {
    // Ideally shouldn't happen
    throw new CatanError('Expected user is different');
  }
  if (actual.pwd !== expected.pwd) {
    throw new CatanError('Username/Password Mismatch', UNAUTHORIZED);
  }
}

export async function signup(request: SignUpRequest): Promise<void> {
  validateUserId(request.id);
  validateName(request.name);
  validatePwd(request.pwd);
  const user: User = { id: request.id, name: request.name, pwd: request.pwd };
  if (newUserEventListener) {
    newUserEventListener(user);
  }
  try {
    await dataConnector.createUser(user);
  } catch (e) {
    const error = CatanError.from(e);
    if (error.errorStatus === CONFLICT) {
      throw new CatanError('User id is already taken!', CONFLICT, error);
    } else {
      throw error;
    }
  }
}

export async function login(request: LoginRequest, rememberMe: boolean): Promise<SessionResponse> {
  validateUserId(request.id);
  validatePwd(request.pwd);
  const user: User = { id: request.id, name: '', pwd: request.pwd };
  return loginInternal(user, rememberMe);
}

async function loginInternal(requestUser: User, rememberMe: boolean): Promise<SessionResponse> {
  let dbUser: User;
  try {
    dbUser = await dataConnector.getUser(requestUser.id);
  } catch (e) {
    const error = CatanError.from(e);
    if (error.errorStatus === NOT_FOUND) {
      throw new CatanError('Username/Password Mismatch', UNAUTHORIZED, error);
    } else {
      throw error;
    }
  }
  validateCredentials(requestUser, dbUser);
  return createSessionInternal(dbUser, rememberMe);
}

async function createSessionInternal(dbUser: User, rememberMe: boolean): Promise<SessionResponse> {
  const accessToken = SessionHelper.createSession(dbUser, TokenType.access);
  let refreshToken: Token | undefined;
  if (rememberMe) {
    refreshToken = SessionHelper.createSession(dbUser, TokenType.refresh);
    (accessToken as Buildable<Token>).linkedId = refreshToken.id;
    (refreshToken as Buildable<Token>).linkedId = accessToken.id;
  }
  try {
    dataConnector.createToken(accessToken);
    if (refreshToken) {
      dataConnector.createToken(refreshToken);
    }
  } catch (e) {
    throw new CatanError('Error creating token', INTERNAL_SERVER_ERROR, e);
  }
  return {
    id: dbUser.id,
    name: dbUser.name,
    roles: dbUser.roles as Role[],
    created: accessToken.created,
    access_token: SessionHelper.serializeToken(accessToken),
    refresh_token: SessionHelper.serializeToken(refreshToken),
  };
}
