#!/bin/sh

set -e
rm -f *.jar `find gamer -name \*.class`

CLASSPATH=.:./lib/junit-4.11.jar:./lib/hamcrest-core-1.3.jar

javac -cp $CLASSPATH -Xlint:unchecked gamer/*.java gamer/*/*.java
java -ea -cp $CLASSPATH org.junit.runner.JUnitCore gamer.TestMain
jar cvfe gamer.jar gamer.App `find gamer -name \*.class | grep -v /Test`
