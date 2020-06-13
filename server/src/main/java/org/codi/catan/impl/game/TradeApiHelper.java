/*
 * @author the-codinator
 * created on 2020/6/13
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.MAX_ACTIVE_TRADES;

import java.util.EnumMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.Resource;
import org.codi.catan.model.game.State;
import org.codi.catan.model.game.Trade;
import org.codi.catan.model.request.TradeBankRequest;
import org.codi.catan.model.request.TradePlayerRequest;
import org.codi.catan.model.request.TradeResponseRequest;
import org.codi.catan.util.Util;

@Singleton
public class TradeApiHelper {

    private final GameUtility gameUtility;

    @Inject
    public TradeApiHelper(GameUtility gameUtility) {
        this.gameUtility = gameUtility;
    }

    /**
     * Trade with Bank / Port
     */
    public void bank(Board board, State state, TradeBankRequest request) throws CatanException {
        if (request.getOffer() == null || request.getAsk() == null) {
            // In theory, the rules do not disallow trading n resources for 1 of the same resource, hence no check
            throw new BadRequestException("Missing resource in request");
        }
        Color color = state.getCurrentMove().getColor();
        switch (request.getCount()) {
            case 2: // 2:1 port
                if (!gameUtility.hasHouseOnPort(board, state, request.getOffer())) {
                    throw new BadRequestException(
                        "Cannot perform trade without house on 2:1 port for " + request.getOffer());
                }
                break;
            case 3: // 3:1 port
                if (!gameUtility.hasHouseOnPort(board, state, null)) {
                    throw new BadRequestException("Cannot perform trade without house on 3:1 port");
                }
                break;
            case 4: // 4:1 bank
                break;
            default:
                throw new BadRequestException("Invalid resource count for trade");
        }
        gameUtility.transferResources(state, color, null, request.getOffer(), request.getCount());
        if (gameUtility.transferResources(state, null, color, request.getAsk()) != 1) {
            throw new BadRequestException("Bank does not have requested resource");
        }
    }

    /**
     * {@param requester} offers a trade with {@param partner} (defaults to current turn player if null)
     */
    public void offer(State state, Color requester, TradePlayerRequest request) throws CatanException {
        Color current = state.getCurrentMove().getColor();
        Color partner = request.getPartner();
        // Validate participants & input
        if (partner == null) {
            partner = current;
        }
        if (requester == partner) {
            throw new BadRequestException("Cannot trade with self");
        }
        if (requester != current && partner != current) {
            throw new BadRequestException("One of the trade participants MUST be the current turn player");
        }
        if (request.getOffer() == null || request.getAsk() == null) {
            throw new BadRequestException("Missing trade resources");
        }
        var trades = state.getCurrentMove().getActiveTrades();
        if (trades.size() >= MAX_ACTIVE_TRADES) {
            throw new BadRequestException("Too many active trades this turn");
        }
        // Validate resources
        var offer = Util.arrayToEnumMap(Resource.class, request.getOffer());
        var ask = Util.arrayToEnumMap(Resource.class, request.getAsk());
        if (offer.isEmpty() && ask.isEmpty()) {
            throw new BadRequestException("Dumb empty trade...");
        }
        if (!hasSufficientResources(state.getHand(requester).getResources(), offer)) {
            throw new BadRequestException("Insufficient Resources for trade");
        }
        // Create Trade
        String id = Util.generateRandomUuid();
        if (trades.containsKey(id)) {
            throw new CatanException("UUID conflict error - this is a random stupid error, please retry");
        }
        Trade trade;
        if (requester == current) {
            trade = new Trade(partner, false, ask, offer);
        } else {
            trade = new Trade(requester, true, offer, ask);
        }
        trades.put(id, trade);
    }

    /**
     * Respond (accept/reject) to a trade offer to you.
     * On acceptance, clear all other active trades due to potential impact from changed resources
     */
    public void respond(State state, Color requester, TradeResponseRequest request) throws CatanException {
        Color current = state.getCurrentMove().getColor();
        Trade trade = state.getCurrentMove().getActiveTrades().get(request.getId());
        if (trade == null) {
            throw new BadRequestException("Invalid trade");
        }
        if (requester != (trade.isOfferedByPartner() ? current : trade.getPartner())) {
            throw new BadRequestException("Cannot respond to a trade you offered");
        }
        if (request.isAccepted()) {
            try {
                gameUtility.transferResources(state, current, trade.getPartner(), trade.getTurnResources());
                gameUtility.transferResources(state, trade.getPartner(), current, trade.getPartnerResources());
            } catch (CatanException e) {
                throw new CatanException("You do not have sufficient resources to accept this trade",
                    Status.BAD_REQUEST, e);
            }
            state.getCurrentMove().getActiveTrades().clear();
            state.getCurrentMove().getAcceptedTrades().add(trade);
        } else {
            state.getCurrentMove().getActiveTrades().remove(request.getId());
        }
    }

    private boolean hasSufficientResources(EnumMap<Resource, Integer> hand, EnumMap<Resource, Integer> required) {
        for (Resource resource : Resource.values()) {
            if (hand.getOrDefault(resource, 0) < required.getOrDefault(resource, 0)) {
                return false;
            }
        }
        return true;
    }
}
