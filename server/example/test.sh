#!/bin/bash -e

DIR=example
SCRIPT=test.js
RESET=$1 # false
SERVER=$2 # http://localhost:8080
SIGNUP=$3 # true
ADMIN_PWD=$4 # empty

if [ -d "${DIR}" ]; then
  cd ${DIR}
fi

if [ ! -d "node_modules" ]; then
  npm i node-fetch deep-equal
  rm -f package-lock.json
fi

node ${SCRIPT} "reset=${RESET}" "server=${SERVER}" "signup=${SIGNUP}" "admin_pwd=${ADMIN_PWD}"
