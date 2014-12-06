package gamer;

import gamer.chess.Chess;
import gamer.chess.endings.ChessEndingSolver;
import gamer.def.Game;
import gamer.def.Move;
import gamer.def.Position;
import gamer.gomoku.Gomoku;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;
import gamer.tournament.GameRunner;
import gamer.tournament.Match;
import gamer.tournament.Tournament;

class App {
  private static <P extends Position<P, M>, M extends Move> void addUctPlayer(
      Tournament<P, M> tournament, int chThres, int samples) {
    MonteCarloUct<P, M> player = new MonteCarloUct<>();
    player.setChildrenThreshold(chThres);
    player.setChildrenThreshold(samples);
    tournament.addPlayer(player);
  }

  private static <P extends Position<P, M>, M extends Move> void addPlayers(
      Tournament<P, M> tournament) {
    addUctPlayer(tournament, 1, 1);
    addUctPlayer(tournament, 2, 1);
    addUctPlayer(tournament, 4, 1);
    addUctPlayer(tournament, 2, 2);
    addUctPlayer(tournament, 4, 2);
    addUctPlayer(tournament, 4, 4);
    addUctPlayer(tournament, 8, 4);
    addUctPlayer(tournament, 8, 8);
    addUctPlayer(tournament, 16, 8);
    addUctPlayer(tournament, 16, 16);
    addUctPlayer(tournament, 32, 16);

    tournament.addPlayer(new MonteCarloUcb<P, M>());
    tournament.addPlayer(new NaiveMonteCarlo<P, M>());
    tournament.addPlayer(new RandomPlayer<P, M>());
  }

  static Gomoku gomoku = Gomoku.getInstance();
  static Chess chess = Chess.getInstance();

  static <P extends Position<P, M>, M extends Move> void runTournament() {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);

    Tournament<P, M> tournament = new Tournament<P, M>(gomoku, true);
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

  static <P extends Position<P, ?>> void runGameFromPosition(P startPosition) {
    int cores = Runtime.getRuntime().availableProcessors();

    MonteCarloUct<P, ?> player1 = new MonteCarloUct<>();
    player1.setTimeout(300000L);
    player1.setMaxWorkers(cores);
    player1.setSamplesBatch(1);
    player1.setChildrenThreshold(2);
    MonteCarloUct<P, ?> player2 = new MonteCarloUct<>();
    player2.setTimeout(300000L);
    player2.setMaxWorkers(cores);
    player2.setSamplesBatch(4);
    Match<P> match = new Match<>(game, player1, player2);

    System.out.println(match);
    GameRunner.playSingleGame(game, player1, player2, true);
    System.out.println(match);
  }

  static <G extends Game> void runGame(G game) {
    runGameFromPosition(game.newGame());
  }

  public static void main(String[] args) throws Exception {
    runGame(chess);
  }
}
