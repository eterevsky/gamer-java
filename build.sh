#!/bin/sh

javac -Xlint:unchecked gamer/*.java && \
javac -Xlint:unchecked gomoku/*.java && \
javac -Xlint:unchecked players/*.java && \
javac -Xlint:unchecked App.java && \
jar cvfe gamer.jar App App.class gamer/*.class gomoku/*.class players/*.class
