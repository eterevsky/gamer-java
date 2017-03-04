package gamer.g2048;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class G2048Move implements Move {
  static private final List<G2048Move> RANDOM2 = genRandom(1);
  static private final List<G2048Move> RANDOM4 = genRandom(2);

  static final G2048Move RIGHT = new G2048Move("right", 3, 4, -1);
  static final G2048Move UP = new G2048Move("up", 12, 1, -4);
  static final G2048Move LEFT = new G2048Move("left", 0, 4, 1);
  static final G2048Move DOWN = new G2048Move("down", 0, 1, 4);

  static final List<G2048Move> PLAYER_MOVES =
      Arrays.asList(RIGHT, UP, LEFT, DOWN);

  boolean random;

  // For random moves.
  int tile;
  int value;

  // For player moves.
  String directionStr;
  int startRow;
  int deltaRow;
  int deltaTile;

  static G2048Move parse(String str) {
    if (str == "right") {
      return RIGHT;
    }
    if (str == "left") {
      return LEFT;
    }
    if (str == "up") {
      return UP;
    }
    if (str == "down") {
      return DOWN;
    }

    String[] parts = str.split("\\s+");
    if (parts.length != 2) {
      throw new RuntimeException("Wrong move string: `" + str + "`");
    }

    int tile = G2048.BOARD.parseTile(parts[0]);
    int value;

    switch (parts[1]) {
      case "2": value = 1; break;
      case "4": value = 2; break;
      default: throw new RuntimeException("Wrong move string: `" + str + "`");
    }

    return G2048Move.of(tile, value);
  }

  private G2048Move(int tile, int value) {
    this.random = true;
    this.tile = tile;
    this.value = value;
  }

  private G2048Move(String directionStr, int startRow, int deltaRow, int
      deltaTile) {
    this.directionStr = directionStr;
    this.startRow = startRow;
    this.deltaRow = deltaRow;
    this.deltaTile = deltaTile;
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
    if (value == 1) {
      return RANDOM2.get(tile);
    } else if (value == 2) {
      return RANDOM4.get(tile);
    } else {
      throw new IllegalArgumentException(
          "Only random moves with value = 2 or 4 are allowed.");

    }
  }
}
