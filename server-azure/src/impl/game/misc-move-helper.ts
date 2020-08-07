import { ACHIEVEMENTS, getVictoryPoints as getAchievementTypeVictoryPoints } from '../../model/game/achievement';
import { THIEF_ROLL, VICTORY_POINTS_FOR_WIN } from '../../util/constants';
import { findTileHexesForRoll, rollDice, transferResources } from './game-utility';
import { getVictoryPoints as getHouseTypeVictoryPoints, getResourceMultiplier } from '../../model/game/house-type';
import { BadRequestError } from '../../core/catan-error';
import type { Board } from '../../model/game/board';
import type { DeepReadonly } from 'ts-essentials';
import { Phase } from '../../model/game/phase';
import type { PlayOptions } from './move-api-helper';
import type { State } from '../../model/game/state';
import type { _BodyLessMoveRequest } from '../../model/request/game-request';
import { createCurrentMove } from '../../model/game/current-move';
import { getVictoryPoints as getDevCardVictoryPoints } from '../../model/game/dev-card';
import { getRoll } from '../../model/game/roll';
import { getVerticesAroundHex } from './graph-helper';
import { handleThiefRoll } from './thief-move-helper';

export function roll({ board, state }: PlayOptions<_BodyLessMoveRequest>): void {
  if (state.currentMove.roll) {
    throw new BadRequestError('Cannot re-roll in a turn');
  }
  const roll = { die1: rollDice(), die2: rollDice() };
  state.currentMove.roll = roll;
  const rollValue: number = getRoll(roll);
  if (rollValue === THIEF_ROLL) {
    handleThiefRoll(state);
  } else {
    for (const hex of findTileHexesForRoll(board, rollValue)) {
      if (state.thief === hex) {
        continue;
      }
      for (const vertex of getVerticesAroundHex(hex)) {
        const house = state.houses[vertex];
        if (house) {
          transferResources(
            state,
            undefined,
            house.color,
            board.tiles[hex].resource!, // Cannot be desert tile
            getResourceMultiplier(house.type)
          );
        }
      }
    }
  }
}

export function end({ board, state }: PlayOptions<_BodyLessMoveRequest>): void {
  endTurn(board, state);
}

export function endTurn(board: DeepReadonly<Board>, state: State): void {
  const color = state.currentMove.color;
  let index = board.players.findIndex(player => player.color === color);
  const minIndex = 0;
  const maxIndex = board.players.length - 1;
  if (state.phase === Phase.gameplay && isVictory(state)) {
    state.phase = Phase.end;
  } else {
    switch (state.phase) {
      case Phase.setup1:
        if (index < maxIndex) {
          index++;
        } else {
          state.phase = Phase.setup2;
        }
        break;
      case Phase.setup2:
        if (index > minIndex) {
          index--;
        } else {
          state.phase = Phase.gameplay;
        }
        break;
      default:
        index++;
        if (index > maxIndex) {
          index = minIndex;
        }
    }
    state.currentMove = createCurrentMove(board.players[index].color);
  }
}

function isVictory(state: State): boolean {
  let points = 0;
  const color = state.currentMove.color;
  for (const house of Object.values(state.houses)) {
    if (house?.color === color) {
      points += getHouseTypeVictoryPoints(house.type);
    }
  }
  // Victory Points from Achievements
  for (const type of ACHIEVEMENTS) {
    if (state.achievements[type].color === color) {
      points += getAchievementTypeVictoryPoints(type);
    }
  }
  // Victory Points from Dev Cards
  for (const devCard of state.hands[color].devCards) {
    points += getDevCardVictoryPoints(devCard);
  }
  return points >= VICTORY_POINTS_FOR_WIN;
}
