#!/bin/bash -e

DIR=example
SCRIPT=test.js
SIGNUP=$1 # true
SERVER=$2 # http://localhost:8080

if [ -d "${DIR}" ]; then
  cd ${DIR}
fi

if [ ! -d "node_modules" ]; then
  npm i node-fetch deep-equal
  rm -f package-lock.json
fi

node ${SCRIPT} "signup=${SIGNUP}" "server=${SERVER}"
