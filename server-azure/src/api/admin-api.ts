import { BadRequestError, CatanError } from '../core/catan-error';
import { Role, User } from '../model/user';
import { AdminAction } from '../model/admin/admin-action';
import type { AdminRequest } from '../model/request';
import type { CatanContext } from '../core/catan-context';
import type { DeepWritable } from 'ts-essentials';
import { NOT_IMPLEMENTED } from 'http-status-codes';
import { OutOfTurnApi } from '../model/game/out-of-turn-api';
import { Phase } from '../model/game/phase';
import type { RouteHandler } from '../core/route-handler';
import type { State } from '../model/game/state';
import { arrayRemove } from '../util/util';
import dataConnector from '../impl/data/catan-data-connector';
import { end } from '../impl/game/misc-move-helper';
import { generateUserGameId } from '../model/game/user-game';
import { play } from '../impl/game/move-api-helper';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const admin: RouteHandler<AdminRequest, any> = async ({ logger, user, request }: CatanContext<AdminRequest>) => {
  logger.warn(`[ ADMIN API ] [ ${user} ] request=${JSON.stringify(request)}`);
  const id = request.id;
  if (user === id) {
    throw new BadRequestError('Cannot operate on self');
  }
  switch (request.action) {
    case AdminAction.delete_user:
      await dataConnector.deleteUser(id);
      return dataConnector.getUserGamesByUser(id, true);

    case AdminAction.delete_game:
      await dataConnector.deleteState(id);
      for (const player of (await dataConnector.getBoard(id)).players) {
        await dataConnector.deleteUserGame(generateUserGameId(id, player.id));
      }
      await dataConnector.deleteBoard(id);
      break;

    case AdminAction.end_turn: {
      play(OutOfTurnApi.ADMIN, { user: '', gameId: id, etag: undefined, request: undefined }, end, Phase.gameplay);
      break;
    }

    case AdminAction.get_state:
      return dataConnector.getState(id, undefined);

    case AdminAction.set_state:
      if (!request.state) {
        throw new BadRequestError('Missing field "state"');
      }
      await dataConnector.updateState(request.state as State);
      break;

    case AdminAction.add_admin:
    case AdminAction.remove_admin: {
      const u = (await dataConnector.getUser(id)) as DeepWritable<User>;
      if (request.action === AdminAction.add_admin) {
        if (!u.roles) {
          u.roles = [Role.ADMIN];
        } else if (!u.roles.includes(Role.ADMIN)) {
          u.roles.push(Role.ADMIN);
        }
      } else {
        if (u.roles) {
          arrayRemove(u.roles, Role.ADMIN);
          if (u.roles.length === 0) {
            delete u.roles;
          }
        }
      }
      await dataConnector.updateUser(u);
      break;
    }

    default:
      throw new CatanError('Unexpected value: ' + request.action, NOT_IMPLEMENTED);
  }

  return undefined;
};

// newUserEventHandler is implemented in user-api-helper.ts itself
