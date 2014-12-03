package gamer.chess;

import gamer.def.Game;
import gamer.def.PositionMut;

public final class Chess implements Game {
  private static final Chess INSTANCE = new Chess();
  private static final ChessState INITIAL = ChessState.fromFen(
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

  private Chess() {}

  public static Chess getInstance() {
    return INSTANCE;
  }

  public ChessState newGame() {
    return INITIAL;
  }

  public PositionMut<?, ?> newGameMut() {
    throw new UnsupportedOperationException("Not implemented.");
  }

  public boolean hasRandomPlayer() {
    return false;
  }

  public int getPlayers() {
    return 2;
  }
}
