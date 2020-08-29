export interface MessageResponse {
  readonly code: number;
  readonly message: string;
}

export function createMessageResponse(code: number, message: string): MessageResponse {
  return { code, message };
}
