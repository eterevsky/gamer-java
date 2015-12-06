package gamer.dominion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gamer.def.Position;
import gamer.def.TerminalPositionException;

public final class DominionState
    implements Position<DominionState, DominionMove> {

  private Dominion game;

  DominionState(Dominion game) {
    this.game = game;
  }

  public int getPlayer() {
    throw new UnsupportedOperationException();
  }

  public boolean isTerminal() {
    return false;
  }

  public int getPayoff(int player) {
    throw new TerminalPositionException();    
  }

  public List<DominionMove> getMoves() {
    return new ArrayList<>();
  }

  public DominionMove getRandomMove(Random rng) {
    throw new UnsupportedOperationException();
  }

  public void play(DominionMove move) {
    throw new UnsupportedOperationException();    
  }

  public DominionMove parseMove(String moveStr) {
    throw new UnsupportedOperationException();    
  }

  public DominionState clone() {
    throw new UnsupportedOperationException();    
  }

  public String toString() {
    throw new UnsupportedOperationException();    
  }
}