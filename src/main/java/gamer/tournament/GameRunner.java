package gamer.tournament;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Player;

import java.util.Queue;

public final class GameRunner<P extends Position<P, ?>> implements Runnable {
  private final Queue<Match<P>> games;
  private final Queue<Match<P>> results;
  private final boolean verbose;

  GameRunner(Queue<Match<P>> games, Queue<Match<P>> results, boolean verbose) {
    this.games = games;
    this.results = results;
    this.verbose = verbose;
  }

  public void run() {
    while (true) {
      Match<G> match = games.poll();
      if (match == null)
        return;

      match.result = playSingleGame(
          match.game, match.player1, match.player2, verbose);
      results.add(match);
    }
  }

  public static <P extends Position<P, M>, M extends Move>
      int playSingleGame(
          P startPosition, Player<P, M> p1, Player<P, M> p2, boolean verbose) {
    P position = startPosition;

    while (!position.isTerminal()) {
      Player<P, M> player = position.getPlayer() ? p1 : p2;
      position = position.play(player.selectMove(state));
      if (verbose) {
        System.out.println(player.getReport());
        System.out.println(position);
        System.out.println();
      }
    }

    return position.getPayoff(0);
  }
}
