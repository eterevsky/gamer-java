package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Sampler<G extends Game> implements Runnable {
  private final Node<G> root;
  private final long finishTime;
  private final long maxSamples;
  private final int samplesBatch;
  private final Random random;

  Sampler(
      Node<G> root, long finishTime, long maxSamples, int samplesBatch,
      Random random) {
    this.root = root;
    this.finishTime = finishTime;
    this.maxSamples = maxSamples;
    this.samplesBatch = samplesBatch;
    this.random = random;
  }

  public void run() {
    Random rnd = random == null ? ThreadLocalRandom.current() : random;

    while ((maxSamples <= 0 || root.getSamples() < maxSamples) &&
           (finishTime <= 0 || System.currentTimeMillis() < finishTime)) {
      Node<G> node = root;
      while (!node.selectIfNoChildren(samplesBatch)) {
        node = node.selectChild(samplesBatch);
      }

      if (node.getState().status().isTerminal() && !node.knowExactValue()) {
        node.setExactValue(node.getState().status().value());
      }

      double value = 0;
      if (node.knowExactValue()) {
        value = node.getValue();
      } else {
        for (int i = 0; i < samplesBatch; i++) {
          GameState<G> state = node.getState();
          do {
            state = state.play(state.getRandomMove(rnd));
          } while (!state.status().isTerminal());

          value += state.status().value();
        }
        value /= samplesBatch;
      }

      while (node != null) {
        node.addSamples(samplesBatch, value);
        node = node.getParent();
      }
    }
  }
}
