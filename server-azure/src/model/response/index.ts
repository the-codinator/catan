import MessageResponse from './message-response';

export type SwaggerResponse = Buffer;
export type StringResponse = string;

export type CatanResponse = MessageResponse | SwaggerResponse | StringResponse;
