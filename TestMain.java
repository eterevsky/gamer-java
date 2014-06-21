package gamer;

import gamer.gomoku.TestGomokuMove;
import gamer.gomoku.TestGomokuState;
import gamer.treegame.TestTreeGame;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestGomokuMove.class,
  TestGomokuState.class,
  TestTreeGame.class
})

public class TestMain {
}
