import * as BuildMoveHelper from '../../impl/game/build-move-helper';
import * as DevCardMoveHelper from '../../impl/game/dev-card-move-helper';
import * as MiscMoveHelper from '../../impl/game/misc-move-helper';
import * as SetupMoveHelper from '../../impl/game/setup-move-helper';
import * as ThiefMoveHelper from '../../impl/game/thief-move-helper';
import * as TradeApiHelper from '../../impl/game/trade-api-helper';
import type {
  DevCardRequest,
  HouseRequest,
  RoadRequest,
  SetupMoveRequest,
  ThiefDropRequest,
  ThiefPlayRequest,
  TradeBankRequest,
  TradePlayerRequest,
  TradeResponseRequest,
  _BodyLessMoveRequest,
} from '../../model/request/game-request';
import type { MoveRequest } from '../../model/request';
import { OutOfTurnApi } from '../../model/game/out-of-turn-api';
import { Phase } from '../../model/game/phase';
import type { RouteHandler } from '../../model/core';
import type { StateResponse } from '../../model/response/state-response';
import { play } from '../../impl/game/move-api-helper';

type Move<T extends MoveRequest> = RouteHandler<T, StateResponse>;

export const setup: Move<SetupMoveRequest> = context =>
  play(undefined, context, SetupMoveHelper.play, Phase.setup1, Phase.setup2);

export const roll: Move<_BodyLessMoveRequest> = context =>
  play(undefined, context, MiscMoveHelper.roll, Phase.gameplay);

export const road: Move<RoadRequest> = context => play(undefined, context, BuildMoveHelper.road, Phase.gameplay);
export const house: Move<HouseRequest> = context => play(undefined, context, BuildMoveHelper.house, Phase.gameplay);

export const devBuy: Move<_BodyLessMoveRequest> = context =>
  play(undefined, context, DevCardMoveHelper.buy, Phase.gameplay);

export const devPlay: Move<DevCardRequest> = context =>
  play(undefined, context, DevCardMoveHelper.play, Phase.gameplay);

export const thiefDrop: Move<ThiefDropRequest> = context =>
  play(OutOfTurnApi.THIEF, context, ThiefMoveHelper.thiefDrop, Phase.thief);

export const thiefPlay: Move<ThiefPlayRequest> = context =>
  play(undefined, context, ThiefMoveHelper.thiefPlay, Phase.thief);

export const tradeBank: Move<TradeBankRequest> = context =>
  play(undefined, context, TradeApiHelper.bank, Phase.gameplay);

export const tradePlayer: Move<TradePlayerRequest> = context =>
  play(undefined, context, TradeApiHelper.offer, Phase.gameplay);

export const tradeResponse: Move<TradeResponseRequest> = context =>
  play(undefined, context, TradeApiHelper.respond, Phase.gameplay);

export const end: Move<_BodyLessMoveRequest> = context => play(undefined, context, MiscMoveHelper.end, Phase.gameplay);
