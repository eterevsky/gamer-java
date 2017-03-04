package gamer.tournament;

import gamer.def.Move;

import gamer.def.Player;
import gamer.def.Position;

import java.util.ArrayList;
import java.util.List;

public final class Match<P extends Position<P, M>, M extends Move> {
  public final P startPosition;
  public final List<Player<P, M>> players = new ArrayList<>();
  public Integer result = null;

  public Match(P position, Player<P, M> player) {
    this.startPosition = position;
    this.players.add(player);
  }

  public Match(P position, Player<P, M> player1, Player<P, M> player2) {
    this.startPosition = position;
    this.players.add(player1);
    this.players.add(player2);
  }

  public void setPayoff(int payoff) {
    this.result = payoff;
  }

  @Override
  public String toString() {
    List<String> names = new ArrayList<>();
    for (Player<P, M> player : players) {
      names.add(player.getName());
    }
    if (result == null) {
      return String.join("  v  ", names);
    }

    switch (players.size()) {
      case 1:
        return String.format("%s (%d)", players.get(0).getName(), result);
      case 2:
        return String.format("%s (%d)  v  %s (%d)", players.get(0).getName(),
            result, players.get(1).getName(), -result);
      default:
        throw new RuntimeException("Can't handle this number of players.");
    }
  }
}
