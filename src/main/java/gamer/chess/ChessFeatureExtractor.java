package gamer.chess;

import gamer.def.FeatureExtractor;
import gamer.def.TerminalPositionException;

public class ChessFeatureExtractor implements FeatureExtractor<ChessState> {
  private static final int PIECE_FEATURES[] = {
      0, /*pawn*/ 1, /*rook*/2, /*knight*/3, /*bishop*/4, /*queen*/5, 0, 0,
      0, 6, 7, 8, 9, 10, 0, 0};

  @Override
  public int getFeatureCount() {
    return 11;
  }

  @Override
  public double[] extractFeatures(ChessState state) {
    if (state.isTerminal()) {
      throw new TerminalPositionException();
    }
    double[] features = new double[11];
    features[0] = state.getPlayerBool() ? 1 : -1;
    for (byte piece : state.getBoard().board) {
      int feature = PIECE_FEATURES[piece];
      if (feature != 0) {
        features[feature] += 1;
      }
    }

    return features;
  }
}
