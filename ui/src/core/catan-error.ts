import type { MessageResponse } from './message-response';

export class CatanError extends Error {
  readonly errorStatus: number;

  constructor(message: string, errorStatus?: number, error?: Error) {
    super(errorStatus ? `[ code=${errorStatus} ] ${message}` : message);
    this.errorStatus = errorStatus ?? (error instanceof CatanError ? error.errorStatus : 0);
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
}

CatanError.prototype.name = 'CatanError';
