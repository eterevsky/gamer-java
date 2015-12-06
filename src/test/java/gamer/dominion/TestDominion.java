package gamer.dominion;

import gamer.def.GameException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDominion {
  @Test
  public void create() {
    Dominion dominion1 = new Dominion.Builder(3).randomizeCards().build();
  }
}