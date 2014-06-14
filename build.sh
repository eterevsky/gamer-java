#!/bin/sh

javac gamer/*.java && \
javac gomoku/*.java && \
javac players/*.java && \
javac App.java && \
jar cvfe gamer.jar App App.class gamer/*.class gomoku/*.class players/*.class
