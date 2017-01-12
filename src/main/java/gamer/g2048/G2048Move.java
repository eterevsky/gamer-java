package gamer.g2048;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class G2048Move implements Move {
  static private final List<G2048Move> RANDOM2 = genRandom(2);
  static private final List<G2048Move> RANDOM4 = genRandom(4);
  static final List<G2048Move> PLAYER_MOVES = Arrays.asList(
      new G2048Move(Direction.RIGHT),
      new G2048Move(Direction.UP),
      new G2048Move(Direction.LEFT),
      new G2048Move(Direction.DOWN));

  enum Direction {
    RIGHT, UP, LEFT, DOWN
  };

  boolean random;
  int tile;
  int value;
  Direction direction;

  private G2048Move(int tile, int value) {
    this.random = true;
    this.tile = tile;
    this.value = value;
  }

  private G2048Move(Direction direction) {
    this.random = false;
    this.direction = direction;
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
