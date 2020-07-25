/*impo

rt getContext, { CatanContext, setContext } from './core/catan-context';

import { setContextCallback } from './core/catan-context';

function A() {
  setContextCallback({ requestId: '123' } as CatanContext, B);
}

function B() {
  console.log('normal callback1', getContext());
}

A();

console.log('######"');

function A1() {
  setContextCallback({ requestId: '123' } as CatanContext, C1);
}

async function C1() {
  await B1();
}

async function B1() {
  console.log('normal callback2', getContext());
  setTimeout(() => console.log('setTimeout', getContext()), 2000);
  await new Promise(x => {
    console.log('promise', getContext());
    x();
  });
  await new Promise(x =>
    setTimeout(() => {
      console.log('promise timeout', getContext());
      x();
    }, 2000)
  );
  console.log('normal callback3', getContext());
}

A1();

console.log('$#$$$#$#');

async function a() {
  await setContext({ requestId: '123' } as CatanContext);
  b();
}

function b() {
  // console.log('async/await', getContext());
}

a();

/**/
