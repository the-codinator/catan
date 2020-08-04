import { ensureCanPlaceHouse, ensureCanPlaceRoad } from './build-move-helper';
import { getConnectedHexListForVertex, validateVertex } from './graph-helper';
import { HouseType } from '../../model/game/house-type';
import { Phase } from '../../model/game/phase';
import type { PlayOptions } from './move-api-helper';
import type { SetupMoveRequest } from '../../model/request/game-request';
import { endTurn } from './misc-move-helper';
import { transferResourcesList } from './game-utility';

export function play({ board, state, request }: PlayOptions<SetupMoveRequest>): void {
  validateVertex(request.houseVertex);
  validateVertex(request.roadVertex);

  ensureCanPlaceHouse(state, request.houseVertex, HouseType.settlement);
  ensureCanPlaceRoad(state, request.houseVertex, request.roadVertex);
  // Create settlement & road
  const color = state.currentMove.color;
  state.houses[request.houseVertex] = { color, type: HouseType.settlement };
  state.roads.push({ color, vertex1: request.houseVertex, vertex2: request.roadVertex });
  // Gain resources
  if (state.phase === Phase.setup2) {
    for (const hex of getConnectedHexListForVertex(request.houseVertex)) {
      transferResourcesList(state, undefined, color, board.tiles[hex].resource);
    }
  }
  // Auto end turn
  endTurn(board, state);
}
