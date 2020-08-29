import type { Board } from '../model/game/board';
import type { User } from '../model/user';

interface LocalStorage {
  user?: {
    id: string;
    name: string;
    accessToken: string;
    accessTokenExpiry: number;
    refreshToken: string;
    refreshTokenExpiry: number;
  };
  userCache: LocalCache<User>;
  boardCache: LocalCache<Board>;
}

type LocalCache<T> = Partial<Record<string, { data: T; expiry: number }>>;

const KEY = 'catan';
const TTL = 604800000; // 1 week

(function init() {
  if (!localStorage.getItem(KEY)) {
    const ls: LocalStorage = {
      userCache: {},
      boardCache: {},
    };
    localStorage.setItem(KEY, JSON.stringify(ls));
  } else {
    groomCache();
  }
  setInterval(groomCache, 86400000); // daily if tab remains open
})();

function get(): LocalStorage {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  return JSON.parse(localStorage.getItem(KEY)!);
}

function set(ls: LocalStorage): void {
  localStorage.setItem(KEY, JSON.stringify(ls));
}

function patch(handler: (ls: LocalStorage) => void): void {
  const ls = get();
  handler(ls);
  set(ls);
}

function groomCache(): void {
  patch(ls => {
    const now = Date.now();
    for (const cache of [ls.userCache, ls.boardCache]) {
      for (const id in cache) {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        if (cache[id]!.expiry < now) {
          delete cache[id];
        }
      }
    }
  });
}

function createCachedData<T>(data: T): LocalCache<T>['id'] {
  return { data, expiry: Date.now() + TTL };
}

export function getLsUserData(): LocalStorage['user'] {
  return get().user;
}

export function setLsUserData(user: LocalStorage['user']): void {
  patch(ls => (ls.user = user));
}

export function getLsCachedUser(id: string): LocalStorage['userCache']['id'] {
  return get().userCache[id];
}

export function setLsCachedUser(id: string, user: User): void {
  patch(ls => (ls.userCache[id] = createCachedData(user)));
}

export function getLsCachedBoard(id: string): LocalStorage['boardCache']['id'] {
  return get().boardCache[id];
}

export function setLsCachedBoard(id: string, board: Board): void {
  patch(ls => (ls.boardCache[id] = createCachedData(board)));
}
