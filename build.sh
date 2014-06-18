#!/bin/sh

rm -f *.jar *.class */*.class

CLASSPATH=$CLASSPATH:/usr/share/java/junit4.jar

javac -cp .:/usr/share/java/junit4.jar -Xlint:unchecked \
    gamer/*.java gomoku/*.java players/*.java *.java && \
java -cp .:/usr/share/java/junit4.jar org.junit.runner.JUnitCore TestMain && \
jar cvfe gamer.jar App App.class gamer/*.class gomoku/*.class players/*.class
