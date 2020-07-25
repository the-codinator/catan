import { shuffle } from '../../util/util';

export enum DevCard {
  knight = 'knight',
  monopoly = 'monopoly',
  road_building = 'road_building',
  year_of_plenty = 'year_of_plenty',
  chapel = 'chapel',
  great_hall = 'great_hall',
  library = 'library',
  market = 'market',
  university = 'university',
}

const counts: Partial<Record<DevCard, number>> = {
  knight: 14,
  monopoly: 2,
  road_building: 2,
  year_of_plenty: 2,
};

export function createRandomDevCards(): DevCard[] {
  const list: DevCard[] = [];
  for (const dev in DevCard) {
    for (let count = counts[dev as DevCard]; count; count--) {
      list.push(dev as DevCard);
    }
  }
  return shuffle(list);
}

const points: DevCard[] = [DevCard.chapel, DevCard.great_hall, DevCard.library, DevCard.market, DevCard.university];

export function getVictoryPoints(devCard: DevCard): number {
  return points.includes(devCard) ? 1 : 0;
}
