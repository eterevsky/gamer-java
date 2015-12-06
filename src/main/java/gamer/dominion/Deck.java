package gamer.dominion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

final class Deck {
  private List<DominionCard> shuffled = new ArrayList<>();
  private List<DominionCard> top = new ArrayList<>();

  Deck() {
  }

  void add(DominionCard card) {
    assert top.isEmpty();
    shuffled.add(card);
  }

  boolean isEmpty() {
    return shuffled.isEmpty() && top.isEmpty();
  }

  DominionCard draw(Random rng) {
    if (!top.isEmpty()) {
      return top.remove(top.size() - 1);
    }

    return shuffled.remove(rng.nextInt(shuffled.size()));
  }

  Stream<DominionCard> stream() {
    return Stream.concat(shuffled.stream(), top.stream());
  }
}
