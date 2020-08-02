export interface Roll {
  die1: number;
  die2: number;
}

export function getRoll(roll: Roll): number {
  return roll.die1 + roll.die2;
}
