export enum Phase {
  setup1 = 'setup1',
  setup2 = 'setup2',
  gameplay = 'gameplay',
  thief = 'thief',
  end = 'end',
}

export function isSetupPhase(phase: Phase): boolean {
  return phase === Phase.setup1 || phase === Phase.setup2;
}
