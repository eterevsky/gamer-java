package gamer;

import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;
import gamer.gomoku.Gomoku;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;
import gamer.tournament.Tournament;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class App {
  public static void main(String[] args) throws Exception {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);
    Gomoku gomoku = Gomoku.getInstance();
    Tournament<Gomoku> tournament = new Tournament<Gomoku>(gomoku);

    tournament.setTimeout(1000);
    tournament.setExecutor(Executors.newFixedThreadPool(cores), cores);

    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(0).setSamplesBatch(1));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(0)
        .setSamplesBatch(1)
        .setFindExact(true));
                                            ;
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(1).setSamplesBatch(1));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(1)
        .setSamplesBatch(1)
        .setFindExact(true));

    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(2).setSamplesBatch(1));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(2)
        .setSamplesBatch(1)
        .setFindExact(true));

    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(4).setSamplesBatch(1));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(4)
        .setSamplesBatch(1)
        .setFindExact(true));

    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(0).setSamplesBatch(2));
    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(2).setSamplesBatch(2));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(4).setSamplesBatch(2));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(4)
        .setSamplesBatch(2)
        .setFindExact(true));

    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(0).setSamplesBatch(4));
    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(4).setSamplesBatch(4));
    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(8).setSamplesBatch(4));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(0).setSamplesBatch(8));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(0)
        .setSamplesBatch(8)
        .setFindExact(true));

    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(8).setSamplesBatch(8));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(8)
        .setSamplesBatch(8)
        .setFindExact(true));

    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(16).setSamplesBatch(8));
    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(0).setSamplesBatch(16));
    tournament.addPlayer(
        new MonteCarloUct<Gomoku>().setChildrenThreshold(16).setSamplesBatch(16));
    tournament.addPlayer(new MonteCarloUct<Gomoku>()
        .setChildrenThreshold(16)
        .setSamplesBatch(16)
        .setFindExact(true));

    // tournament.addPlayer(
    //     new MonteCarloUct<Gomoku>().setChildrenThreshold(32).setSamplesBatch(16));
    // tournament.addPlayer(new MonteCarloUcb<Gomoku>().setSamplesBatch(1));
    // tournament.addPlayer(new MonteCarloUcb<Gomoku>().setSamplesBatch(2));
    // tournament.addPlayer(new MonteCarloUcb<Gomoku>().setSamplesBatch(4));
    // tournament.addPlayer(new MonteCarloUcb<Gomoku>().setSamplesBatch(8));
    // tournament.addPlayer(new NaiveMonteCarlo<Gomoku>().setSamplesBatch(1));
    // tournament.addPlayer(new NaiveMonteCarlo<Gomoku>().setSamplesBatch(2));
    // tournament.addPlayer(new RandomPlayer<Gomoku>());

    tournament.play();
  }
}
