package gamer.g2048;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class G2048Move implements Move {
  static private final List<G2048Move> RANDOM2 = genRandom(2);
  static private final List<G2048Move> RANDOM4 = genRandom(4);
  static final G2048Move RIGHT = new G2048Move("right");
  static final G2048Move UP = new G2048Move("up");
  static final G2048Move LEFT = new G2048Move("left");
  static final G2048Move DOWN = new G2048Move("down");
  static final List<G2048Move> PLAYER_MOVES =
      Arrays.asList(RIGHT, UP, LEFT, DOWN);

  boolean random;
  int tile;
  int value;
  String directionStr;

  static G2048Move parse(String str) {
    throw new UnsupportedOperationException();
  }

  private G2048Move(int tile, int value) {
    this.random = true;
    this.tile = tile;
    this.value = value;
  }

  private G2048Move(String directionStr) {
    this.directionStr = directionStr;
  }

  boolean isRandom() {
    return random;
  }

  static List<G2048Move> genRandom(int value) {
    ArrayList<G2048Move> moves = new ArrayList<>();
    for (int i = 0; i < 16; i++) {
      moves.add(new G2048Move(i, value));
    }
    return moves;
  }

  static G2048Move of(int tile, int value) {
    if (value == 2) {
      return RANDOM2.get(tile);
    } else if (value == 4) {
      return RANDOM4.get(tile);
    } else {
      throw new IllegalArgumentException(
          "Only random moves with value = 2 or 4 are allowed.");

    }
  }
}
