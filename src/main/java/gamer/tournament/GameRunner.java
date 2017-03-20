package gamer.tournament;

import gamer.def.ComputerPlayer;
import gamer.def.Move;
import gamer.def.Player;
import gamer.def.Position;

import java.util.List;
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
    return playSingleGame(match.startPosition, match.players, verbose);
  }

  private static <P extends Position<P, M>, M extends Move> int playSingleGame(
      P position, List<Player<P, M>> ps, boolean verbose) {
    while (!position.isTerminal()) {
      int iplayer = position.getPlayer();
      Player<P, M> player = null;
      M move;
      if (iplayer == -1) {
        move = position.getRandomMove();
      } else {
        player = ps.get(iplayer);
        move = player.selectMove(position);
      }
      position.play(move);
      if (verbose) {
        if (player instanceof ComputerPlayer) {
          System.out.println(((ComputerPlayer<P, M>)player).getReport());
        }
        System.out.println(position);
        System.out.println();
      }
    }

    System.out.println(position.getPayoff(0));

    return position.getPayoff(0);
  }
}
