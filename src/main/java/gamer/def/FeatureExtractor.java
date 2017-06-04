package gamer.def;

public interface FeatureExtractor<S extends State<S, ?>> {
  int getFeatureCount();
  double[] extractFeatures(S state);
}
