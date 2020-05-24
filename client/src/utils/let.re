module Async = {
  let let_ = (prom, cb) => Js.Promise.then_(cb, prom)
};