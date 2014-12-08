package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import gamer.def.Solver;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import java.util.Random;

public final class TestGenericPlayer {

  private class MockSampler extends Sampler<TreeGameState, TreeGameMove> {
    boolean wasCalled = false;
    private Node<TreeGameState, TreeGameMove> root;

    MockSampler(Node<TreeGameState, TreeGameMove> root) {
      super(root, 123456L + 239, 234, 12, null);
      this.root = root;
    }

    @Override
    public void run() {
      assertFalse(wasCalled);
      wasCalled = true;

      Node<TreeGameState, TreeGameMove> node1 = root.selectChildOrAddPending(10);
      node1.selectChildOrAddPending(10);
      node1.addSamples(10, 0.8);
      Node<TreeGameState, TreeGameMove> node2 = root.selectChildOrAddPending(12);
      node2.selectChildOrAddPending(12);
      node2.addSamples(12, 0.9);
      Node<TreeGameState, TreeGameMove> node3 = root.selectChildOrAddPending(5);
      node3.selectChildOrAddPending(5);
      node3.addSamples(5, 0.2);
    }
  }

  private class MockPlayer extends GenericPlayer<TreeGameState, TreeGameMove> {
    private Node<TreeGameState, TreeGameMove> root;
    private MockSampler sampler;
    MockPlayer(Node<TreeGameState, TreeGameMove> root, MockSampler sampler) {
      this.root = root;
      this.sampler = sampler;
    }

    @Override
    protected Node<TreeGameState, TreeGameMove> getRoot(TreeGameState state) {
      return root;
    }

    @Override
    protected long getCurrentTime() {
      return 123456L;
    }

    @Override
    protected MockSampler newSampler(
        Node<TreeGameState, TreeGameMove> root, long finishTime, long samplesLimit,
        int samplesBatch, Random random) {
      assertEquals(this.root, root);
      assertEquals(123456L + 239, finishTime);
      assertEquals(234, samplesLimit);
      assertEquals(12, samplesBatch);
      assertNull(random);
      return sampler;
    }
  }

  @Test(timeout=500)
  public void selectMove() {
    TreeGame game = TreeGame.newBuilder().setRoot(0)
        .addMove(0, 1).addMove(0, 2).addMove(0, 3)
        .toGame();

    Node<TreeGameState, TreeGameMove> root = new Node<TreeGameState, TreeGameMove>(
        null,
        game.newGame(),
        null,
        new NaiveMonteCarlo.Selector<TreeGameState, TreeGameMove>(),
        new NodeContext<TreeGameState, TreeGameMove>());

    MockSampler sampler = new MockSampler(root);
    MockPlayer player = new MockPlayer(root, sampler);
    player.setTimeout(239);
    player.setMaxSamples(234);
    player.setSamplesBatch(12);
    TreeGameMove move = player.selectMove(root.getPosition());
    assertEquals("-> 2", move.toString());
  }

  private class MockSolver implements Solver<TreeGameState, TreeGameMove> {
    @Override
    public Solver.Result<TreeGameMove> solve(TreeGameState position) {
      switch (position.getId()) {
        case 4: return new Solver.Result<>(1, 3, position.getMoveToNode(7));
        case 5: return new Solver.Result<>(1, 1, position.getMoveToNode(13));
        case 7: return new Solver.Result<>(1, 2, position.getMoveToNode(10));
        case 8: return new Solver.Result<>(1, 6, position.getMoveToNode(0));
        case 9: return new Solver.Result<>(-1, 1, position.getMoveToNode(11));
        case 10: return new Solver.Result<>(1, 1, position.getMoveToNode(12));
        default: return null;
      }
    }
  }

  @Test
  public void useSolver() {
    TreeGame game = TreeGameInstances.GAME4;
    MonteCarloUct<TreeGameState, TreeGameMove> player = new MonteCarloUct<>();
    player.addSolver(new MockSolver());
    player.setMaxSamples(100);
    player.setFindExact(true);

    TreeGameState state = game.newGame();
    state = state.play(player.selectMove(state));
    assertEquals(2, state.getId());
    state = state.play(player.selectMove(state));
    assertEquals(4, state.getId());
    state = state.play(player.selectMove(state));
    assertEquals(7, state.getId());
    state = state.play(player.selectMove(state));
    assertEquals(10, state.getId());
  }
}
