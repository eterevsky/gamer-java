package gamer;

import gamer.chess.Chess;
import gamer.chess.ChessEndingHelper;
import gamer.def.Game;
import gamer.def.Match;
import gamer.gomoku.Gomoku;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;
import gamer.tournament.GameRunner;
import gamer.tournament.Tournament;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class App {
  private static void addPlayers(Tournament<Gomoku> tournament) {
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(1).setSamplesBatch(1));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(2).setSamplesBatch(1));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(4).setSamplesBatch(1));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(2).setSamplesBatch(2));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(4).setSamplesBatch(2));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(4).setSamplesBatch(4));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(8).setSamplesBatch(4));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(8).setSamplesBatch(8));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(16).setSamplesBatch(8));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(16).setSamplesBatch(16));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(32).setSamplesBatch(16));

    tournament.addPlayer(new MonteCarloUcb<Gomoku>().setSamplesBatch(1));
    tournament.addPlayer(new NaiveMonteCarlo<Gomoku>().setSamplesBatch(1));
    tournament.addPlayer(new RandomPlayer<Gomoku>());
  }

  static Gomoku gomoku = Gomoku.getInstance();
  static Chess chess = Chess.getInstance();

  static void runTournament() {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);

    Tournament<Gomoku> tournament = new Tournament<>(gomoku, true);
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    tournament.setTimeout(10000);
    tournament.setExecutor(executor);
    tournament.setGameThreads(1);
    tournament.setThreadsPerPlayer(cores);
    tournament.setRounds(1);

    addPlayers(tournament);

    tournament.play();
    executor.shutdown();
  }

  static <G extends Game<G>> void runGame(G game) {
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    MonteCarloUct<G> player1 = new MonteCarloUct<>();
    player1.setTimeout(300000L);
    player1.setExecutor(executor, cores);
    player1.setSamplesBatch(1);
    player1.setChildrenThreshold(2);
    MonteCarloUct<G> player2 = new MonteCarloUct<>();
    player2.setTimeout(300000L);
    player2.setExecutor(executor, cores);
    player2.setSamplesBatch(4);
    Match<G> match = new Match<>(game, player1, player2);

    System.out.println(match);
    GameRunner.playSingleGame(game, player1, player2, true);
    System.out.println(match);
    executor.shutdown();
  }

  public static void main(String[] args) throws Exception {
    runGame(chess);
  }
}
