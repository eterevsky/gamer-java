package gamer.treegame;

import static gamer.def.GameResult.WIN;
import static gamer.def.GameResult.LOSS;

public final class TreeGameInstances {
  // See game0.svg
  public static final TreeGame GAME0 =
      TreeGame.newBuilder().setRoot(0)
          .addLastMove(0, 1, WIN).addMove(0, 2)
          .addLastMove(2, 3, LOSS)
          .toGame();

  // See game1.svg
  public static final TreeGame GAME1 =
      TreeGame.newBuilder().setRoot(0)
          .addMove(0, 1).addMove(0, 2)
          .addMove(1, 3).addMove(2, 3).addLastMove(2, 4, LOSS)
          .addLastMove(3, 5, WIN)
          .toGame();

  // See game2.svg
  // Node 1 has lower winning percent for random games than node 2, but has a
  // winning strategy for player 1, as opposed for node 2 which has winning
  // strategy for player 2.
  public static final TreeGame GAME2 =
      TreeGame.newBuilder().setRoot(0)
          .addMove(0, 1).addMove(0, 2)
          .addMove(1, 3).addMove(1, 4)
          .addMove(2, 5).addMove(2, 6).addMove(2, 7)
          .addLastMove(3, 8, WIN).addLastMove(3, 9, LOSS)
          .addLastMove(3, 10, LOSS)
          .addLastMove(4, 11, WIN).addLastMove(4, 12, LOSS)
          .addLastMove(4, 13, LOSS)
          .addLastMove(5, 14, LOSS)
          .addLastMove(6, 15, WIN).addLastMove(6, 16, WIN)
          .addLastMove(7, 17, WIN).addLastMove(6, 18, WIN)
          .toGame();

  // See game3.svg
  // Correct first move is 0 -> 2.
  public static final TreeGame GAME3 =
      TreeGame.newBuilder().setRoot(0)
          .addMove(0, 1).addMove(0, 2).addMove(0, 3)
          .addLastMove(1, 4, LOSS).addMove(1, 5)
          .addMove(2, 5).addLastMove(2, 6, WIN)
          .addLastMove(3, 6, WIN).addLastMove(3, 7, LOSS)
          .addLastMove(5, 8, WIN).addLastMove(5, 9, LOSS)
          .toGame();
}
