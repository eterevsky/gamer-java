package gamer.def;

public interface Player {
  String getName();
  boolean isHuman();

  <M extends Move, P extends Position<P, M>> M selectMove(P position);
}
