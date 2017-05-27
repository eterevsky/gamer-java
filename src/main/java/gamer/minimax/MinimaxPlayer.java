package gamer.minimax;

import gamer.def.ComputerPlayer;
import gamer.def.Evaluator;
import gamer.def.Move;
import gamer.def.State;

public class MinimaxPlayer<S extends State<S, M>, M extends Move>
    implements ComputerPlayer<S, M> {

  private double parentScoreCoefficient = 0.001;
  private double childScoreCoefficient = 0.999;

  private long timeout = 1000;
  private long maxSamples = Long.MAX_VALUE;
  private int maxDepth = Integer.MAX_VALUE;

  private long samples = 0;
  private long deadline = Long.MAX_VALUE;
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
      throw new UnsupportedOperationException(
          "MinimaxPlayer doesn't support multithreading.");
    }
  }

  @Override
  public void setMaxSamples(long maxSamples) {
    this.maxSamples = maxSamples;
  }

  @Override
  public void setTimeout(long timeout) {
    this.timeout = timeout == 0 ? Long.MAX_VALUE : timeout;
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
    SearchResult<M> result = null;
    for (int depth = 1;
         depth <= maxDepth && System.currentTimeMillis() < deadline &&
         samples < maxSamples; depth++) {
      SearchResult<M> newResult =
          search(state, depth, state.getGame().getMinPayoff(),
                 state.getGame().getMaxPayoff());
      if (newResult != null) {
        result = newResult;
        lastDepth = depth;
      }
    }

    selectedPayoff = result.score;
    selectedMoveStr = state.moveToString(result.move);
    return result.move;
  }

  @Override
  public String getReport() {
    return String
        .format("%s depth: %d, samples: %d, score: %f", selectedMoveStr,
                lastDepth, samples, selectedPayoff);
  }

  /* package */ SearchResult<M> search(
      S state, int depth, double minScore, double maxScore) {
    assert state.getPlayer() == 0 || state.getPlayer() == 1;
    if (System.currentTimeMillis() >= deadline || samples >= maxSamples) {
      return null;
    }

    double currentScore = evaluator.evaluate(state);

    samples++;
    if (depth == 0 || state.isTerminal()) {
      return new SearchResult<>(null, currentScore);
    }

    double bestChildScore =
        state.getPlayerBool() ? state.getGame().getMinPayoff() - 1
                              : state.getGame().getMaxPayoff() + 1;
    double minChildScore = Math.max((minScore - parentScoreCoefficient * currentScore) /
                           childScoreCoefficient, state.getGame().getMinPayoff());
    double maxChildScore = Math.min((maxScore - parentScoreCoefficient * currentScore) /
                           childScoreCoefficient, state.getGame().getMaxPayoff());
    M bestMove = null;

    for (M move : state.getMoves()) {
      S stateClone = state.clone();
      stateClone.play(move);
      SearchResult<M> childResult =
          search(stateClone, depth - 1, minChildScore, maxChildScore);
      if (childResult == null) break;
      if (state.getPlayerBool() ? (childResult.score > bestChildScore)
                                : (childResult.score < bestChildScore)) {
        bestChildScore = childResult.score;
        bestMove = move;
        if (state.getPlayerBool()) {
          if (bestChildScore > minChildScore) {
            minChildScore = bestChildScore;
            if (bestChildScore >= maxChildScore) {
              break;
            }
          }
        } else {
          if (bestChildScore < maxChildScore) {
            maxChildScore = bestChildScore;
            if (bestChildScore <= minChildScore) {
              break;
            }
          }
        }
      }
    }

    if (bestMove == null) return null;

    return new SearchResult<>(bestMove, childScoreCoefficient * bestChildScore +
                                        parentScoreCoefficient * currentScore);
  }

  static class SearchResult<M extends Move> {
    final M move;
    final double score;

    SearchResult(M move, double score) {
      this.move = move;
      this.score = score;
    }
  }
}
