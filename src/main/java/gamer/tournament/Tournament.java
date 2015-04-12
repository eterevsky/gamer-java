package gamer.tournament;

import gamer.def.ComputerPlayer;
import gamer.def.Move;
import gamer.def.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public final class Tournament<P extends Position<P, M>, M extends Move> {
  private final P startPosition;
  private final List<ComputerPlayer<P, M>> players = new ArrayList<>();
  private Integer timeout = null;
  private int threadsPerPlayer = 1;
  private int gameThreads = 1;
  private Map<ComputerPlayer<P, M>, Map<ComputerPlayer<P, M>, Double>> results =
      null;
  private Queue<Match<P, M>> gamesQueue;
  private BlockingQueue<Match<P, M>> resultsQueue;
  private final boolean verbose;
  private int rounds = 1;

  public Tournament(P startPosition) {
    this(startPosition, false);
  }

  public Tournament(P startPosition, boolean verbose) {
    this.startPosition = startPosition;
    this.verbose = verbose;
    this.gamesQueue = new ConcurrentLinkedQueue<>();
    this.resultsQueue = new LinkedBlockingQueue<>();
  }

  public void addPlayer(ComputerPlayer<P, M> player) {
    players.add(player);
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
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

  private static class PlayerResult implements Comparable<PlayerResult> {
    final ComputerPlayer<?, ?> player;
    final Double result;

    @Override
    public int compareTo(PlayerResult o) {
      return o.result.compareTo(result);
    }

    PlayerResult(ComputerPlayer<?, ?> p, double r) {
      player = p;
      result = r;
    }
  }

  public void play() {
    if (players.size() < 2) {
      throw new RuntimeException("Not enough players for the tournament.");
    }

    for (ComputerPlayer<P, M> p : players) {
      initPlayer(p);
    }

    initResults();

    List<Match<P, M>> games = new ArrayList<>();

    for (ComputerPlayer<P, M> p1 : players) {
      for (ComputerPlayer<P, M> p2 : players) {
        if (p1 != p2) {
          for (int i = 0; i < rounds; i++)
            games.add(new Match<>(startPosition, p1, p2));
        }
      }
    }

    Collections.shuffle(games);

    for (Match<P, M> match : games) {
      gamesQueue.add(match);
    }

    ExecutorService executor = Executors.newFixedThreadPool(gameThreads);

    for (int i = 0; i < gameThreads; i++) {
      executor.submit(new GameRunner<>(gamesQueue, resultsQueue, verbose));
    }

    collectResults();
    executor.shutdown();
  }

  private void collectResults() {
    int ngames = rounds * players.size() * (players.size() - 1);
    int nresults = 0;

    while (nresults < ngames) {
      nresults += 1;
      Match<P, M> match;
      try {
        match = resultsQueue.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      ComputerPlayer<P, M> p1 = (ComputerPlayer<P, M>)match.player1;
      ComputerPlayer<P, M> p2 = (ComputerPlayer<P, M>)match.player2;
      System.out.format("%s  (%d/%d)\n", match, nresults, ngames);
      results.get(p1).put(p2, results.get(p1).get(p2) + match.result);
      results.get(p2).put(p1, results.get(p2).get(p1) - match.result);
    }

    List<PlayerResult> playersTable = new ArrayList<>();
    for (ComputerPlayer<P, M> p : players) {
      double s = 0;
      Map<ComputerPlayer<P, M>, Double> presult = results.get(p);
      for (double r : presult.values()) {
        s += r;
      }
      playersTable.add(new PlayerResult(p, s));
    }

    Collections.sort(playersTable);

    for (PlayerResult pr : playersTable) {
      System.out.format("%s %f\n", pr.player.getName(), pr.result);
    }
  }

  private void initPlayer(ComputerPlayer<P, M> player) {
    if (timeout != null) {
      player.setTimeout(timeout);
    }
    player.setMaxWorkers(threadsPerPlayer);
  }

  private void initResults() {
    results = new HashMap<>();

    for (ComputerPlayer<P, M> p : players) {
      HashMap<ComputerPlayer<P, M>, Double> presults = new HashMap<>();

      for (ComputerPlayer<P, M> p2 : players) {
        presults.put(p2, 0.0);
      }
      results.put(p, presults);
    }
  }
}
