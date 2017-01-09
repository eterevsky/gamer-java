package gamer.players;

import gamer.def.Move;
import gamer.def.MoveSelector;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Sampler<P extends Position<P, M>, M extends Move> implements Runnable {
  static final double PAYOFF_SCALE_FACTOR = 0.999;

  private final Node<P, M> root;
  private final long finishTime;
  private final long maxSamples;
  private final int samplesBatch;
  private final MoveSelector<P, M> selector;
  private Solver<P, M> solver = null;

  Sampler(Node<P, M> root,
          long finishTime,
          long maxSamples,
          int samplesBatch,
          MoveSelector<P, M> selector) {
    this.root = root;
    this.finishTime = finishTime;
    this.maxSamples = maxSamples;
    this.samplesBatch = samplesBatch;
    this.selector = selector;
  }

  void setSolver(Solver<P, M> solver) {
    this.solver = solver;
  }

  @Override
  public void run() {
    while (!root.knowExact() &&
        (maxSamples <= 0 || root.getSamples() < maxSamples) &&
        (finishTime <= 0 || System.currentTimeMillis() < finishTime)) {
      Node<P, M> node = root;
      Node<P, M> next = node.selectChildOrAddPending(samplesBatch);
      while (next != Node.NO_CHILDREN && next != Node.KNOW_EXACT) {
        node = next;
        next = node.selectChildOrAddPending(samplesBatch);
      }

      if (next == Node.KNOW_EXACT) {
        continue;
      }

      double value = 0;
      for (int i = 0; i < samplesBatch; i++) {
        P position = node.getState().clone();
        Solver.Result<M> sResult = null;
        int moves = 0;
        do {
          position.play(selector.select(position));
          // sResult = (solver != null) ? solver.solve(position) : null;
          moves += 1;
        } while (!position.isTerminal() && sResult == null);

        if (position.isTerminal()) {
          value += Math.pow(PAYOFF_SCALE_FACTOR, moves) * position.getPayoff(0);
        } else {
          value += Math.pow(PAYOFF_SCALE_FACTOR, moves) * sResult.payoff;
        }
      }
      node.addSamples(samplesBatch, value / samplesBatch);
    }
  }
}
