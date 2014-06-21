#!/bin/sh

rm -f *.jar *.class */*.class

CLASSPATH=$CLASSPATH:/usr/share/java/junit4.jar

javac -cp .:/usr/share/java/junit4.jar -Xlint:unchecked \
    gamer/*.java gamer/*/*.java && \
java -cp .:/usr/share/java/junit4.jar org.junit.runner.JUnitCore \
    gamer.TestMain && \
jar cvfe gamer.jar gamer.App \
    gamer/App.class gamer/def/*.class gamer/gomoku/*.class gamer/players/*.class
