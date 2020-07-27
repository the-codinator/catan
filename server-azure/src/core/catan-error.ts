import { BAD_REQUEST, INTERNAL_SERVER_ERROR } from 'http-status-codes';
import type { MessageResponse } from '../model/response/message-response';

export const DEFAULT_ERROR_STATUS = INTERNAL_SERVER_ERROR;

export class CatanError extends Error {
  readonly errorStatus: number;

  constructor(message: string, errorStatus?: number, error?: Error) {
    super(errorStatus ? `[ code=${errorStatus} ] ${message}` : message);
    this.errorStatus = errorStatus || (error instanceof CatanError && error.errorStatus) || DEFAULT_ERROR_STATUS;
    if (error && error.stack) {
      if (this.stack) {
        this.stack = this.stack + '\n Caused By: ' + error.stack;
      } else {
        this.stack = error.stack;
      }
    }
  }

  public asMessageResponse(): MessageResponse {
    return {
      code: this.errorStatus,
      message: this.message.startsWith('[ code=') ? this.message.slice(this.message.indexOf(']') + 2) : this.message,
    };
  }

  static from(e: Error, message?: string): CatanError {
    return e instanceof CatanError ? e : new CatanError(message || 'Internal Error', undefined, e);
  }
}

CatanError.prototype.name = 'CatanError';

export class BadRequestError extends CatanError {
  constructor(message: string, error?: Error) {
    super(message, BAD_REQUEST, error);
  }
}

BadRequestError.prototype.name = 'BadRequestError';
