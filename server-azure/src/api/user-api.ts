import * as UserApiHelper from '../impl/user/user-api-helper';
import type { LoginRequest, SignUpRequest } from '../model/request/user-request';
import type { MessageResponse } from '../model/response/message-response';
import { OK } from 'http-status-codes';
import type { RouteHandler } from '../model/core';
import type { SessionResponse } from '../model/response/session-response';
import { createMessageResponse } from '../model/response/message-response';

export const signup: RouteHandler<SignUpRequest, MessageResponse> = async context => {
  await UserApiHelper.signup(context.request);
  return createMessageResponse(OK, 'User Created Successfully!');
};

export const login: RouteHandler<LoginRequest, SessionResponse> = async context => {
  return UserApiHelper.login(context.request, context.params.rememberMe?.toLowerCase() === 'true');
};
