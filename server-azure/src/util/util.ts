import type { Primitive, ValueOf } from 'ts-essentials';
import { uuid } from 'uuidv4';

export function getFrequencyMapTotalCount(map: { [_: string]: number | undefined }): number {
  return Object.values(map).reduce<number>((a, b) => (b ? a + b : a), 0);
}

export function count<T>(arr: T[], filter: (_: T) => boolean): number {
  return arr.reduce((count, item) => (filter(item) ? count + 1 : count), 0);
}

export function shuffle<T>(arr: T[]): T[] {
  // knuth-fisher-yates
  for (let n = arr.length; --n; ) {
    const i = ~~(Math.random() * (n + 1));
    const t = arr[n];
    arr[n] = arr[i];
    arr[i] = t;
  }
  return arr;
}

export function createEnumMap<E extends Record<string, string>, T>(
  enumType: E,
  mapper: (_: ValueOf<E>) => T
): Record<ValueOf<E>, T> {
  const map: Partial<Record<ValueOf<E>, T>> = {};
  for (const key of Object.values(enumType)) {
    map[key as ValueOf<E>] = mapper(key as ValueOf<E>);
  }
  return map as Record<ValueOf<E>, T>;
}

export function createPartialEnumMap<E extends Record<string, string>, T>(
  enumType: E,
  mapper: (_: ValueOf<E>) => T | undefined
): Partial<Record<ValueOf<E>, T>> {
  const map: Partial<Record<ValueOf<E>, T>> = {};
  for (const key of Object.values(enumType)) {
    const val = mapper(key as ValueOf<E>);
    if (val !== undefined) {
      map[key as ValueOf<E>] = val;
    }
  }
  return map;
}

export function base64Encode(val: string): string {
  return Buffer.from(val).toString('base64');
}

export function base64Decode(val: string): string {
  return Buffer.from(val, 'base64').toString('utf-8');
}

export function generateRandomUuid(): string {
  return uuid();
}

export function arrayEquals<T extends Primitive>(a: Readonly<T[]> | undefined, b: Readonly<T[]> | undefined): boolean {
  if (a === b) {
    return true;
  }
  if (a === undefined || b === undefined || a.length !== b.length) {
    return false;
  }
  for (let i = 0; i < a.length; i++) {
    if (a[i] !== b[i]) {
      return false;
    }
  }
  return true;
}

export function addToFrequencyMap<T extends string>(map: Partial<Record<T, number>>, key: T, delta: number): void {
  map[key] = (map[key] || 0)! + delta;
}
