package gamer.tournament;

import gamer.def.Move;

import gamer.def.Player;
import gamer.def.Position;

public final class Match<P extends Position<P, M>, M extends Move> {
  public final P startPosition;
  public final Player<P, M> player1;
  public final Player<P, M> player2;
  public Integer result = null;

  public Match(P position, Player<P, M> player1, Player<P, M> player2) {
    this.startPosition = position;
    this.player1 = player1;
    this.player2 = player2;
  }

  public void setPayoff(int payoff) {
    this.result = payoff;
  }

  @Override
  public String toString() {
    if (result == null) {
      return String.format("%s: %s  v  %s",
                           player1.getName(),
                           player2.getName());
    }

    char result1, result2;
    switch (result) {
      case 1: result1 = '1'; result2 = '0'; break;
      case -1: result1 = '0'; result2 = '1'; break;
      case 0: result1 = '½'; result2 = '½'; break;
      default:
        throw new RuntimeException("shouldn't happen");
    }

    return String.format("%s (%c)  v  %s (%c)",
                         player1.getName(), result1,
                         player2.getName(), result2);
  }
}
