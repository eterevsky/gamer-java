package gamer.def;

public final class Match {
  public final Game game;
  public final Player player1;
  public final Player player2;
  public Integer payoff = null;

  public Match(Game game, Player player1, Player player2) {
    this.game = game;
    this.player1 = player1;
    this.player2 = player2;
  }

  public void setPayoff(int payoff) {
    this.payoff = payoff;
  }

  public String toString() {
    if (payoff == null) {
      return String.format("%s: %s  v  %s", game.toString(),
                           player1.getName(),
                           player2.getName());
    }

    char result1, result2;
    switch (payoff) {
      case 1: result1 = '1'; result2 = '0'; break;
      case -1: result1 = '0'; result2 = '1'; break;
      case 0: result1 = '½'; result2 = '½'; break;
      default:
        throw new RuntimeException("shouldn't happen");
    }

    return String.format("%s: %s (%c)  v  %s (%c)",
                         game.toString(),
                         player1.getName(), result1,
                         player2.getName(), result2);
  }
}
