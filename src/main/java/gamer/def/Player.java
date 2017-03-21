package gamer.def;

public interface Player<P extends State<P, M>, M extends Move> {
  String getName();

  boolean isExternal();

  M selectMove(P position);
}
