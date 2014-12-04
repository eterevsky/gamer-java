package gamer.util;

import gamer.def.TerminalPositionException;

public final class GameStatusInt {
  private static int PAYOFF_MASK = 3;
  private static int TERMINAL_MASK = 4;
  private static int PLAYER_MASK = 8;

  public static boolean isTerminal(int status) {
    return (status & TERMINAL_MASK) != 0;
  }

  public static int getPayoff(int status, int player) {
    return getPayoff(status, player == 0);
  }

  public static int getPayoff(int status, boolean player) {
    if (!isTerminal(status)) {
      throw new TerminalPositionException(
          "Requesting payoff for non-terminal position");
    }
    int v = (status & PAYOFF_MASK) - 1;
    return player ? v : -v;
  }

  public static boolean getPlayerBool(int status) {
    return (status & PLAYER_MASK) != 0;
  }

  public static int init() {
    return PLAYER_MASK;
  }

  public static int setPayoff(int status, int payoff) {
    status &= ~PAYOFF_MASK;
    status |= payoff + 1;
    return status;
  }

  public static int switchPlayer(int status) {
    return status ^ PLAYER_MASK;
  }
}
