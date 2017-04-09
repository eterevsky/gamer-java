package gamer.minimax;

import gamer.def.ComputerPlayer;
import gamer.def.Evaluator;
import gamer.def.Move;
import gamer.def.State;

public class MinimaxPlayer<S extends State<S, M>, M extends Move>
    implements ComputerPlayer<S, M> {

  private long timeout = 1000;
  private long maxSamples = 0;
  private int maxDepth = 0;

  private long samples = 0;
  private long deadline = 0;
  private double selectedPayoff = 0;
  private int lastDepth = 0;
  private String selectedMoveStr;

  private Evaluator<S> evaluator;

  public MinimaxPlayer() {
  }

  @Override
  public String getName() {
    return String.format("MinimaxPlayer(timeout=%01.1f)", timeout / 1000.0);
  }

  @Override
  public void setMaxWorkers(int maxWorkers) {
    if (maxWorkers > 1) {
      throw new RuntimeException(
          "MinimaxPlayer doesn't support multithreading.");
    }
  }

  @Override
  public void setMaxSamples(long maxSamples) {
    this.maxSamples = maxSamples;
  }

  @Override
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public void setEvaluator(Evaluator<S> evaluator) {
    this.evaluator = evaluator;
  }

  @Override
  public M selectMove(S state) {
    if (evaluator == null) {
      throw new RuntimeException("Minimax called with unspecified evaluator.");
    }
    deadline = timeout > 0 ? System.currentTimeMillis() + timeout : -1;
    samples = 0;
    int depth = 0;
    SearchResult<M> result = null;
    while ((maxDepth <= 0 || depth < maxDepth) &&
           (deadline <= 0 || System.currentTimeMillis() < deadline) &&
           (maxSamples <= 0 || samples < maxSamples)) {
      depth++;
      SearchResult<M> newResult = search(state, depth, state.getGame().getMinPayoff(), state.getGame().getMaxPayoff());
      if (newResult != null) {
        result = newResult;
        lastDepth = depth;
      }
    }

    selectedPayoff = result.payoff;
    selectedMoveStr = state.moveToString(result.move);
    return result.move;
  }

  @Override
  public String getReport() {
    return String
        .format("%s depth: %d, samples: %d, score: %f",
                selectedMoveStr, lastDepth, samples, selectedPayoff);
  }

  static class SearchResult<M extends Move> {
    private final M move;
    private final double payoff;

    SearchResult(M move, double payoff) {
      this.move = move;
      this.payoff = payoff;
    }
  }

  private SearchResult<M> search(S state, int depth, double minScore, double maxScore) {
    assert state.getPlayer() == 0 || state.getPlayer() == 1;

    if (state.isTerminal()) {
      samples++;
      return new SearchResult<>(null, state.getPayoff(0));
    }

    if (depth == 0) {
      samples++;
      return new SearchResult<>(null, evaluator.evaluate(state));
    }

    if (deadline > 0 && System.currentTimeMillis() > deadline ||
        maxSamples > 0 && samples > maxSamples) {
      return null;
    }

    double bestPayoff = state.getPlayerBool() ? state.getGame().getMinPayoff()
                                              : state.getGame().getMaxPayoff();
    M bestMove = state.getRandomMove();

    for (M move : state.getMoves()) {
      S stateClone = state.clone();
      stateClone.play(move);
      SearchResult<M> subresult = search(stateClone, depth - 1, minScore, maxScore);
      if (subresult == null) {
        return null;
      }
      double payoff = 0.9999 * subresult.payoff;
      if (state.getPlayerBool() ? (payoff > bestPayoff)
                                : (payoff < bestPayoff)) {
        bestPayoff = subresult.payoff;
        bestMove = move;
        if (state.getPlayerBool()) {
          if (bestPayoff > minScore) {
            minScore = bestPayoff;
            if (bestPayoff >= maxScore) {
              break;
            }
          }
        } else {
          if (bestPayoff < maxScore) {
            maxScore = bestPayoff;
            if (bestPayoff <= minScore) {
              break;
            }
          }
        }
      }
    }

    assert bestMove != null;

    return new SearchResult<>(bestMove, bestPayoff);
  }
}
