#!/bin/sh

set -e
rm -f *.jar

CLASSPATH=.:./lib/junit-4.11.jar:./lib/hamcrest-core-1.3.jar

javac -cp $CLASSPATH -Xlint:unchecked gamer/*.java gamer/*/*.java
java -cp $CLASSPATH org.junit.runner.JUnitCore gamer.TestMain
#java -cp $CLASSPATH org.junit.runner.JUnitCore gamer.players.TestMonteCarloUct

CLASSES=`ls -R gamer | grep .class\$ | grep -v ^Test`

jar cvfe gamer.jar gamer.App $CLASSES
