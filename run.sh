#!/bin/sh

./gradlew run -Pargs="$*"

# java -agentlib:hprof=cpu=samples,depth=10 -jar build/libs/gamer-all-0.1-93-gb7ade7b-dirty.jar -b