package gamer.tournament;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Match;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public final class Tournament<G extends Game<G>> {
  private final Game<G> game;
  private final List<Player<G>> players = new ArrayList<>();
  private Integer timeout = null;
  private ExecutorService executor = null;
  private int threadsPerPlayer = 1;
  private int gameThreads = 1;
  private Map<Player<G>, Map<Player<G>, Double>> results = null;
  private Queue<Match<G>> gamesQueue;
  private BlockingQueue<Match<G>> resultsQueue;
  private final boolean verbose;
  private int rounds = 1;

  public Tournament(G game) {
    this(game, false);
  }

  public Tournament(G game, boolean verbose) {
    this.game = game;
    this.verbose = verbose;
    this.gamesQueue = new ConcurrentLinkedQueue<>();
    this.resultsQueue = new LinkedBlockingQueue<>();
  }

  public void addPlayer(Player<G> player) {
    players.add(player);
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public void setExecutor(ExecutorService executor) {
    this.executor = executor;
  }

  public void setThreadsPerPlayer(int threads) {
    threadsPerPlayer = threads;
  }

  public void setGameThreads(int threads) {
    gameThreads = threads;
  }

  public void setRounds(int rounds) {
    this.rounds = rounds;
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
      initPlayer(p);
    }

    initResults();

    List<Match<G>> games = new ArrayList<>();

    for (Player<G> p1 : players) {
      for (Player<G> p2 : players) {
        if (p1 != p2)
          for (int i = 0; i < rounds; i++)
            games.add(new Match<G>(game, p1, p2));
      }
    }

    Collections.shuffle(games);

    for (Match<G> match : games) {
      gamesQueue.add(match);
    }

    for (int i = 0; i < gameThreads; i++) {
      executor.submit(new GameRunner<G>(gamesQueue, resultsQueue, verbose));
    }

    collectResults();
  }

  private void collectResults() {
    int ngames = rounds * players.size() * (players.size() - 1);
    int nresults = 0;

    while (nresults < ngames) {
      nresults += 1;
      Match<G> match;
      try {
        match = resultsQueue.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      Player<G> p1 = match.player1;
      Player<G> p2 = match.player2;
      System.out.format("%s  (%d/%d)", match, nresults, ngames);
      double gameResult = match.result.value();
      results.get(p1).put(p2, results.get(p1).get(p2) + gameResult);
      results.get(p2).put(p1, results.get(p2).get(p1) + (1 - gameResult));
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

  private void initPlayer(Player<G> player) {
    if (timeout != null) {
      player.setTimeout(timeout);
    }
    if (executor != null && threadsPerPlayer > 1) {
      player.setExecutor(executor, threadsPerPlayer);
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
}
