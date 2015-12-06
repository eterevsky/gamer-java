package gamer.dominion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDeck {
  @Test
  public void shuffle() {
    Deck deck = new Deck();
    deck.add(Dominion.COPPER);
    deck.add(Dominion.COPPER);
    deck.add(Dominion.PROVINCE);

    int ncopper = 0;
    int nprovince = 0;

    Stream<DominionCard> s = deck.stream();
    assertEquals(3, s.count());

    Random rng = new Random();

    List<DominionCard> shuffled = new ArrayList<>();
    assertFalse(deck.isEmpty());
    shuffled.add(deck.draw(rng));
    shuffled.add(deck.draw(rng));
    shuffled.add(deck.draw(rng));
    assertTrue(deck.isEmpty());

    assertEquals(2, Collections.frequency(shuffled, Dominion.COPPER));
    assertEquals(1, Collections.frequency(shuffled, Dominion.PROVINCE));
  }
}
