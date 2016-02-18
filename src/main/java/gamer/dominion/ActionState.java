package gamer.dominion;

import java.util.List;

public interface ActionState {
  int getPlayer();

  List<DominionMove> getMoves();
}
