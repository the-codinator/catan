import type { IdentifiableEntity } from '../../model/core';

export class MyCache<T extends IdentifiableEntity> {
  private readonly map: Map<string, { val: T; expiry: number }>;
  private readonly expires: number; // seconds
  private readonly capacity: number;

  constructor(capacity: number, expires: number) {
    this.map = new Map();
    this.capacity = capacity;
    this.expires = expires;
  }

  private getExpiry(): number {
    // We dont care tooooooo much so we're dropping the nanoseconds part
    return process.hrtime()[0] + this.expires;
  }

  private clean(minimum = 0): void {
    const now = process.hrtime()[0];
    for (const [k, { expiry }] of this.map) {
      if (minimum <= 0 && expiry > now) {
        break;
      }
      minimum--;
      this.map.delete(k); // Apparently the iterator doesn't break!?!
    }
  }

  get(id: string): T | undefined {
    this.clean();
    const val = this.map.get(id)?.val;
    return val;
  }

  put(val: T): void {
    this.clean();
    this.map.set(val.id, { val, expiry: this.getExpiry() }); // Replaces if exists
    if (this.map.size > this.capacity) {
      this.clean(this.map.size - this.capacity);
    }
  }

  del(id: string): void {
    this.clean();
    this.map.delete(id);
  }
}
