package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

final class ShallowNode<G extends Game> {
  final Move<G> move;
  final GameState<G> state;
  int samples = 0;
  int processedSamples = 0;
  private double sumValue = 0;

  ShallowNode(GameState<G> parent, Move<G> move) {
    this.move = move;
    state = parent.play(move);
  }

  int getSamplesWithProcessed() {
    return samples + processedSamples;
  }

  double getValue(boolean player) {
    return player ? (sumValue / samples) : (1 - sumValue / samples);
  }

  double getValueWithProcessed(boolean player) {
    return (double) sumValue / (samples + processedSamples);
  }

  void addProcessedSamples(int samples) {
    this.processedSamples += samples;
  }

  void addSamples(double value, int samples) {
    sumValue += value;
    this.samples += samples;
    if (this.processedSamples > 0) {
      this.processedSamples -= samples;
    }
  }
}
