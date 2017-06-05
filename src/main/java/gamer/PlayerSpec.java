package gamer;

import com.google.gson.Gson;
import gamer.def.Move;
import gamer.def.Player;
import gamer.def.State;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class PlayerSpec {
  public static  <S extends State<S, M>, M extends Move> Player<S, M> parsePlayer(String path) {
    Reader reader = null;
    try {
      reader = Files.newBufferedReader(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    PlayerSpec<new Gson().fromJson
  }
}
