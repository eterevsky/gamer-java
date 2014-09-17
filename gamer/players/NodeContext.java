package gamer.players;

final class NodeContext {
  final boolean propagateExact;

  static NodeContext BASIC = new NodeContext(false);

  NodeContext(boolean propagateExact) {
    this.propagateExact = propagateExact;
  }
}
