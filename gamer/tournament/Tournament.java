package gamer.tournament;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public final class Tournament<G extends Game<G>> {
  private final Game<G> game;
  private final List<Player<G>> players = new ArrayList<>();
  private Integer timeout = null;
  private ExecutorService executor = null;
  private int workers = 1;
  private Map<Player<G>, Map<Player<G>, Double>> results = null;
  private final boolean verbose;

  public Tournament(G game) {
    this(game, false);
  }

  public Tournament(G game, boolean verbose) {
    this.game = game;
    this.verbose = verbose;
  }

  public void addPlayer(Player<G> player) {
    players.add(player);
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public void setExecutor(ExecutorService executor, int workers) {
    this.executor = executor;
    this.workers = workers;
  }

  private static class PlayerResult<G extends Game>
      implements Comparable<PlayerResult<G>> {
    final Player<G> player;
    final Double result;

    @Override
    public int compareTo(PlayerResult<G> o) {
      return o.result.compareTo(result);
    }

    PlayerResult(Player<G> p, double r) {
      player = p;
      result = r;
    }
  }

  public void play() {
    if (players.size() < 2) {
      throw new RuntimeException("Not enough players for the tournament.");
    }

    for (Player<G> p : players) {
      if (timeout != null) {
        p.setTimeout(timeout);
      }
      if (executor != null) {
        p.setExecutor(executor, workers);
      }
    }

    initResults();

    for (Player<G> p1 : players) {
      for (Player<G> p2 : players) {
        if (p1 != p2) {
          double gameResult = playSingleGame(p1, p2);
          results.get(p1).put(p2, results.get(p1).get(p2) + gameResult);
          results.get(p2).put(p1, results.get(p2).get(p1) + (1 - gameResult));
          String p1res =
              gameResult > 0.4 && gameResult < 0.6 ?
                  "½" : String.format("%d", Math.round(gameResult));
          String p2res =
              gameResult > 0.4 && gameResult < 0.6 ?
                  "½" : String.format("%d", 1 - Math.round(gameResult));
          System.out.format("%s (%s)  vs  %s (%s)\n",
                            p1.getName(), p1res, p2.getName(), p2res);
        }
      }
    }

    List<PlayerResult<G>> playersTable = new ArrayList<>();
    for (Player<G> p : players) {
      double s = 0;
      Map<Player<G>, Double> presult = results.get(p);
      for (double r : presult.values()) {
        s += r;
      }
      playersTable.add(new PlayerResult<G>(p, s));
    }

    Collections.sort(playersTable);

    for (PlayerResult<G> pr : playersTable) {
      System.out.format("%s %f\n", pr.player.getName(), pr.result);
    }
  }

  private void initResults() {
    results = new HashMap<>();

    for (Player<G> p : players) {
      HashMap<Player<G>, Double> presults = new HashMap<>();

      for (Player<G> p2 : players) {
        presults.put(p2, 0.0);
      }
      results.put(p, presults);
    }
  }

  private double playSingleGame(Player<G> p1, Player<G> p2) {
    if (verbose)
      System.out.format("Starting %s  vs  %s\n", p1.getName(), p2.getName());

    GameState<G> state = game.newGame();

    while (!state.isTerminal()) {
      Move<G> move;
      if (state.status().getPlayer()) {
        move = p1.selectMove(state);
      } else {
        move = p2.selectMove(state);
      }
      state = state.play(move);
      if (verbose)
        System.out.println(state);
    }

    return state.status().value();
  }
}
