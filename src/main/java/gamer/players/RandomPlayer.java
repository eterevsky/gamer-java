package gamer.players;

import gamer.def.Position;
import gamer.def.Move;
import gamer.def.Player;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomPlayer<P extends Position<M>, M extends Move>
    implements Player<P, M> {
  private Random random = null;

  @Override
  public <M extends Move, P extends Position<P, M>> M selectMove(P position) {
    return position.getRandomMove(
        random == null ? ThreadLocalRandom.current() : random);
  }

  @Override
  public void setTimeout(long timeout) {
    return this;
  }

  @Override
  public void setSamplesLimit(long samplesLimit) {
    return this;
  }

  public void setName(String name) {
    return this;
  }

  public String getName() {
    return "RandomPlayer";
  }

  @Override
  public String getReport() {
    return "";
  }

  public void setRandom(Random random) {
    this.random = random;
    return this;
  }
}
