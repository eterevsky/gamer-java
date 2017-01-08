package gamer.dominion;

import org.junit.Test;

import gamer.def.MoveSelector;

import java.util.Random;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class TestDominion {
  @Test
  public void create() {
    Dominion dominion1 = new Dominion.Builder(3).randomizeCards().build();
    DominionState state = dominion1.newGame();
    assertFalse(state.isTerminal());
  }

  @Test
  public void play() {
    Random rng = new Random(1234567890L);

    Dominion dominion = new Dominion.Builder(3).build();
    DominionState state = dominion.newGame();
    MoveSelector<DominionState, DominionMove> selector =
        dominion.getRandomMoveSelector();
    assertNull(state.getMoves());
    assertEquals(-1, state.getPlayer());
    // state.play(selector.select(state));
    //
    // assertEquals(0, state.getPlayer());
    // assertThat(state.getMoves(), hasItem(DominionMove.BUY_PHASE));
    // assertEquals(1, state.getMoves().size());
    //
    // state.play(DominionMove.BUY_PHASE);
    // assertThat(state.getMoves(), hasItem(DominionMove.CLEANUP));
    // assertThat(state.getMoves(), hasItem(Cards.COPPER.getBuy()));
    // assertThat(state.getMoves(), not(hasItem(Cards.COPPER.getBuy())));
    //
    // state.play(Cards.COPPER.getBuy());
    //
    // assertThat(state.getMoves(), hasItem(DominionMove.CLEANUP));
    // assertEquals(1, state.getMoves().size());
    //
    // state.play(DominionMove.CLEANUP);
    // assertEquals(-1, state.getPlayer());
    // assertFalse(state.isTerminal());
    // assertNull(state.getMoves());
    // state.play(selector.select(state));
    //
    // assertEquals(1, state.getPlayer());
  }
}
