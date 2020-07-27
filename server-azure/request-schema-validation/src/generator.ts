import * as TJS from 'typescript-json-schema';
import * as fs from 'fs-extra';
import * as path from 'path';

import { validator } from '@exodus/schemasafe';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const pipe = (...fn: Function[]) => (v: any = undefined) => {
  fn.reduce((_v, f) => {
    return f(_v);
  }, v);
};

const settings: TJS.PartialArgs = {
  aliasRef: true,
  defaultNumberType: 'integer',
  noExtraProps: true,
  required: true,
  strictNullChecks: true,
  titles: true,
};
const compilerOptions: TJS.CompilerOptions = { strictNullChecks: true };
const basePath = path.resolve(__dirname, '..', '..');

function resolve(...route: string[]): string {
  return path.join(basePath, ...route);
}

function getInterfaceFiles(): string[] {
  // Search interface files
  return fs
    .readdirSync(resolve('src', 'model', 'request'))
    .filter(file => file.endsWith('.ts') && file !== 'index.ts')
    .map(file => resolve('src', 'model', 'request', file));
}

function generateSchemas(files: string[]): { generator: TJS.JsonSchemaGenerator; symbols: string[] } {
  // Generate schema files
  const program = TJS.getProgramFromFiles(files, compilerOptions, basePath);
  const generator = TJS.buildGenerator(program, settings);
  if (generator === null) {
    throw new Error('Null Generator!');
  }

  return { generator, symbols: generator.getMainFileSymbols(program) };
}

function saveSchemas({
  generator,
  symbols,
}: {
  generator: TJS.JsonSchemaGenerator;
  symbols: string[] | undefined;
}): void {
  if (!symbols) {
    symbols = generator.getUserSymbols();
  }
  console.log('Loaded schemas for symbols', symbols);

  // remove old files if exists & create directory
  if (fs.existsSync(resolve('request-schema-validation', 'generated-schemas'))) {
    fs.removeSync(resolve('request-schema-validation', 'generated-schemas'));
  }
  fs.mkdirSync(resolve('request-schema-validation', 'generated-schemas'));

  // store all schema files
  symbols.forEach(symbol => {
    const schema = generator.getSchemaForSymbol(symbol);
    const filePath = `${resolve('request-schema-validation', 'generated-schemas', `${symbol}.json`)}`;
    const fileContents = JSON.stringify(schema, null, 2);
    fs.writeFileSync(filePath, fileContents);
  });
}

function generateModules(): void {
  const opts = { schemas: undefined, /* mode: 'strong', */ isJSON: true };
  const files = fs
    .readdirSync(resolve('request-schema-validation', 'generated-schemas'))
    .filter(file => file.includes('Request'));
  const validationModules: string[] = [];
  for (const file of files) {
    const contents = fs.readFileSync(resolve('request-schema-validation', 'generated-schemas', file)).toString();
    const schema = validator(JSON.parse(contents), opts);
    const module: string = schema.toModule();
    validationModules.push(
      'export const validate' +
        file.slice(0, file.lastIndexOf('.')) +
        ': (data: any, recursive?: any) => boolean = ' +
        module.replace(/function validate\(data, recursive\)/g, 'function validate(data: any, recursive: any)')
    );
  }
  fs.writeFileSync(resolve('src', 'model', 'request', 'generated-validator.ts'), validationModules.join('\n\n'));
  console.log('Generated Request Validator');
}

pipe(getInterfaceFiles, generateSchemas, saveSchemas, generateModules)();
