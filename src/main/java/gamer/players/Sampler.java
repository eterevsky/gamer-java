package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Sampler<P extends Position<P, M>, M extends Move> implements Runnable {
  private final Node<P, M> root;
  private final long finishTime;
  private final long maxSamples;
  private final int samplesBatch;
  private final Random random;
  private Solver<P, M> solver = null;

  Sampler(Node<P, M> root,
          long finishTime,
          long maxSamples,
          int samplesBatch,
          Random random) {
    this.root = root;
    this.finishTime = finishTime;
    this.maxSamples = maxSamples;
    this.samplesBatch = samplesBatch;
    this.random = random;
  }

  void setSolver(Solver<P, M> solver) {
    this.solver = solver;
  }

  @Override
  public void run() {
    Random rnd = random == null ? ThreadLocalRandom.current() : random;

    while (!root.knowExact() &&
           (maxSamples <= 0 || root.getSamples() < maxSamples) &&
           (finishTime <= 0 || System.currentTimeMillis() < finishTime)) {
      Node<P, M> node = root;
      Node.SelectChildResult<P, M> scResult = null;
      while (true) {
        scResult = node.selectChildOrAddPending(samplesBatch);
        if (scResult.noChildren || scResult.knowExact)
          break;
        node = scResult.child;
      }

      if (scResult.knowExact)
        continue;

      double value = 0;
      for (int i = 0; i < samplesBatch; i++) {
        P position = node.getPosition();
        Solver.Result<M> sResult = null;
        do {
          position = position.play(position.getRandomMove(rnd));
          sResult = (solver != null) ? solver.solve(position) : null;
        } while (!position.isTerminal() && sResult == null);

        if (position.isTerminal()) {
          value += position.getPayoff(0);
        } else {
          value += sResult.payoff;
        }
      }
      node.addSamples(samplesBatch, value / samplesBatch);
    }
  }
}
