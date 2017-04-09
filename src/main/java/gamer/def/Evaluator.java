package gamer.def;

public interface Evaluator<S extends State<S, ?>> {
  double evaluate(S state);
}
