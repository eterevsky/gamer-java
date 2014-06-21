#!/bin/sh

rm -f *.jar *.class */*.class

CLASSPATH=$CLASSPATH:/usr/share/java/junit4.jar

javac -cp .:/usr/share/java/junit4.jar -Xlint:unchecked \
    def/*.java gomoku/*.java players/*.java treegame/*.java *.java && \
java -cp ..:.:/usr/share/java/junit4.jar org.junit.runner.JUnitCore \
    gamer.TestMain && \
cd .. && \
jar cvfe gamer/gamer.jar gamer.App \
    gamer/App.class gamer/def/*.class gamer/gomoku/*.class gamer/players/*.class
