#!/usr/bin/env bash

set -e
SCRIPT_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
BASE_DIR=$(cd $SCRIPT_DIR && cd .. && pwd)
TOMCAT_VERSION=7
JMX_PORT=9000

set -x

docker run \
  -it \
  --rm \
  -p 8080:8080 \
  -p $JMX_PORT:$JMX_PORT \
  -e CATALINA_OPTS="\
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.local.only=false \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.port=$JMX_PORT \
    -Dcom.sun.management.jmxremote.rmi.port=$JMX_PORT \
    -Djava.rmi.server.hostname=127.0.0.1 \
    -Dcom.sun.management.jmxremote.ssl=false" \
  --name tomcat$TOMCAT_VERSION \
  --mount \
    "type=bind,source=$SCRIPT_DIR/tomcat-users.xml,target=/usr/local/tomcat/conf/tomcat-users.xml" \
  tomcat:$TOMCAT_VERSION