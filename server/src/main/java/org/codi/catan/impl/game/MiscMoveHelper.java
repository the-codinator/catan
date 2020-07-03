/*
 * @author the-codinator
 * created on 2020/5/30
 */

package org.codi.catan.impl.game;

import static org.codi.catan.util.Constants.THIEF_ROLL;
import static org.codi.catan.util.Constants.VICTORY_POINTS_FOR_WIN;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.codi.catan.core.BadRequestException;
import org.codi.catan.core.CatanException;
import org.codi.catan.impl.user.UserGamesHelper;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.Color;
import org.codi.catan.model.game.CurrentMove;
import org.codi.catan.model.game.DevCard;
import org.codi.catan.model.game.House;
import org.codi.catan.model.game.Phase;
import org.codi.catan.model.game.Roll;
import org.codi.catan.model.game.State;
import org.codi.catan.util.Util;

@Singleton
public class MiscMoveHelper {

    private final GameUtility gameUtility;
    private final GraphHelper graphHelper;
    private final ThiefMoveHelper thiefMoveHelper;
    private final UserGamesHelper userGamesHelper;

    @Inject
    public MiscMoveHelper(GameUtility gameUtility, GraphHelper graphHelper, ThiefMoveHelper thiefMoveHelper,
        UserGamesHelper userGamesHelper) {
        this.gameUtility = gameUtility;
        this.graphHelper = graphHelper;
        this.thiefMoveHelper = thiefMoveHelper;
        this.userGamesHelper = userGamesHelper;
    }

    /**
     * Roll the dice at the start of a person's turn
     */
    public void roll(Board board, State state) throws CatanException {
        if (state.getCurrentMove().getRoll() != null) {
            throw new BadRequestException("Cannot re-roll in a turn");
        }
        Roll roll = new Roll(gameUtility.rollDice(), gameUtility.rollDice());
        state.getCurrentMove().setRoll(roll);
        if (roll.getRoll() == THIEF_ROLL) {
            thiefMoveHelper.handleThiefRoll(state);
        } else {
            for (int hex : gameUtility.findTileHexesForRoll(board, roll.getRoll())) {
                if (state.getThief() == hex) {
                    continue;
                }
                for (int vertex : graphHelper.getVerticesAroundHex(hex)) {
                    House house = state.getHouses().get(vertex);
                    if (house != null) {
                        gameUtility.transferResources(state, null, house.getColor(),
                            board.getTiles()[hex].getResource(), house.getType().getResourceMultiplier());
                    }
                }
            }
        }
    }

    /**
     * Complete current turn
     */
    public void endTurn(Board board, State state) throws CatanException {
        Color color = state.getCurrentMove().getColor();
        int index = Util.find(board.getPlayers(), p -> p.getColor() == color);
        int minIndex = 0;
        int maxIndex = board.getPlayers().length - 1;
        if (state.getPhase() == Phase.gameplay && isVictory(state)) {
            state.setPhase(Phase.end);
        } else {
            switch (state.getPhase()) {
                case setup1:
                    if (index < maxIndex) {
                        index++;
                    } else {
                        state.setPhase(Phase.setup2);
                    }
                    break;
                case setup2:
                    if (index > minIndex) {
                        index--;
                    } else {
                        state.setPhase(Phase.gameplay);
                    }
                    break;
                default:
                    index++;
                    if (index > maxIndex) {
                        index = minIndex;
                    }
            }
            state.setCurrentMove(new CurrentMove(board.getPlayers()[index].getColor()));
        }
    }

    /**
     * Check if current turn is winning
     */
    private boolean isVictory(State state) {
        int points = 0;
        Color color = state.getCurrentMove().getColor();
        // Victory Points from Houses
        for (House house : state.getHouses().values()) {
            if (house.getColor() == color) {
                points += house.getType().getVictoryPoints();
            }
        }
        // Victory Points from Achievements
        for (var achievement : state.getAchievements().entrySet()) {
            if (achievement.getValue().getColor() == color) {
                points += achievement.getKey().getVictoryPoints();
            }
        }
        // Victory Points from Dev Cards
        for (DevCard devCard : state.getHand(color).getDevCards()) {
            points += devCard.getVictoryPoint();
        }
        return points >= VICTORY_POINTS_FOR_WIN;
    }
}
