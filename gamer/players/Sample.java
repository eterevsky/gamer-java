package gamer.players;

final class Sample<T> {
  final T label;
  final int nsamples;
  final int result;

  Sample(T label, int nsamples, int result) {
    this.label = label;
    this.nsamples = nsamples;
    this.result = result;
  }
}
