import type { MessageResponse } from '../model/response/message-response';
import { OK } from 'http-status-codes';
import type { RouteHandler } from '../model/core';
import type { SignUpRequest } from '../model/request/user-request';
import { createMessageResponse } from '../model/response/message-response';

export const signup: RouteHandler<SignUpRequest, MessageResponse> = async context => {
  // TODO: impl
  return createMessageResponse(OK, 'User Created Successfully!');
};
