package gamer.chess;

import gamer.def.Game;
import gamer.def.GameStateMut;

public final class Chess implements Game<Chess> {
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

  public GameStateMut<Chess> newGameMut() {
    throw new RuntimeException("Not implemented.");
  }
}
