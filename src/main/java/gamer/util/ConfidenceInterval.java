package gamer.util;

import static java.lang.Math.sqrt;

public class ConfidenceInterval {
  /**
   * http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Wilson_score_interval
   * http://en.wikipedia.org/wiki/Normal_distribution#Quantile_function
   */
  public static Interval binomialWilson(int success, int failure) {
    double z = 3.891;

    double n = success + failure;
    double p = success / n;
    double c = 1 / (1 + z*z / n);
    double center = c * (p + z*z / (2*n));
    double err = c * sqrt(p * (1 - p) / n + z * z / (4 * n * n));

    return new Interval(center, err);
  }

  public static class Interval {
    public double center;
    public double err;

    private Interval(double center, double err) {
      this.center = center;
      this.err = err;
    }
  }
}
