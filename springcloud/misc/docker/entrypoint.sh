#!/bin/bash

echo "start of entrypoint.sh"
env

if [ $# > 0 ] && [ "$(command -v $1)" != "" ]; then
    exec $@
fi

cmd="./consul agent $@"
set -x
exec ${cmd}

