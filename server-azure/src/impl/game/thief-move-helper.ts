import { COLORS, Color } from '../../model/game/color';
import { DROP_CARDS_FOR_THIEF_THRESHOLD } from '../../util/constants';
import { Phase } from '../../model/game/phase';
import type { State } from '../../model/game/state';
import { getResourceCount } from '../../model/game/hand';

export function handleThiefRoll(state: State): void {
  state.phase = Phase.thief;
  const thieved: Color[] = [];
  for (const color of COLORS) {
    if (getResourceCount(state.hands[color]) > DROP_CARDS_FOR_THIEF_THRESHOLD) {
      thieved.push(color);
    }
  }
  if (thieved.length) {
    state.currentMove.thieved = thieved;
  }
}

// TODO: impl

/*
public void thiefDrop(State state, Color color, ThiefDropRequest request) throws CatanException {
  Hand hand = state.getHand(color);
  Util.validateInput(request.getResources(), "resources");
  if (request.getResources().length != hand.getResourceCount()) {
      throw new BadRequestException("Incorrect number of resource cards - need " + hand.getResourceCount() / 2);
  }
  gameUtility.transferResources(state, color, null, request.getResources());
  state.getCurrentMove().getThieved().remove(color);
  if (state.getCurrentMove().getThieved().isEmpty()) {
      state.getCurrentMove().setThieved(null);
  }
}

public void thiefPlay(State state, ThiefPlayRequest request) throws CatanException {
  if (state.getCurrentMove().getThieved() != null && !state.getCurrentMove().getThieved().isEmpty()) {
      throw new BadRequestException(
          "Please wait until players with 8+ cards have dropped half - " + state.getCurrentMove().getThieved());
  }
  if (request.getHex() == state.getThief()) {
      throw new BadRequestException("Thief MUST be moved to a different tile");
  }
  Color color = state.getCurrentMove().getColor();
  if (color == request.getVictim()) {
      throw new BadRequestException("Cannot steal from self");
  }
  graphHelper.validateHex(request.getHex());
  int[] vertices = graphHelper.getVerticesAroundHex(request.getHex());
  boolean hasHouse = false;
  for (int vertex : vertices) {
      House house = state.getHouses().get(vertex);
      if (house != null && house.getColor() != color) {
          if (request.getVictim() == null && state.getHand(house.getColor()).getResourceCount() > 0) {
              throw new BadRequestException("Must steal from a player if possible (missing field - victim)");
          }
          if (house.getColor() == request.getVictim()) {
              hasHouse = true;
              break;
          }
      }
  }
  if (request.getVictim() == null) {
      return;
  }
  if (!hasHouse) {
      throw new BadRequestException("Cannot steal from player without house on thief tile");
  }
  Resource resource = gameUtility.chooseRandomlyStolenCard(state, request.getVictim());
  // Note: resource can be null if chosen player has no cards
  gameUtility.transferResources(state, request.getVictim(), color, resource);
}
*/
