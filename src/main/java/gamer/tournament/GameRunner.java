package gamer.tournament;

import gamer.def.ComputerPlayer;
import gamer.def.Move;
import gamer.def.Player;
import gamer.def.Position;

import java.util.Queue;

public final class GameRunner<P extends Position<P, M>, M extends Move>
    implements Runnable {
  private final Queue<Match<P, M>> games;
  private final Queue<Match<P, M>> results;
  private final boolean verbose;

  GameRunner(Queue<Match<P, M>> games,
             Queue<Match<P, M>> results,
             boolean verbose) {
    this.games = games;
    this.results = results;
    this.verbose = verbose;
  }

  @Override
  public void run() {
    while (true) {
      Match<P, M> match = games.poll();
      if (match == null)
        return;

      match.result = playSingleGame(match, verbose);
      results.add(match);
    }
  }

  public static <P extends Position<P, M>, M extends Move>
      int playSingleGame(Match<P, M> match, boolean verbose) {
    if (verbose) {
      System.out.println(match);
    }
    return playSingleGame(
        match.startPosition, match.player1, match.player2, verbose);
  }

  private static <P extends Position<P, M>, M extends Move> int playSingleGame(
      P position, Player<P, M> p1, Player<P, M> p2, boolean verbose) {
    while (!position.isTerminal()) {
      Player<P, M> player = position.getPlayerBool() ? p1 : p2;
      position = position.play(player.selectMove(position));
      if (verbose) {
        if (player instanceof ComputerPlayer) {
          System.out.println(((ComputerPlayer<P, M>)player).getReport());
        }
        System.out.println(position);
        System.out.println();
      }
    }

    return position.getPayoff(0);
  }
}
