import { AchievementType } from '../../model/game/achievement';
import { CatanError } from '../../core/catan-error';
import { DevCard } from '../../model/game/dev-card';
import { MAX_ROADS_PER_PLAYER } from '../../util/constants';
import type { Road } from '../../model/game/road';
import type { State } from '../../model/game/state';
import { count } from '../../util/util';
import { getVertexCount } from './graph-helper';

export function handleLargestArmy(state: State): void {
  const color = state.currentMove.color;
  const achievement = state.achievements[AchievementType.largest_army];
  if (achievement.color === color) {
    return;
  }
  // It is assumed that this is called only when a player plays a knight, so playedDevCards cannot be undefined
  const knights = count(state.playedDevCards[color]!, dev => dev === DevCard.knight);
  if (knights > achievement.count) {
    achievement.color = color;
    achievement.count = knights;
  }
}

export function handleLongestRoad(state: State): void {
  const color = state.currentMove.color;
  const achievement = state.achievements[AchievementType.longest_road];
  if (achievement.count === MAX_ROADS_PER_PLAYER) {
    return;
  }
  if (state.roads[state.roads.length - 1].color !== color) {
    throw new CatanError('Last created road does not belong to current player');
  }
  const roads = state.roads.filter(road => road.color === color);
  if (roads.length <= achievement.count) {
    return;
  }
  const roadVertices = new Array<boolean>(getVertexCount()); // Note: we aren't pre-filling `false` in the array, so iteration will skip the empty slots
  roads.forEach(road => (roadVertices[road.vertex1] = roadVertices[road.vertex2] = true));
  const visited = new Array<boolean>(roads.length).fill(false);
  const max = roadVertices.reduce((max, isNeighbor, i) => (isNeighbor ? dfs(i, roads, visited, 0, max) : max), 0);
  if (max > achievement.count) {
    achievement.color = color;
    achievement.count = max;
  }
}

function dfs(vertex: number, roads: Road[], visited: boolean[], curr: number, max: number): number {
  if (curr > MAX_ROADS_PER_PLAYER) {
    throw new CatanError('DFS depth exceeded max');
  }
  for (let i = 0; i < roads.length; i++) {
    if (!visited[i]) {
      const v = getOtherVertex(roads[i], vertex);
      if (v !== -1) {
        visited[i] = true;
        max = dfs(v, roads, visited, curr + 1, max);
        visited[i] = false;
      }
    }
  }
  return Math.max(curr, max);
}

function getOtherVertex(road: Road, vertex: number): number {
  return vertex === road.vertex1 ? road.vertex2 : vertex === road.vertex2 ? road.vertex1 : -1;
}
