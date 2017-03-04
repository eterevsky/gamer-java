package gamer.util;

import static java.lang.Math.sqrt;

public class ConfidenceInterval {
  /**
   * http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Wilson_score_interval
   * http://en.wikipedia.org/wiki/Normal_distribution#Quantile_function
   */
  public static Interval binomialWilson(int success, int failure) {
    double z = 3.891;  // for 99.99%

    double n = success + failure;
    double c = 1 / (n + z*z);
    double center = c * (success + z*z / 2);
    double err = c * z * sqrt((double)success * failure / n + z * z / 4);

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
