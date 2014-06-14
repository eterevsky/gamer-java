package players;

class Sample<T> {
  T label;
  int nsamples;
  int result;

  Sample(T label, int nsamples, int result) {
    this.label = label;
    this.nsamples = nsamples;
    this.result = result;
  }
}
