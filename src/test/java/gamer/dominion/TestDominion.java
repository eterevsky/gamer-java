package gamer.dominion;

import org.junit.Test;

public class TestDominion {
  @Test
  public void create() {
    Dominion dominion1 = new Dominion.Builder(3).randomizeCards().build();
  }
}
