package gamer.chess.endings;

class GenerateEndingTables {
  public static void main(String[] args) {
    for (int i = 3; i < 7; i++) {
      EndingTable ending = new EndingTable(i, null);
      System.out.format("%d %d\n", i, ending.length());
    }
  }
}
