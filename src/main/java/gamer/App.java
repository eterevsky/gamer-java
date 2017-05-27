package gamer;

import gamer.benchmark.BenchmarkSuite;
import gamer.chess.BenchmarkChess;
import gamer.chess.Chess;
import gamer.chess.ChessSimpleEvaluator;
import gamer.chess.ChessState;
import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;
import gamer.g2048.Benchmark2048;
import gamer.g2048.G2048;
import gamer.gomoku.BenchmarkGomoku;
import gamer.gomoku.Gomoku;
import gamer.mcts.BenchmarkMcts;
import gamer.mcts.MonteCarloPlayer;
import gamer.minimax.MinimaxPlayer;
import gamer.players.BenchmarkUct;
import gamer.players.MonteCarloUct;
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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

class App {
  private static <G extends Game<P, M>, P extends State<P, M>,
                  M extends Move>
  void addUctPlayer(G game, Tournament<P, M> tournament, int chThres,
                    int samples, String selector) {
    MonteCarloUct<P, M> player = new MonteCarloUct<>(game);
    player.setChildrenThreshold(chThres);
    player.setSamplesBatch(samples);
    player.setSelector(game.getMoveSelector(selector));

    tournament.addPlayer(player);
  }

  private static <G extends Game<P, M>, P extends State<P, M>, M extends Move> void addPlayers(
      Tournament<P, M> tournament, G game) {
    addUctPlayer(game, tournament, 4, 1, "neighbor");
    addUctPlayer(game, tournament, 4, 4, "neighbor");
    addUctPlayer(game, tournament, 8, 4, "neighbor");
    addUctPlayer(game, tournament, 8, 8, "neighbor");
    addUctPlayer(game, tournament, 16, 8, "neighbor");
    addUctPlayer(game, tournament, 16, 16, "neighbor");
    addUctPlayer(game, tournament, 32, 16, "neighbor");

    addUctPlayer(game, tournament, 4, 4, "random");
    addUctPlayer(game, tournament, 8, 4, "random");
    addUctPlayer(game, tournament, 8, 8, "random");
    addUctPlayer(game, tournament, 16, 8, "random");
    addUctPlayer(game, tournament, 16, 16, "random");
    addUctPlayer(game, tournament, 32, 16, "random");
  }

  static <G extends Game<P, M>, P extends State<P, M>, M extends Move>
  void runTournament(G game) {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);

    Tournament<P, M> tournament = new Tournament<>(game.newGame(), false);

    tournament.setTimeout(1000);
    tournament.setGameThreads(1);
    tournament.setThreadsPerPlayer(cores);
    tournament.setRounds(1);

    addPlayers(tournament, game);

    tournament.play();
  }

  private static <G extends Game<P, M>, P extends State<P, M>, M extends
      Move> void runGame(
      G game, long moveTime) {
    int cores = Runtime.getRuntime().availableProcessors();
    P startPosition = game.newGame();

    MonteCarloPlayer<P, M> player1 = new MonteCarloPlayer<>(game);
    player1.setTimeout(moveTime * 1000);
    player1.setMaxWorkers(cores);
    //    player1.setSamplesBatch(1);

    Match<P, M> match;

    if (game.getPlayersCount() == 1) {
      match = new Match<>(startPosition, player1);
    }
    else {
//      MonteCarloPlayer<P, M> player2 = new MonteCarloPlayer<>(game);
//      player2.setTimeout(moveTime * 1000);
//      player2.setMaxWorkers(cores);
//      player2.setSamplesBatch(4);
//      player2.setSelector(game.getMoveSelector("random"));
      MinimaxPlayer<P, M> player2 = new MinimaxPlayer<>();
      player2.setTimeout(moveTime * 1000);
      if (startPosition instanceof ChessState) {
        @SuppressWarnings("unchecked")
        MonteCarloPlayer<ChessState, ?> chessPlayer1 =
            (MonteCarloPlayer<ChessState, ?>) player1;
        chessPlayer1.setEvaluator(ChessSimpleEvaluator.getInstance());
        @SuppressWarnings("unchecked")
        MinimaxPlayer<ChessState, ?> chessPlayer2 =
            (MinimaxPlayer<ChessState, ?>) player2;
        chessPlayer2.setEvaluator(ChessSimpleEvaluator.getInstance());
      }
      match = new Match<>(startPosition, player1, player2);
    }

    System.out.println(match);
    GameRunner.playSingleGame(match, true);
    System.out.println(match);
  }

  private static void runSingleGame(CommandLine cl) {
    String gameStr = cl.getOptionValue("game", "gomoku");
    long moveTime = Integer.parseInt(cl.getOptionValue("move_time", "15"));

    switch (gameStr) {
      case "gomoku":
        Gomoku gomoku = Gomoku.getInstance(19);
        runGame(gomoku, moveTime);
        break;

      case "chess":
        Chess chess = Chess.getInstance();
        runGame(chess, moveTime);
        break;

      case "2048":
        G2048 g2048 = G2048.getInstance();
        runGame(g2048, moveTime);
        break;

      default:
        throw new RuntimeException("Unknown game in --game.");
    }
  }

  private static void runBenchmarks(CommandLine cl) {
    int timeLimit = Integer.parseInt(
        cl.getOptionValue("benchmark_time_limit", "30"));
    String filter = cl.getOptionValue("filter", null);
    BenchmarkSuite suite = new BenchmarkSuite(timeLimit, filter);
    if (cl.hasOption("benchmark_precision")) {
      suite.setPrecision(
          Double.parseDouble(cl.getOptionValue("benchmark_precision")));
    }
    suite.add(BenchmarkSuite.class);
    suite.add(Benchmark2048.class);
    suite.add(BenchmarkChess.class);
    suite.add(BenchmarkGomoku.class);
    suite.add(BenchmarkUct.class);
    suite.add(BenchmarkMcts.class);

    suite.run();
  }

  private static Options initOptions() {
    Options options = new Options();

    OptionGroup mode = new OptionGroup();
    mode.addOption(new Option("h", "help", false, "Show help message"));
    mode.addOption(new Option("g", "single_game", false, "Run single game"));
    mode.addOption(new Option("t", "tournament", false, "Run tournament"));
    mode.addOption(new Option("b", "benchmark", false, "Run benchmarks"));
    mode.setRequired(true);
    options.addOptionGroup(mode);

    options.addOption(
        "benchmark_time_limit", true,
        "Time limit in seconds for a single benchmark. (Default: 30)");
    options.addOption("game", true, "Game to be played. (Default: gomoku)");
    options.addOption(
        "move_time", true, "Time per move in seconds. (Default: 15)");
    options.addOption(
        "filter", true,
        "Only run benchmarks with this substring in the name. (Default: '')");
    options.addOption(
        "benchmark_precision", true, "Benchmark precision. (Default: 0.05)");

    return options;
  }

  private static String getVersion() {
    try {
      Enumeration<URL> resources =
          App.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
      while (resources.hasMoreElements()) {
        Manifest manifest = new Manifest(resources.nextElement().openStream());
        Attributes attr = manifest.getMainAttributes();

        if ("gamer.App".equals(attr.getValue("Main-Class"))) {
          return attr.getValue("Implementation-Version");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public static void main(String[] args) {
    String version = getVersion();
    if (version != null) {
      System.out.format("gamer %s\n", version);
    }

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
    } else if (cl.hasOption("single_game")) {
      runSingleGame(cl);
    } else if (cl.hasOption("tournament")) {
      runTournament(Gomoku.getInstance());
    } else {
      throw new RuntimeException("Internal error.");
    }
  }
}
