package gamer;

import gamer.benchmark.Benchmark;
import gamer.benchmark.BenchmarkSuite;
import gamer.chess.BenchmarkPerft;
import gamer.chess.Chess;
import gamer.def.Move;
import gamer.def.Position;
import gamer.gomoku.BenchmarkGomoku;
import gamer.gomoku.Gomoku;
import gamer.players.BenchmarkUct;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;
import gamer.tournament.GameRunner;
import gamer.tournament.Match;
import gamer.tournament.Tournament;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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

  static <P extends Position<P, M>, M extends Move> void runTournament(
      P startPosition) {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);

    Tournament<P, M> tournament = new Tournament<>(startPosition, true);

    tournament.setTimeout(2000);
    tournament.setGameThreads(1);
    tournament.setThreadsPerPlayer(cores);
    tournament.setRounds(1);

    addPlayers(tournament);

    tournament.play();
  }

  static <P extends Position<P, M>, M extends Move> void runGameFromPosition(
      P startPosition) {
    int cores = Runtime.getRuntime().availableProcessors();

    MonteCarloUct<P, M> player1 = new MonteCarloUct<>();
    player1.setTimeout(15000L);
    player1.setMaxWorkers(cores);
    player1.setSamplesBatch(16);
//    player1.setChildrenThreshold(1);
    MonteCarloUct<P, M> player2 = new MonteCarloUct<>();
    player2.setTimeout(15000L);
    player2.setMaxWorkers(cores);
    player2.setSamplesBatch(8);
    Match<P, M> match = new Match<>(startPosition, player1, player2);

    System.out.println(match);
    GameRunner.playSingleGame(match, true);
    System.out.println(match);
  }

  private static void runBenchmarks(CommandLine cl) {
    BenchmarkSuite suite = new BenchmarkSuite();
    suite.add(BenchmarkSuite.class);
    suite.add(BenchmarkGomoku.class);
    suite.add(BenchmarkPerft.class);
    suite.add(BenchmarkUct.class);

    suite.run();
  }

  private static Options initOptions() {
    Options options = new Options();

    OptionGroup mode = new OptionGroup();
    mode.addOption(new Option("h", "help", false, "Show help message"));
    mode.addOption(new Option("g", "game", false, "Run single game"));
    mode.addOption(new Option("t", "tournament", false, "Run tournament"));
    mode.addOption(new Option("b", "benchmark", false, "Run benchmarks"));
    mode.setRequired(true);
    options.addOptionGroup(mode);

    return options;
  }

  public static void main(String[] args) throws Exception {
    Options options = initOptions();
    CommandLineParser parser = new BasicParser();
    CommandLine cl;
    try {
      cl = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      return;
    }

    if (cl.hasOption("help")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("gamer", options, true /* autoUsage */);
    } else if (cl.hasOption("benchmark")) {
      runBenchmarks(cl);
    } else if (cl.hasOption("game")) {
      runGameFromPosition(Gomoku.getInstance().newGame());
    } else if (cl.hasOption("tournament")) {
      runTournament(Gomoku.getInstance().newGame());
    } else {
      throw new RuntimeException("Internal error.");
    }
  }
}
