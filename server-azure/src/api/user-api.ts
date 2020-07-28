import * as UserApiHelper from '../impl/user/user-api-helper';
import type { LoginRequest, RefreshTokenRequest, SignUpRequest } from '../model/request/user-request';
import type { AuthenticatedGetRequest } from '../model/request';
import type { FindUserResponse } from '../model/response/find-user-response';
import type { MessageResponse } from '../model/response/message-response';
import { OK } from 'http-status-codes';
import type { RouteHandler } from '../model/core';
import type { SessionResponse } from '../model/response/session-response';
import { createMessageResponse } from '../model/response/message-response';

export const signup: RouteHandler<SignUpRequest, MessageResponse> = async context => {
  await UserApiHelper.signup(context.request);
  return createMessageResponse(OK, 'User Created Successfully!');
};

export const login: RouteHandler<LoginRequest, SessionResponse> = context =>
  UserApiHelper.login(context.request, context.params.rememberMe?.toLowerCase() === 'true');

export const refresh: RouteHandler<RefreshTokenRequest, SessionResponse> = context =>
  UserApiHelper.refresh(context.request);

export const logout: RouteHandler<AuthenticatedGetRequest, MessageResponse> = async context => {
  await UserApiHelper.logout(context.token);
  return createMessageResponse(OK, 'User Logged Out Successfully!');
};

export const find: RouteHandler<AuthenticatedGetRequest, FindUserResponse> = context => {
  return UserApiHelper.find(context.params.user);
};
