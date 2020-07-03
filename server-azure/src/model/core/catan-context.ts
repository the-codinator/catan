import { CatanError } from '../../core/catan-error';
import { Logger } from '@azure/functions';
import { createNamespace } from 'cls-hooked';

export interface CatanLogger {
  info(message: string): void;
  warning(message: string, error?: Error): void;
  error(message: string, error?: Error): void;
}

export type CatanContext = Readonly<{
  requestId: string;
  logger: Logger;
}>;

const ns = createNamespace('catan');
const key = 'context';

export async function setContext(context: CatanContext): Promise<void> {
  if (ns && ns.active) {
    return new Promise(callback =>
      ns.run(() => {
        ns.set(key, context);
        callback();
      })
    );
  } else {
    throw new CatanError('Cannot access context outside call chain');
  }
}

export default function getContext(): CatanContext {
  if (ns && ns.active) {
    const context = ns.get(key);
    if (!context) {
      throw new CatanError('Context not defined');
    }
    return context;
  } else {
    throw new CatanError('Cannot access context outside call chain');
  }
}
