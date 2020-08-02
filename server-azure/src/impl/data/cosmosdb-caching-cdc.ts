import { CONFLICT, CREATED, NOT_FOUND, NOT_MODIFIED, NO_CONTENT, OK, PRECONDITION_FAILED } from 'http-status-codes';
import type { Container, Item, ItemResponse, Items, RequestOptions, Resource } from '@azure/cosmos';
import {
  DB_ERROR_BAD_STATUS_CODE,
  DB_ERROR_MISSING_RESOURCE,
  DB_ERROR_UNKNOWN,
  ENTITY_CONFLICT,
  ENTITY_NOT_FOUND,
  ENTITY_PRECONDITION_FAILED,
  METHOD_DELETE,
  METHOD_GET,
  METHOD_PATCH,
  METHOD_POST,
  METHOD_PUT,
} from '../../util/constants';
import type { IdentifiableEntity, StrongEntity } from '../../model/core';
import type { Token, User } from '../../model/user';

import type { Board } from '../../model/game/board';
import type { CatanDataConnector } from './catan-data-connector';
import { CatanError } from '../../core/catan-error';
import { CosmosClient } from '@azure/cosmos';
import { MyCache } from './my-cache';
import type { State } from '../../model/game/state';
import { format } from 'util';

if (!process.env.CATAN_COSMOSDB_ENDPOINT || !process.env.CATAN_COSMOSDB_KEY) {
  throw new CatanError('Missing Cosmos DB Configs');
}
const DB_OPTIONS = { endpoint: process.env.CATAN_COSMOSDB_ENDPOINT, key: process.env.CATAN_COSMOSDB_KEY };
const DB_NAME = 'catan';
const COLLECTION_USERS_NAME = 'user';
const COLLECTION_TOKENS_NAME = 'token';
const COLLECTION_BOARDS_NAME = 'board';
const COLLECTION_STATES_NAME = 'state';

type TypedContainer<T extends IdentifiableEntity> = Omit<Container, 'item'> & {
  item: (
    id: string,
    partitionKeyValue?: string
  ) => Omit<Item, 'read'> & { read: (options?: RequestOptions) => Promise<ItemResponse<T>> };
} & {
  items: Omit<Items, 'upsert'> & {
    upsert: (body: T, options?: RequestOptions) => Promise<ItemResponse<T>>;
  };
};

type EntityStore<T extends IdentifiableEntity> = Readonly<{
  container: TypedContainer<T>;
  cache?: MyCache<T & Resource>;
  strong: T extends StrongEntity ? true : false;
}>;

function isStrongEntity<T extends IdentifiableEntity & (StrongEntity | {})>(
  strong: EntityStore<T>['strong'],
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  val: T
): val is T & StrongEntity {
  return strong;
}

function handlePopulateETag<T extends IdentifiableEntity>(
  strong: EntityStore<T>['strong'],
  val: T | undefined,
  resource: Resource | undefined
): void {
  if (val && resource && isStrongEntity(strong, val)) {
    val.etag = resource._etag;
  }
}

function requestOptions(etag: string | undefined, ifMatch: boolean): RequestOptions | undefined {
  if (etag) {
    return {
      accessCondition: {
        type: ifMatch ? 'IfMatch' : 'IfNoneMatch',
        condition: etag,
      },
    };
  } else {
    return undefined;
  }
}

export class CosmosDBCachingCDC implements CatanDataConnector {
  private readonly $client: CosmosClient;
  private readonly users: EntityStore<User>;
  private readonly tokens: EntityStore<Token>;
  private readonly boards: EntityStore<Board>;
  private readonly states: EntityStore<State>;

  constructor() {
    this.$client = new CosmosClient(DB_OPTIONS);
    const database = this.$client.database(DB_NAME);
    this.users = {
      container: database.container(COLLECTION_USERS_NAME),
      cache: new MyCache(100, 86400), // 1 day
      strong: false,
    };
    this.tokens = {
      container: database.container(COLLECTION_TOKENS_NAME),
      cache: new MyCache(200, 900), // 15 min,
      strong: false,
    };
    this.boards = {
      container: database.container(COLLECTION_BOARDS_NAME),
      cache: new MyCache(50, 21600), // 6 hr
      strong: false,
    };
    this.states = {
      container: database.container(COLLECTION_STATES_NAME),
      strong: true,
    };
  }

  /*
    NOTES: Cosmos DB item operations
    --------------------------------

    Code: response.statusCode / error.code
    Syntax: success / failure / failure with option (unspecified option = unsupported, ignores option)
    Providing wrong/undefined pid = failure case

     GET     c.item(id, pid).read(opts)          -- 200 resource / 404 / IfNoneMatch 304 null
     POST    c.items.create(obj)                 -- 201 resource / error 409
     PATCH   c.item(id, pid).replace(obj, opts)  -- 200 resource / error 404 / IfMatch error 412 / wrong obj.id error 400
     PUT     c.items.upsert(obj, opts)           -- 200 (update) 201 (create) resource / IfMatch (exists) error 412
     DELETE  c.item(id, pid).delete(opts)        -- 204 null / error 404 / IfMatch error 412
  */

  /* Generic Cosmos DB CRUD Utilities */

  // GET     c.item(id, pid).read(opts)          -- 200 resource / 404 / IfNoneMatch 304 null
  private async get<T extends IdentifiableEntity>(store: EntityStore<T>, id: string): Promise<T & Resource>;
  private async get<T extends IdentifiableEntity & StrongEntity>(
    store: EntityStore<T>,
    id: string,
    etag: string | undefined
  ): Promise<(T & Resource) | undefined>;
  private async get<T extends IdentifiableEntity>(
    { container, strong }: EntityStore<T>,
    id: string,
    etag?: string
  ): Promise<T extends StrongEntity ? (T & Resource) | undefined : T & Resource> {
    try {
      const options = requestOptions(etag, false);
      const { resource, statusCode } = await container.item(id, id).read(options);
      if (statusCode === NOT_FOUND) {
        throw new CatanError(format(ENTITY_NOT_FOUND, container.id, id), NOT_FOUND);
      } else if (statusCode !== OK && statusCode !== NOT_MODIFIED) {
        throw new CatanError(format(DB_ERROR_BAD_STATUS_CODE, METHOD_GET, statusCode));
      }
      if (!strong && !resource) {
        throw new CatanError(format(DB_ERROR_MISSING_RESOURCE, METHOD_GET));
      }
      handlePopulateETag(strong, resource, resource);
      // I couldn't figure out how to remove the `as`, but this typing is true
      return resource as T extends StrongEntity ? (T & Resource) | undefined : T & Resource;
    } catch (e) {
      if (e instanceof CatanError) {
        throw e;
      } else {
        throw new CatanError(format(DB_ERROR_UNKNOWN, METHOD_GET), undefined, e);
      }
    }
  }

  private async getWithCacheWithoutETag<T extends IdentifiableEntity>(
    store: EntityStore<T>,
    id: string
  ): Promise<T & Resource> {
    const { cache } = store;
    const val = cache?.get(id) || (await this.get(store, id));
    cache?.put(val);
    return val;
  }

  // POST    c.items.create(obj)                 -- 201 resource / error 409
  private async create<T extends IdentifiableEntity>(
    { container, strong }: EntityStore<T>,
    val: T
  ): Promise<T & Resource> {
    try {
      const { resource, statusCode } = await container.items.create(val);
      if (statusCode !== CREATED) {
        throw new CatanError(format(DB_ERROR_BAD_STATUS_CODE, METHOD_POST, statusCode));
      }
      if (!resource) {
        throw new CatanError(format(DB_ERROR_MISSING_RESOURCE, METHOD_POST));
      }
      handlePopulateETag(strong, resource, resource);
      return resource;
    } catch (e) {
      if (e instanceof CatanError) {
        throw e;
      } else if (e.code === CONFLICT) {
        throw new CatanError(format(ENTITY_CONFLICT, container.id, val.id), CONFLICT, e);
      } else {
        throw new CatanError(format(DB_ERROR_UNKNOWN, METHOD_POST), undefined, e);
      }
    }
  }

  // PATCH   c.item(id, pid).replace(obj, opts)  -- 200 resource / error 404 / IfMatch error 412 / wrong obj.id error 400
  // PUT     c.items.upsert(obj, opts)           -- 200 (update) 201 (create) resource / IfMatch (exists) error 412
  private async update<T extends IdentifiableEntity>(
    { container, cache, strong }: EntityStore<T>,
    val: T,
    method: string,
    loader: (val: T, options?: RequestOptions) => Promise<ItemResponse<T>>,
    statuses: number[],
    returner?: number
  ): Promise<boolean> {
    let etag: string | undefined = undefined;
    try {
      if (isStrongEntity(strong, val)) {
        etag = val.etag;
        // Remove etag before write
        delete val.etag;
      }
      const options = requestOptions(etag, true);
      const { resource, statusCode } = await loader(val, options);
      if (!resource) {
        throw new CatanError(format(DB_ERROR_MISSING_RESOURCE, method));
      }
      if (!statuses.includes(statusCode)) {
        throw new CatanError(format(DB_ERROR_BAD_STATUS_CODE, method, statusCode));
      }
      handlePopulateETag(strong, val, resource);
      cache?.del(val.id);
      return returner ? statusCode === returner : false;
    } catch (e) {
      if (isStrongEntity(strong, val)) {
        // Reinstate old etag on error
        val.etag = etag;
      }
      if (e instanceof CatanError) {
        throw e;
      } else if (e.code === PRECONDITION_FAILED) {
        throw new CatanError(format(ENTITY_PRECONDITION_FAILED, container.id, val.id), PRECONDITION_FAILED, e);
      } else {
        throw new CatanError(format(DB_ERROR_UNKNOWN, method), undefined, e);
      }
    }
  }

  // PATCH   c.item(id, pid).replace(obj, opts)  -- 200 resource / error 404 / IfMatch error 412 / wrong obj.id error 400
  private async patch<T extends IdentifiableEntity>(store: EntityStore<T>, val: T): Promise<void> {
    this.update(store, val, METHOD_PATCH, store.container.item(val.id, val.id).replace, [OK]);
  }

  // PUT     c.items.upsert(obj, opts)           -- 200 (update) 201 (create) resource / IfMatch (exists) error 412
  private async put<T extends IdentifiableEntity>(store: EntityStore<T>, val: T): Promise<boolean> {
    return this.update(store, val, METHOD_PUT, store.container.items.upsert, [OK, CREATED], CREATED);
  }

  // DELETE  c.item(id, pid).delete(opts)        -- 204 null / error 404 / IfMatch error 412
  private async delete<T extends IdentifiableEntity>(
    { container, cache }: EntityStore<T>,
    id: string,
    etag?: string
  ): Promise<void> {
    try {
      const options = requestOptions(etag, true);
      const { statusCode } = await container.item(id, id).delete(options);
      if (statusCode !== NO_CONTENT) {
        throw new CatanError(format(DB_ERROR_BAD_STATUS_CODE, METHOD_DELETE, statusCode));
      }
      cache?.del(id);
    } catch (e) {
      if (e instanceof CatanError) {
        throw e;
      } else if (e.code === PRECONDITION_FAILED) {
        throw new CatanError(format(ENTITY_PRECONDITION_FAILED, container.id, id), PRECONDITION_FAILED, e);
      } else {
        throw new CatanError(format(DB_ERROR_UNKNOWN, METHOD_DELETE), undefined, e);
      }
    }
  }

  /* Implementing Methods from CatanDataConnector */

  public getUser(id: string, skipCache = false): Promise<User> {
    if (skipCache) {
      return this.get(this.users, id);
    } else {
      return this.getWithCacheWithoutETag(this.users, id);
    }
  }

  public getUsers(ids: string[]): Promise<User[]> {
    // Maybe the query "select * from c where c.id = u1 or c.id = u2 ..." is better, but I'm unsure if it works cross-partition
    // For now, we're taking the easy approach with parallel GETs
    // Also, `this.getUser` will leverage the in-memory cache
    return Promise.all(
      ids.map(id =>
        this.getUser(id).catch(e => {
          if (e instanceof CatanError && e.errorStatus === NOT_FOUND) {
            return undefined;
          } else {
            throw e;
          }
        })
      )
    ).then(users => users.filter(user => user !== undefined) as User[]);
  }

  public async createUser(user: User): Promise<void> {
    await this.create(this.users, user);
  }

  public getToken(id: string): Promise<Token> {
    return this.getWithCacheWithoutETag(this.tokens, id);
  }

  public async createToken(token: Token): Promise<void> {
    await this.create(this.tokens, token);
  }

  public async deleteToken(id: string): Promise<void> {
    await this.delete(this.tokens, id);
  }

  public getBoard(id: string): Promise<Board> {
    return this.getWithCacheWithoutETag(this.boards, id);
  }

  public async createBoard(board: Board): Promise<void> {
    await this.create(this.boards, board);
  }

  public getState(id: string, etag: string | undefined): Promise<State | undefined> {
    return this.get(this.states, id, etag);
  }

  public async createState(state: State): Promise<void> {
    await this.create(this.states, state);
  }
}
