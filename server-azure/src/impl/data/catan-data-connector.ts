import type { Token, User } from '../../model/user';
import type { Board } from '../../model/game/board';
import { CosmosDBCachingCDC } from './cosmosdb-caching-cdc';
import type { State } from '../../model/game/state';

export interface CatanDataConnector {
  getUser(id: string, skipCache?: boolean): Promise<User>;
  getUsers(ids: string[]): Promise<User[]>;
  createUser(user: User): Promise<void>;
  updateUser(user: User): Promise<void>;
  deleteUser(id: string): Promise<void>;

  // List<String> getGames(String userId, Boolean ongoing) throws CatanException;

  getToken(id: string): Promise<Token>;
  createToken(token: Token): Promise<void>;
  deleteToken(id: string): Promise<void>;

  getBoard(id: string): Promise<Board>;
  createBoard(board: Board): Promise<void>;
  deleteBoard(id: string): Promise<void>;

  getState(id: string, etag: string | undefined): Promise<State | undefined>;
  createState(state: State): Promise<void>;
  updateState(state: State): Promise<void>;
  deleteState(id: string): Promise<void>;
}

const dataConnector: CatanDataConnector = new CosmosDBCachingCDC();
export default dataConnector;
