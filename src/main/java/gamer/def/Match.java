package gamer.def;

public final class Match<G extends Game<G>> {
  public final Game<G> game;
  public final Player<G> player1;
  public final Player<G> player2;
  public GameStatus result;

  public Match(Game<G> game, Player<G> player1, Player<G> player2) {
    this.game = game;
    this.player1 = player1;
    this.player2 = player2;
  }

  public void setResult(GameStatus result) {
    if (!result.isTerminal()) {
      throw new RuntimeException("Result must be a terminal status.");
    }
    this.result = result;
  }

  public String toString() {
    if (result == null) {
      return String.format("%s  v  %s", player1.getName(), player2.getName());
    }

    char result1, result2;
    switch (result) {
      case WIN: result1 = '1'; result2 = '0'; break;
      case LOSS: result1 = '0'; result2 = '1'; break;
      case DRAW: result1 = '½'; result2 = '½'; break;
      default:
        throw new RuntimeException("can't happen");
    }

    return String.format("%s (%c)  v  %s (%c)",
                         player1.getName(), result1,
                         player2.getName(), result2);
  }
}
