import type { AzureFunction, Context, HttpRequest } from '@azure/functions';

const httpTrigger: AzureFunction = async function (context: Context, req: HttpRequest): Promise<void> {
  context.log('HTTP trigger function processed a request.');
  const name = req.query.name || (req.body && req.body.name);

  if (name) {
    context.res = {
      // status: 200, /* Defaults to 200 */
      body: { t: 'Hello ' + (req.query.name || req.body.name), r: context.bindingData }
    };
  } else {
    context.res = {
      status: 400,
      body: { X: 'Please pass a name on the query string or in the request body' }
    };
  }
};

export default httpTrigger;
