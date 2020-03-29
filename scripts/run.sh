#!/usr/bin/env bash

SCRIPT_DIR=$(cd $"${BASH_SOURCE%/*}" && pwd)
BASE_DIR=$(cd $SCRIPT_DIR && cd .. && pwd)

set -e

set -x
(
  cd $BASE_DIR &&
  ./gradlew clean jar &&
  java -agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=y -jar build/libs/jmx-exporter.jar scripts/config.yaml
)
