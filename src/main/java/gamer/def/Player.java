package gamer.def;

public interface Player {
  String getName();
  boolean isHuman();

  <M extends Move, P extends Position<M>> M selectMove(P position);
}
