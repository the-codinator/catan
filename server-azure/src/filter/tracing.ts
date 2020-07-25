import type { CatanLogger } from '../core/catan-context';

export function accessLog(logger: CatanLogger, user: string | undefined, method: string, path: string): void {
  logger.info(`[ ACCESS ] method=${method} path=/${path} user=${user}`);
}

export function requestLog(
  logger: CatanLogger,
  user: string | undefined,
  method: string,
  path: string,
  clientIp: string,
  userAgent: string,
  status: number,
  start: [number, number]
): void {
  const durationHr = process.hrtime(start);
  const duration = durationHr[0] * 1000 + Math.ceil(durationHr[1] / 1000000);
  logger.info(
    `[ REQUEST ] requestId=${logger.requestId} request="${method} /${path}" ip=${clientIp} user=${user} status=${status} duration=${duration} agent="${userAgent}"`
  );
}
