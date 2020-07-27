import { BadRequestError, CatanError } from '../../core/catan-error';
import { NAME_REGEX, USER_ID_REGEX } from '../../util/constants';
import { CONFLICT } from 'http-status-codes';
import type { SignUpRequest } from '../../model/request/user-request';
import type { User } from '../../model/user';
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
    const error = CatanError.wrap(e);
    if (error.errorStatus === CONFLICT) {
      throw new CatanError('User id is already taken!', CONFLICT, error);
    } else {
      throw error;
    }
  }
}
