import type { Token, User } from '../../model/user';
import type { Board } from '../../model/game/board';
import { CosmosDBCachingCDC } from './cosmosdb-caching-cdc';
import type { State } from '../../model/game/state';

export interface CatanDataConnector {
  getUser(id: string, skipCache?: boolean): Promise<User>;
  getUsers(ids: string[]): Promise<User[]>;
  createUser(user: User): Promise<void>;
  // void updateUser(User user) throws CatanException;
  // void deleteUser(String id) throws CatanException;

  // List<String> getGames(String userId, Boolean ongoing) throws CatanException;

  getToken(id: string): Promise<Token>;
  createToken(token: Token): Promise<void>;
  deleteToken(id: string): Promise<void>;

  getBoard(id: string): Promise<Board>;
  createBoard(board: Board): Promise<void>;
  // void deleteBoard(String id) throws CatanException;

  getState(id: string, etag: string | undefined): Promise<State | undefined>;
  createState(state: State): Promise<void>;
  // void updateState(State state) throws CatanException;
  // void deleteState(String id, String etag) throws CatanException;
}

const dataConnector: CatanDataConnector = new CosmosDBCachingCDC();
export default dataConnector;
