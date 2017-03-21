package gamer.players;

import gamer.def.Move;
import gamer.def.MoveSelector;
import gamer.def.State;
import gamer.def.Solver;

class Sampler<S extends State<S, M>, M extends Move> implements Runnable {
  static final double PAYOFF_SCALE_FACTOR = 1 - 1E-9;

  private final Node<S, M> root;
  private final S startingState;
  private final long finishTime;
  private final long maxSamples;
  private final int samplesBatch;
  private final MoveSelector<S, M> selector;
  private Solver<S, M> solver = null;

  Sampler(Node<S, M> root,
          S startingState,
          long finishTime,
          long maxSamples,
          int samplesBatch,
          MoveSelector<S, M> selector) {
    this.root = root;
    this.startingState = startingState;
    this.finishTime = finishTime;
    this.maxSamples = maxSamples;
    this.samplesBatch = samplesBatch;
    this.selector = selector;
  }

  void setSolver(Solver<S, M> solver) {
    this.solver = solver;
  }

  @Override
  public void run() {
    while (!root.knowExact() &&
        (maxSamples <= 0 || root.getSamples() < maxSamples) &&
        (finishTime <= 0 || System.currentTimeMillis() < finishTime)) {
      S state = startingState.clone();
      Node<S, M> node = root;
      Node<S, M> next = node.selectChildOrAddPending(state, samplesBatch);
      while (next != Node.NO_CHILDREN && next != Node.KNOW_EXACT) {
        node = next;
        state.play(node.getMove());
        next = node.selectChildOrAddPending(state, samplesBatch);
      }

      if (next == Node.KNOW_EXACT) {
        continue;
      }

      double value = 0;
      for (int i = 0; i < samplesBatch; i++) {
        S position;
        if (i < samplesBatch - 1) {
          position = state.clone();
        } else {
          position = state;
        }
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
