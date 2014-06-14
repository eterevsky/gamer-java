package gamer;

public interface Player {
  public <T extends Game> Move<T> selectMove(GameState<T> state)
      throws Exception;
}
