#!/bin/bash -e

DIR=example
SCRIPT=init.js
SERVER=$1

if [ -d "${DIR}" ]; then
  cd ${DIR}
fi

if [ ! -d "node_modules" ]; then
  npm i node-fetch
  rm -f package-lock.json
fi

node ${SCRIPT} ${SERVER}
