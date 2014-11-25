package gamer.def;

public interface Player<P extends Position<P, M>, M extends Move> {
  String getName();
  boolean isHuman();

  M selectMove(P position);
}
