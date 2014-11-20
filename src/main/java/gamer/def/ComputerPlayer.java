package gamer.def;

import java.util.Random;
import java.util.concurrent.ExecutorService;

public interface ComputerPlayer extends Player {
  void setRandom(Random random);
  void setMaxWorkers(int maxWorkers);
  void setMaxSamples(long maxSamples);
  void setTimeout(long timout);

  String getReport();
}
