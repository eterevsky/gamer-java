#!/bin/sh

rm -f *.jar *.class */*.class

javac -Xlint:unchecked gamer/*.java gomoku/*.java players/*.java App.java && \
jar cvfe gamer.jar App App.class gamer/*.class gomoku/*.class players/*.class
