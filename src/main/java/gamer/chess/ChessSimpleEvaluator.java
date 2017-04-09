package gamer.chess;

import gamer.def.Evaluator;

public class ChessSimpleEvaluator implements Evaluator<ChessState> {
  private static final double PIECE_SCORE[] = {
      0, 0.01, 0.05, 0.03, 0.03, 0.09, 0, 0,
      0, -0.01, -0.05, -0.03, -0.03, -0.09, 0, 0};

  private static final ChessSimpleEvaluator INSTANCE = new ChessSimpleEvaluator();

  public static ChessSimpleEvaluator getInstance() {
    return INSTANCE;
  }

  @Override
  public double evaluate(ChessState state) {
    double score = 0;
    for (byte piece : state.getBoard().board) {
      score += PIECE_SCORE[piece];
    }

    score *= Math.pow(0.99, state.getMovesSinceCapture());
    return score;
  }
}
