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

  @Override
  public ChessState newGame() {
    return INITIAL;
  }

  @Override
  public PositionMut<?, ?> newGameMut() {
    return INITIAL.toBuilder();
  }

  @Override
  public boolean hasRandomPlayer() {
    return false;
  }

  @Override
  public int getPlayersCount() {
    return 2;
  }
}
