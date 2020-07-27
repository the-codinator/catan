import type { MessageResponse } from './message-response';
import type { StateResponse } from './state-response';

export type SwaggerResponse = Buffer;
export type StringResponse = string;
export type ETagResponse = StateResponse;

export type CatanResponse = MessageResponse | ETagResponse | StringResponse;
