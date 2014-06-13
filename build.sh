#!/bin/sh

javac gamer/*.java && \
javac gomoku/*.java && \
javac App.java && \
jar cvfe gamer.jar App App.class gamer/*.class gomoku/*.class
