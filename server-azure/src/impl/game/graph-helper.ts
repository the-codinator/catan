import { BadRequestError } from '../../core/catan-error';

export function validateVertex(vertex: number): void {
  if (!Number.isInteger(vertex) || vertex < 0 || vertex >= vertexToAdjacentVertexMatrix.length) {
    throw new BadRequestError('Invalid Vertex Id');
  }
}

export function validateHex(hex: number): void {
  if (!Number.isInteger(hex) || hex < 0 || hex >= hexToConnectedVertexMatrix.length) {
    throw new BadRequestError('Invalid Hex Id');
  }
}

export function normalizePort(port: number): number {
  return portVertexList.includes(port) || portVertexList.includes(--port) ? port : -1;
}

export function normalizeAndValidatePort(port: number): number {
  const normalizedPort = normalizePort(port);
  if (normalizedPort === -1) {
    throw new BadRequestError('Invalid Port Id - ' + port);
  }
  return normalizedPort;
}

export function getComplementaryPortVertex(vertex: number): number {
  const port = normalizePort(vertex);
  return port === -1 ? -1 : 2 * port + 1 - vertex; // port + complement = normalized + (normalized+1)
}

export function getAdjacentVertexListForVertex(vertex: number): number[] | undefined {
  return vertexToAdjacentVertexMatrix[vertex]?.slice();
}

export function isAdjacentVertices(a: number, b: number): boolean {
  return vertexToAdjacentVertexMatrix[a].includes(b);
}

export function getConnectedHexListForVertex(vertex: number): number[] | undefined {
  return vertexToConnectedHexMatrix[vertex]?.slice();
}

export function getVerticesAroundHex(hex: number): number[] | undefined {
  return hexToConnectedVertexMatrix[hex]?.slice();
}

export function getPortCount(): number {
  return portVertexList.length;
}

export function getVertexCount(): number {
  return vertexToAdjacentVertexMatrix.length;
}

export function getDiceRollCount(): number[] {
  return diceRollCount.slice();
}

// --------------------------------------------------------------------------------
// ---------------- Don't look at / touch anything below this line ----------------
// --------------------------------------------------------------------------------

const vertexToAdjacentVertexMatrix = [
  [1, 29],
  [0, 2, 31],
  [1, 3],
  [2, 4],
  [3, 5, 32],
  [4, 6],
  [5, 7, 34],
  [6, 8],
  [7, 9],
  [8, 10, 35],
  [9, 11],
  [10, 12, 37],
  [11, 13],
  [12, 14],
  [13, 15, 38],
  [14, 16],
  [15, 17, 40],
  [16, 18],
  [17, 19],
  [18, 20, 41],
  [19, 21],
  [20, 22, 43],
  [21, 23],
  [22, 24],
  [23, 25, 44],
  [24, 26],
  [25, 27, 46],
  [26, 28],
  [27, 29],
  [0, 28, 47],
  [31, 47, 48],
  [1, 30, 32],
  [4, 31, 33],
  [32, 34, 49],
  [6, 33, 35],
  [9, 34, 36],
  [35, 37, 50],
  [11, 36, 38],
  [14, 37, 39],
  [38, 40, 51],
  [16, 39, 41],
  [19, 40, 42],
  [41, 43, 52],
  [21, 42, 44],
  [24, 43, 45],
  [44, 46, 53],
  [26, 45, 47],
  [29, 30, 46],
  [30, 49, 53],
  [33, 48, 50],
  [36, 49, 51],
  [39, 50, 52],
  [42, 51, 53],
  [45, 48, 52],
];

const hexToConnectedVertexMatrix = [
  [0, 1, 29, 30, 31, 47],
  [1, 2, 3, 4, 31, 32],
  [4, 5, 6, 32, 33, 34],
  [6, 7, 8, 9, 34, 35],
  [9, 10, 11, 35, 36, 37],
  [11, 12, 13, 14, 37, 38],
  [14, 15, 16, 38, 39, 40],
  [16, 17, 18, 19, 40, 41],
  [19, 20, 21, 41, 42, 43],
  [21, 22, 23, 24, 43, 44],
  [24, 25, 26, 44, 45, 46],
  [26, 27, 28, 29, 46, 47],
  [30, 31, 32, 33, 48, 49],
  [33, 34, 35, 36, 49, 50],
  [36, 37, 38, 39, 50, 51],
  [39, 40, 41, 42, 51, 52],
  [42, 43, 44, 45, 52, 53],
  [30, 45, 46, 47, 48, 53],
  [48, 49, 50, 51, 52, 53],
];

const vertexToConnectedHexMatrix = [
  [0],
  [0, 1],
  [1],
  [1],
  [1, 2],
  [2],
  [2, 3],
  [3],
  [3],
  [3, 4],
  [4],
  [4, 5],
  [5],
  [5],
  [5, 6],
  [6],
  [6, 7],
  [7],
  [7],
  [7, 8],
  [8],
  [8, 9],
  [9],
  [9],
  [9, 10],
  [10],
  [10, 11],
  [11],
  [11],
  [0, 11],
  [0, 12, 17],
  [0, 1, 12],
  [1, 2, 12],
  [2, 12, 13],
  [2, 3, 13],
  [3, 4, 13],
  [4, 13, 14],
  [4, 5, 14],
  [5, 6, 14],
  [6, 14, 15],
  [6, 7, 15],
  [7, 8, 15],
  [8, 15, 16],
  [8, 9, 16],
  [9, 10, 16],
  [10, 16, 17],
  [10, 11, 17],
  [0, 11, 17],
  [12, 17, 18],
  [12, 13, 18],
  [13, 14, 18],
  [14, 15, 18],
  [15, 16, 18],
  [16, 17, 18],
];

const portVertexList = [0, 4, 7, 10, 14, 17, 20, 24, 27];

const diceRollCount = [1, 0, 1, 2, 2, 2, 2, 0, 2, 2, 2, 2, 1]; // Desert -> 0

// Improvement TODO: Make the entire validation code part as a compile time thing.
// Can also consider generating the above arrays.
// Validation Code is available in Java implementation.
