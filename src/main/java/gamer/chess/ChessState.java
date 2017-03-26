package gamer.chess;

import gamer.def.GameException;
import gamer.def.State;
import gamer.def.TerminalPositionException;

import java.util.ArrayList;
import java.util.List;

import static gamer.chess.Board.i2col;
import static gamer.chess.Board.i2row;
import static gamer.chess.Pieces.BISHOP;
import static gamer.chess.Pieces.BLACK;
import static gamer.chess.Pieces.EMPTY;
import static gamer.chess.Pieces.KING;
import static gamer.chess.Pieces.KNIGHT;
import static gamer.chess.Pieces.PAWN;
import static gamer.chess.Pieces.QUEEN;
import static gamer.chess.Pieces.ROOK;
import static gamer.chess.Pieces.WHITE;
import static gamer.chess.Pieces.black;
import static gamer.chess.Pieces.white;

@SuppressWarnings("PointlessBitwiseExpression")
public final class ChessState implements State<ChessState, ChessMove> {
  static byte WHITE_SHORT_CASTLING = 1;
  static byte WHITE_LONG_CASTLING = 2;
  static byte BLACK_SHORT_CASTLING = 4;
  static byte BLACK_LONG_CASTLING = 8;

  static int MOVES_WITHOUT_CAPTURE = 100;

  private static final int[] ROOK_DELTA_COL = {0, 1, 0, -1};
  private static final int[] ROOK_DELTA_ROW = {1, 0, -1, 0};
  private static final int[] KNIGHT_DELTA_COL = {1, 2, 2, 1, -1, -2, -2, -1};
  private static final int[] KNIGHT_DELTA_ROW = {2, 1, -1, -2, -2, -1, 1, 2};
  private static final int[] BISHOP_DELTA_COL = {1, 1, -1, -1};
  private static final int[] BISHOP_DELTA_ROW = {1, -1, -1, 1};
  private static final int[] QUEEN_DELTA_COL = {0, 1, 1, 1, 0, -1, -1, -1};
  private static final int[] QUEEN_DELTA_ROW = {1, 1, 0, -1, -1, -1, 0, 1};
  private static final int[] KING_DELTA_COL = {0, 1, 1, 1, 0, -1, -1, -1};
  private static final int[] KING_DELTA_ROW = {1, 1, 0, -1, -1, -1, 0, 1};
  private Board board;
  private boolean player = true;
  private byte castlings = (byte) (WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                                   BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING);
  /** -1 if no en passant pawn, otherwise the passed empty square */
  private int enPassant = -1;
  private int movesSinceCapture = 0;
  private int movesCount = 0;
  private List<ChessMove> moves = null;
  private int undoSquare1, undoSquare2, undoSquare3, undoSquare4;
  private byte undoPiece1, undoPiece2, undoPiece3, undoPiece4;
  private boolean check = false;
  private int kingSquare = -1;

  public ChessState() {
    this.board = new Board();
  }

  static ChessState fromFen(String fen) {
    return Fen.parse(fen);
  }

  // State implementation

  @Override
  public Chess getGame() {
    return Chess.getInstance();
  }

  @Override
  public boolean getPlayerBool() {
    return player;
  }

  @Override
  public boolean isTerminal() {
    if (movesSinceCapture > MOVES_WITHOUT_CAPTURE || drawByMaterial())
      return true;

    if (moves == null)
      generateMoves();

    return moves.size() == 0;
  }

  @Override
  public int getPayoff(int p) {
    if (!isTerminal()) {
      throw new TerminalPositionException();
    }

    if (isCheck()) {
      return (player == (p == 0)) ? -1 : 1;
    } else {
      return 0;
    }
  }

  @Override
  public List<ChessMove> getMoves() {
    if (moves == null)
      generateMoves();

    return moves;
  }

  @Override
  public void play(ChessMove move) {
    if (moves == null)
      generateMoves();

    if (!moves.contains(move)) {
      throw new GameException("Illegal move");
    }

    byte piece = board.getPiece(move.from);

    // We need board before the move, so this should be before applyToBoard().
    if (piece == PAWN || board.get(move.to) != EMPTY) {
      movesSinceCapture = 0;
    } else {
      movesSinceCapture++;
    }

    applyToBoard(move);

    player = !player;

    castlings = newCastlings();

    enPassant = -1;
    if (piece == PAWN) {
      if (!player && move.to - move.from == 2)
        enPassant = move.from + 1;
      if (player && move.from - move.to == 2)
        enPassant = move.to + 1;
    }

    movesCount++;

    moves = null;
  }

  @Override
  public String moveToString(ChessMove move) {
    return AlgebraicNotation.moveToString(this, move);
  }

  @Override
  public ChessMove parseMove(String moveStr) {
    return AlgebraicNotation.parse(this, moveStr);
  }

  @Override
  public ChessState clone() {
    try {
      ChessState result = (ChessState) super.clone();
      result.board = board.clone();
      if (moves != null) {
        result.moves = new ArrayList<>(moves);
      }
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int row = 8; row >= 1; row--) {
      for (int col = 1; col <= 8; col++) {
        builder.append(" ").append(Pieces.piece2a(board.get(col, row)));
      }
      builder.append("\n");
    }
    return builder.toString();
  }

  public byte get(int square) {
    return board.get(square);
  }

  public byte get(String square) {
    return board.get(square);
  }

  public byte get(int col, int row) {
    return board.get(col + 1, row + 1);
  }

  public byte getCastlings() {
    return castlings;
  }

  public void setCastlings(byte castlings) {
    this.castlings = castlings;
  }

  public int getEnPassant() {
    return enPassant;
  }

  public void setEnPassant(int square) {
    enPassant = square;
  }

  public int getMovesSinceCapture() {
    return movesSinceCapture;
  }

  public void setMovesSinceCapture(int moves) {
    movesSinceCapture = moves;
  }

  public int getMovesCount() {
    return movesCount;
  }

  public void setMovesCount(int moves) {
    movesCount = moves;
  }


  public void set(int square, byte piece) {
    board.set(square, piece);
  }

  public void set(int square, int piece) {
    set(square, (byte)piece);
  }

  public void setPlayer(boolean player) {
    this.player = player;
  }

  private byte newCastlings() {
    byte castlings = this.castlings;
    if (board.get("a1") != (WHITE | ROOK))
      castlings &= ~WHITE_LONG_CASTLING;

    if (board.get("a8") != (BLACK | ROOK))
      castlings &= ~BLACK_LONG_CASTLING;

    if (board.get("h1") != (WHITE | ROOK))
      castlings &= ~WHITE_SHORT_CASTLING;

    if (board.get("h8") != (BLACK | ROOK))
      castlings &= ~BLACK_SHORT_CASTLING;

    if (board.get("e1") != (WHITE | KING))
      castlings &= ~WHITE_LONG_CASTLING & ~WHITE_SHORT_CASTLING;

    if (board.get("e8") != (BLACK | KING))
      castlings &= ~BLACK_LONG_CASTLING & ~BLACK_SHORT_CASTLING;

    return castlings;
  }

  private void applyToBoard(ChessMove move) {
    undoSquare1 = move.from;
    undoPiece1 = board.get(move.from);
    undoSquare2 = move.to;
    undoPiece2 = board.get(move.to);
    undoSquare3 = -1;
    undoSquare4 = -1;

    byte piece = board.getPiece(move.from);
    board.move(move.from, move.to);

    if (piece == PAWN) {
      if (move.to == enPassant) {
        int takenSquare = player ? move.to - 1 : move.to + 1;
        undoSquare3 = takenSquare;
        undoPiece3 = board.get(undoSquare3);
        board.set(takenSquare, EMPTY);
      } else if (i2row(move.to) == 1 || i2row(move.to) == 8) {
        board.set(move.to, Pieces.withColor(move.promote, player));
      }
    }

    if (piece == KING && Math.abs(move.from - move.to) == 16) {
      int rookFrom;
      int rookTo;
      if (move.to > move.from) {
        rookFrom = move.from + 24;
        rookTo = move.from + 8;
      } else {
        rookFrom = move.from - 32;
        rookTo = move.from - 8;
      }

      undoSquare3 = rookFrom;
      undoPiece3 = board.get(rookFrom);
      undoSquare4 = rookTo;
      undoPiece4 = board.get(rookTo);

      board.move(rookFrom, rookTo);
    }
  }

  private void undoApplyToBoard() {
    board.set(undoSquare1, undoPiece1);
    board.set(undoSquare2, undoPiece2);
    if (undoSquare3 >= 0) {
      board.set(undoSquare3, undoPiece3);
      if (undoSquare4 >= 0) {
        board.set(undoSquare4, undoPiece4);
      }
    }
  }

  private void generateMoves() {
    findKing();
    check = isCheck();
    moves = new ArrayList<>();

    for (int square = 0; square < 64; square++) {
      if (board.isEmpty(square) || board.color(square) != player)
        continue;

      switch (board.getPiece(square)) {
        case PAWN:
          addPawnMoves(square);
          break;

        case ROOK:
          addLinearMoves(square, ROOK_DELTA_COL, ROOK_DELTA_ROW);
          break;

        case KNIGHT:
          addSpotMoves(square, KNIGHT_DELTA_COL, KNIGHT_DELTA_ROW);
          break;

        case BISHOP:
          addLinearMoves(square, BISHOP_DELTA_COL, BISHOP_DELTA_ROW);
          break;

        case QUEEN:
          addLinearMoves(square, QUEEN_DELTA_COL, QUEEN_DELTA_ROW);
          break;

        case KING:
          addSpotMoves(square, KING_DELTA_COL, KING_DELTA_ROW);
          if (!check)
            addCastlings();
          break;

        default:
          throw new RuntimeException("WTF is this piece?!");
      }
    }
  }

  private void addCastlings() {
    if (player) {
      if ((castlings & WHITE_SHORT_CASTLING) != 0 &&
          board.isEmpty("f1") && board.isEmpty("g1") &&
          moves.contains(ChessMove.of("e1", "f1"))) {
        assert board.get("e1") == white(KING);
        assert board.isEmpty("f1");
        assert board.isEmpty("g1");
        assert board.get("h1") == white(ROOK);
        addKingMoveIfValid(ChessMove.of("e1", "g1"));
      }
      if ((castlings & WHITE_LONG_CASTLING) != 0 &&
          board.isEmpty("b1") && board.isEmpty("c1") && board.isEmpty("d1") &&
          moves.contains(ChessMove.of("e1", "d1"))) {
        assert board.get("e1") == white(KING);
        assert board.isEmpty("d1");
        assert board.isEmpty("c1");
        assert board.isEmpty("b1");
        assert board.get("a1") == white(ROOK);
        addKingMoveIfValid(ChessMove.of("e1", "c1"));
      }
    } else {
      if ((castlings & BLACK_SHORT_CASTLING) != 0 &&
          board.isEmpty("f8") && board.isEmpty("g8") &&
          moves.contains(ChessMove.of("e8", "f8"))) {
        assert board.get("e8") == black(KING);
        assert board.isEmpty("f8");
        assert board.isEmpty("g8");
        assert board.get("h8") == black(ROOK);
        addKingMoveIfValid(ChessMove.of("e8", "g8"));
      }
      if ((castlings & BLACK_LONG_CASTLING) != 0 &&
          board.isEmpty("b8") && board.isEmpty("c8") && board.isEmpty("d8") &&
          moves.contains(ChessMove.of("e8", "d8"))) {
        assert board.get("e8") == black(KING);
        assert board.isEmpty("d8");
        assert board.isEmpty("c8");
        assert board.isEmpty("b8");
        assert board.get("a8") == (BLACK | ROOK);
        addKingMoveIfValid(ChessMove.of("e8", "c8"));
      }
    }
  }

  private void addPawnMoves(int square) {
    int row = i2row(square);
    int col = i2col(square);

    if (player) {

      if (row < 7) {
        if (board.isEmpty(square + 1)) {
          addIfValid(ChessMove.of(square, square + 1));
          if (row == 2 && board.isEmpty(square + 2))
            addIfValid(ChessMove.of(square, square + 2));
        }
        if (col != 1 && (board.isBlack(square - 7) || square - 7 == enPassant)) {
          addIfValid(ChessMove.of(square, square - 7));
        }
        if (col != 8 && (board.isBlack(square + 9) || square + 9 == enPassant)) {
          addIfValid(ChessMove.of(square, square + 9));
        }
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board.isEmpty(square + 1))
            addIfValid(ChessMove.of(square, square + 1, promote));
          if (col != 1 && board.isBlack(square - 7))
            addIfValid(ChessMove.of(square, square - 7, promote));
          if (col != 8 && board.isBlack(square + 9))
            addIfValid(ChessMove.of(square, square + 9, promote));
        }
      }

    } else {

      if (row > 2) {
        if (board.isEmpty(square - 1)) {
          addIfValid(ChessMove.of(square, square - 1));
          if (row == 7 && board.isEmpty(square - 2))
            addIfValid(ChessMove.of(square, square - 2));
        }
        if (col != 1 && (board.isWhite(square - 9) || square - 9 == enPassant))
          addIfValid(ChessMove.of(square, square - 9));
        if (col != 8 && (board.isWhite(square + 7) || square + 7 == enPassant))
          addIfValid(ChessMove.of(square, square + 7));
      } else {
        for (byte promote = ROOK; promote <= QUEEN; promote++) {
          if (board.isEmpty(square - 1))
            addIfValid(ChessMove.of(square, square - 1, promote));
          if (col != 1 && board.isWhite(square - 9))
            addIfValid(ChessMove.of(square, square - 9, promote));
          if (col != 8 && board.isWhite(square + 7))
            addIfValid(ChessMove.of(square, square + 7, promote));
        }
      }

    }
  }

  private void addLinearMoves(int square, int[] colDelta, int[] rowDelta) {
    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int dc = colDelta[idelta];
      int dr = rowDelta[idelta];

      int col = i2col(square);
      int row = i2row(square);

      while (true) {
        col += dc;
        row += dr;

        if (col > 8 || col < 1 || row > 8 || row < 1)
          break;

        int currentSquare = Board.cr2i(col, row);
        byte piece = board.get(currentSquare);

        if (piece == EMPTY) {
          addIfValid(ChessMove.of(square, currentSquare));
        } else if (Pieces.color(piece) != player) {
          addIfValid(ChessMove.of(square, currentSquare));
          break;
        } else {
          break;
        }
      }
    }
  }

  private void addSpotMoves(int square, int[] colDelta, int[] rowDelta) {
    boolean isKing = (board.getPiece(square) == KING);

    for (int idelta = 0; idelta < colDelta.length; idelta++) {
      int col = i2col(square) + colDelta[idelta];
      int row = i2row(square) + rowDelta[idelta];

      if (col >= 1 && col <= 8 && row >= 1 && row <= 8 &&
          (board.isEmpty(col, row) || board.color(col, row) != player)) {
        ChessMove move = ChessMove.of(square, Board.cr2i(col, row));
        if (isKing) {
          addKingMoveIfValid(move);
        } else {
          addIfValid(move);
        }
      }
    }
  }

  private void findKing() {
    byte king = Pieces.withColor(KING, player);

    if (kingSquare < 0 || board.get(kingSquare) != king) {
      for (kingSquare = 0; kingSquare < 64; kingSquare++) {
        if (board.get(kingSquare) == king)
          break;
      }
    }
  }

  private void addKingMoveIfValid(ChessMove move) {
    assert board.getPiece(move.from) == KING;
    applyToBoard(move);

    if (!isCheck(move.to))
      moves.add(move);

    undoApplyToBoard();
  }

  private void addIfValid(ChessMove move) {
    assert board.getPiece(move.from) != KING;

    if (check || move.to == enPassant) {
      applyToBoard(move);
      if (!isCheck(kingSquare))
        moves.add(move);
      undoApplyToBoard();
      return;
    }

    int kingCol = i2col(kingSquare);
    int kingRow = i2row(kingSquare);
    int fromCol = i2col(move.from);
    int fromRow = i2row(move.from);
    boolean valid = true;

    if (kingRow == fromRow && kingRow != i2row(move.to)) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, 1, 0);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, -1, 0);
      }
    } else if (kingCol == fromCol && kingCol != i2col(move.to)) {
      if (fromRow > kingRow) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, 0, 1);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, ROOK, 0, -1);
      }
    } else if (fromCol - kingCol == fromRow - kingRow) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, 1, 1);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, -1, -1);
      }
    } else if (fromCol - kingCol == kingRow - fromRow) {
      if (fromCol > kingCol) {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, 1, -1);
      } else {
        valid = !checkByLinearPiece(move, kingCol, kingRow, BISHOP, -1, 1);
      }
    }

    if (valid)
      moves.add(move);
  }

  private boolean checkByLinearPiece(
      ChessMove move, int col, int row, byte attackingPiece, int cd, int rd) {
    applyToBoard(move);

    attackingPiece = Pieces.withColor(attackingPiece, !player);
    byte queen = Pieces.withColor(QUEEN, !player);
    while (true) {
      col += cd;
      row += rd;
      if (col > 8 || col < 1 || row > 8 || row < 1)
        break;

      byte piece = board.get(col, row);

      if (piece == attackingPiece || piece == queen) {
        undoApplyToBoard();
        return true;
      }

      if (piece != EMPTY)
        break;
    }

    undoApplyToBoard();
    return false;
  }

  // true if check to (not by) player
  private boolean isCheck() {
    return isCheck(kingSquare);
  }

  private boolean isCheck(int square) {
    int col = i2col(square);
    int row = i2row(square);

    if (player && row < 7 &&
        (col > 1 && board.get(square - 7) == (BLACK | PAWN) ||
         col < 8 && board.get(square + 9) == (BLACK | PAWN))) {
      return true;
    }

    if (!player && row > 2 &&
        (col > 1 && board.get(square - 9) == (WHITE | PAWN) ||
         col < 8 && board.get(square + 7) == (WHITE | PAWN))) {
      return true;
    }

    return checkBySpotPiece(square, KNIGHT, KNIGHT_DELTA_COL, KNIGHT_DELTA_ROW) ||
           checkBySpotPiece(square, KING, KING_DELTA_COL, KING_DELTA_ROW) ||
           checkByLinearPieces(
              square, ROOK, QUEEN, ROOK_DELTA_COL, ROOK_DELTA_ROW) ||
           checkByLinearPieces(
              square, BISHOP, QUEEN, BISHOP_DELTA_COL, BISHOP_DELTA_ROW);
  }

  private boolean checkBySpotPiece(
      int square, byte piece, int[] colDelta, int[] rowDelta) {
    piece = Pieces.withColor(piece, !player);
    int kingCol = i2col(square);
    int kingRow = i2row(square);

    for (int i = 0; i < colDelta.length; i++) {
      int col = kingCol + colDelta[i];
      int row = kingRow + rowDelta[i];

      if (col >= 1 && col <= 8 && row >= 1 && row <= 8 &&
          board.get(col, row) == piece) {
        return true;
      }
    }

    return false;
  }

  private boolean checkByLinearPieces(
      int square, byte piece1, byte piece2, int[] colDelta, int[] rowDelta) {
    piece1 = Pieces.withColor(piece1, !player);
    piece2 = Pieces.withColor(piece2, !player);
    int kingCol = i2col(square);
    int kingRow = i2row(square);

    for (int i = 0; i < colDelta.length; i++) {
      int col = kingCol;
      int row = kingRow;
      while (true) {
        col += colDelta[i];
        row += rowDelta[i];
        if (col > 8 || col < 1 || row > 8 || row < 1)
          break;

        byte piece = board.get(col, row);

        if (piece == piece1 || piece == piece2)
          return true;

        if (piece != EMPTY)
          break;
      }
    }

    return false;
  }

  private boolean drawByMaterial() {
    byte leftPiece = EMPTY;

    for (int square = 0; square < 64; square++) {
      byte piece = board.getPiece(square);
      if (piece == EMPTY || piece == KING)
        continue;
      if (board.color(square) != player || leftPiece != EMPTY)
        return false;
      leftPiece = piece;
    }

    return leftPiece == KNIGHT || leftPiece == BISHOP;
  }
}
