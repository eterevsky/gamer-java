package gamer;

import gamer.gomoku.TestGomokuMove;
import gamer.gomoku.TestGomokuState;
import gamer.players.TestGenericPlayer;
import gamer.players.TestRandomPlayer;
import gamer.players.TestMonteCarloUcb;
import gamer.players.TestMonteCarloUct;
import gamer.players.TestNaiveMonteCarlo;
import gamer.players.TestNode;
import gamer.treegame.TestTreeGame;
import gamer.util.TestUpdatablePriorityQueue;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestGomokuMove.class,
  TestGomokuState.class,

  TestGenericPlayer.class,
  TestMonteCarloUcb.class,
  TestMonteCarloUct.class,
  TestNaiveMonteCarlo.class,
  TestNode.class,
  TestRandomPlayer.class,

  TestTreeGame.class,

  TestUpdatablePriorityQueue.class
})

public class TestMain {
  @BeforeClass
  public static void setUpClass() {
    Logger.getLogger("gamer").setLevel(Level.WARNING);
  }
}
