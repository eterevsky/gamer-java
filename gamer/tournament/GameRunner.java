package gamer.tournament;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.Match;
import gamer.def.Player;

import java.util.Queue;

public final class GameRunner<G extends Game<G>> implements Runnable {
  private final Queue<Match<G>> games;
  private final Queue<Match<G>> results;
  private final boolean verbose;

  GameRunner(Queue<Match<G>> games, Queue<Match<G>> results, boolean verbose) {
    this.games = games;
    this.results = results;
    this.verbose = verbose;
  }

  public void run() {
    while (true) {
      Match<G> match = games.poll();
      if (match == null)
        return;

      match.result = playSingleGame(match.game, match.player1, match.player2);
      results.add(match);
    }
  }

  public static <G extends Game<G>> GameStatus playSingleGame(
      Game<G> game, Player<G> p1, Player<G> p2) {
    GameState<G> state = game.newGame();

    while (!state.isTerminal()) {
      Player<G> player = state.status().getPlayer() ? p1 : p2;
      state = state.play(player.selectMove(state));
      System.out.println(player.getReport());
      System.out.println(state);
      System.out.println();
    }

    return state.status();
  }
}
