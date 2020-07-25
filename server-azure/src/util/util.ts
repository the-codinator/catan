export function getFrequencyMapTotalCount(map: { [_: string]: number | undefined }): number {
  return Object.values(map).reduce((a, b) => (a ? (b ? a + b : a) : b)) || 0;
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
  mapper: (_: keyof E) => T
): Record<keyof E, T> {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const map: any = {};
  for (const key in enumType) {
    map[key] = mapper(key);
  }
  return map;
}

export function createPartialEnumMap<E extends Record<string, string>, T>(
  enumType: E,
  mapper: (_: keyof E) => T | undefined
): Partial<Record<keyof E, T>> {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const map: any = {};
  for (const key in enumType) {
    const val = mapper(key);
    if (val !== undefined) {
      map[key] = val;
    }
  }
  return map;
}
