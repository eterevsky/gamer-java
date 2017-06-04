package gamer.minimax;

import gamer.def.Evaluator;
import gamer.def.FeatureExtractor;
import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class LinearRegressionEvaluator<S extends State<S, M>, M extends Move>
    implements Evaluator<S> {
  private static final double LEARNING_RATE = 0.001;
  private final FeatureExtractor<S> featureExtractor;
  private final Game<S, M> game;
  private double[] coefficients;

  public LinearRegressionEvaluator(Game<S, M> game, FeatureExtractor<S> featureExtractor) {
    this.game = game;
    this.featureExtractor = featureExtractor;
  }

  @Override
  public double evaluate(S state) {
    double sum = 0;
    double[] features = featureExtractor.extractFeatures(state);
    for (int i = 0; i < features.length; i++) {
      sum += features[i] * coefficients[i];
    }

    if (sum > game.getMaxPayoff()) return game.getMaxPayoff();
    if (sum < game.getMinPayoff()) return game.getMinPayoff();

    return sum;
  }

  public void train() {
    Random rng = ThreadLocalRandom.current();
    coefficients = new double[featureExtractor.getFeatureCount()];
    // coefficients = rng.doubles(featureExtractor.getFeatureCount()).map(x -> (x - 0.5)).toArray();
    MinimaxPlayer<S, M> player = new MinimaxPlayer<>();
    player.setMaxDepth(1);
    player.setEvaluator(this);

    for (int igame = 0; igame < 10000; igame++) {
      S state = game.newGame();
      int moves = 0;

      while (!state.isTerminal()) {
        moves++;
        MinimaxPlayer.SearchResult<M> searchResult = player.search(
            state, 3, game.getMinPayoff(), game.getMaxPayoff());
        double currentEvaluation = evaluate(state);
        double error = currentEvaluation - searchResult.score;
        // if (searchResult.score != 0) {
        //   System.out.println(searchResult.score);
        // }
//        double[] gradient = new double[coefficients.length];
        double[] features = featureExtractor.extractFeatures(state);

        for (int i = 0; i < coefficients.length; i++) {
          double gradi = features[i] * error;
          coefficients[i] -= LEARNING_RATE * gradi;
        }

        if (rng.nextDouble() < 0.1) {
          state.play(state.getRandomMove());
        } else {
          state.play(searchResult.move);
        }
      }

      System.out.format("Game #%d, %d moves. Result: %d%n", igame, moves, state.getPayoff(0));
      System.out.format("[%.4f", coefficients[0]);
      for (int i = 1; i < coefficients.length; i++) {
        System.out.format(" %.4f", coefficients[i]);
      }
      System.out.println("]");
    }
  }
}
