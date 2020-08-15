export interface IdentifiableEntity {
  readonly id: string;
}

export interface StrongEntity {
  etag?: string;
}
