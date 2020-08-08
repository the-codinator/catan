import { BadRequestError, CatanError } from '../../core/catan-error';
import { RESOURCES, Resource } from '../../model/game/resource';
import type { TradeBankRequest, TradePlayerRequest, TradeResponseRequest } from '../../model/request/game-request';
import { arrayToEnumMap, generateRandomUuid } from '../../util/util';
import { hasHouseOnPort, transferResources, transferResourcesMap } from './game-utility';
import { BAD_REQUEST } from 'http-status-codes';
import { MAX_ACTIVE_TRADES } from '../../util/constants';
import type { PlayOptions } from './move-api-helper';

export function bank({ board, state, request }: PlayOptions<TradeBankRequest>): void {
  const color = state.currentMove.color;
  switch (request.count) {
    case 2:
      if (!hasHouseOnPort(board, state, request.offer)) {
        throw new BadRequestError('Cannot perform trade without house on 2:1 port for ' + request.offer);
      }
      break;
    case 3:
      if (!hasHouseOnPort(board, state, undefined)) {
        throw new BadRequestError('Cannot perform trade without house on 3:1 port');
      }
      break;
    case 4:
      break;
    default:
      throw new BadRequestError('Invalid resource count for trade');
  }
  transferResources(state, color, undefined, request.offer, request.count);
  if (transferResources(state, undefined, color, request.ask, 1) !== 1) {
    throw new BadRequestError('Bank does not havce requested resource');
  }
}

export function offer({ state, color: requester, request }: PlayOptions<TradePlayerRequest>): void {
  const current = state.currentMove.color;
  let partner = request.partner;
  // Validate participants & input
  if (!partner) {
    partner = current;
  }
  if (requester === partner) {
    throw new BadRequestError('Cannot trade with self');
  }
  if (requester !== current && partner !== current) {
    throw new BadRequestError('One of the trade participants MUST be the current turn player');
  }
  const trades = state.currentMove.activeTrades;
  const tradeIds = Object.keys(trades);
  if (tradeIds.length >= MAX_ACTIVE_TRADES) {
    throw new BadRequestError('Too many active trades this turn');
  }
  // Validate resources
  const offer = arrayToEnumMap(...request.offer);
  const ask = arrayToEnumMap(...request.ask);
  if (request.offer.length === 0 && request.ask.length === 0) {
    throw new BadRequestError('Dumb empty trade...');
  }
  if (!hasSufficientResources(state.hands[requester].resources, offer)) {
    throw new BadRequestError('Insufficient Resources for trade');
  }
  // Create Trade
  const id = generateRandomUuid();
  if (trades[id]) {
    throw new CatanError('UUID conflict error - this is a random stupid error, please retry');
  }
  const trade =
    requester === current
      ? { partner, offeredByPartner: false, partnerResources: ask, turnResources: offer }
      : { partner: requester, offeredByPartner: true, partnerResources: offer, turnResources: ask };
  trades[id] = trade;
}

export function respond({ state, color: requester, request }: PlayOptions<TradeResponseRequest>): void {
  const current = state.currentMove.color;
  const trade = state.currentMove.activeTrades[request.id];
  if (!trade) {
    throw new BadRequestError('Invalid Trade');
  }
  if (requester !== (trade.offeredByPartner ? current : trade.partner)) {
    throw new BadRequestError('Cannot respond to a trade offered to someone else');
  }
  if (request.accepted) {
    try {
      transferResourcesMap(state, current, trade.partner, trade.turnResources);
      transferResourcesMap(state, trade.partner, current, trade.partnerResources);
    } catch (e) {
      throw new CatanError('You do not have sufficient resources to accept this trade', BAD_REQUEST, e);
    }
    state.currentMove.activeTrades = {};
    state.currentMove.acceptedTrades.push(trade);
  } else {
    delete state.currentMove.activeTrades[request.id];
  }
}

function hasSufficientResources(
  hand: Partial<Record<Resource, number>>,
  required: Partial<Record<Resource, number>>
): boolean {
  for (const resource of RESOURCES) {
    if ((hand[resource] ?? 0) < (required[resource] ?? 0)) {
      return false;
    }
  }
  return true;
}
