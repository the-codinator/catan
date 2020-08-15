import type { AzureFunction, Context, HttpRequest } from '@azure/functions';
import { router } from '../router';

const httpTrigger: AzureFunction = async function (context: Context, req: HttpRequest): Promise<void> {
  context.res = await router(context, req);
};

export default httpTrigger;
