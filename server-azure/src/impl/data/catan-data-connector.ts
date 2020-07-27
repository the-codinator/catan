import { Token, User } from '../../model/user';

import { Board } from '../../model/game/board';
import { CosmosDBCachingCDC } from './cosmosdb-caching-cdc';
import { State } from '../../model/game/state';

export interface CatanDataConnector {
  getUser(id: string, skipCache?: boolean): Promise<User>;
  // User[] getUsers(String... ids) throws CatanException;
  createUser(user: User): Promise<void>;
  // void updateUser(User user) throws CatanException;
  // void deleteUser(String id) throws CatanException;

  // List<String> getGames(String userId, Boolean ongoing) throws CatanException;

  getToken(id: string): Promise<Token>;
  createToken(token: Token): Promise<void>;
  // void deleteToken(String id) throws CatanException;

  getBoard(id: string): Promise<Board>;
  // void createBoard(Board board) throws CatanException;
  // void deleteBoard(String id) throws CatanException;

  getState(id: string, etag: string | undefined): Promise<State | undefined>;
  // void createState(State state) throws CatanException;
  // void updateState(State state) throws CatanException;
  // void deleteState(String id, String etag) throws CatanException;
}

const dataConnector: CatanDataConnector = new CosmosDBCachingCDC();
export default dataConnector;
