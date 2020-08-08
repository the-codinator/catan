import * as UserApiHelper from '../impl/user/user-api-helper';
import type { LoginRequest, RefreshTokenRequest, SignUpRequest } from '../model/request/user-request';
import { MessageResponse, createMessageResponse } from '../model/response/message-response';
import type { AuthenticatedGetRequest } from '../model/request';
import type { FindUserResponse } from '../model/response/find-user-response';
import { OK } from 'http-status-codes';
import type { RouteHandler } from '../core/route-handler';
import type { SessionResponse } from '../model/response/session-response';
import type { UserGamesResponse } from '../model/response/user-games-response';

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

export const find: RouteHandler<AuthenticatedGetRequest, FindUserResponse> = context =>
  UserApiHelper.find(context.params.user);

export const games: RouteHandler<AuthenticatedGetRequest, UserGamesResponse> = context =>
  UserApiHelper.games(context.user, context.params.ongoing);
