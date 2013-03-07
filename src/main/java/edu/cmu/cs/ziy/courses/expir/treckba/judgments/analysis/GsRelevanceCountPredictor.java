package edu.cmu.cs.ziy.courses.expir.treckba.judgments.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GsRelevanceCountPredictor {

  private int begin, end;

  public GsRelevanceCountPredictor(int begin, int end) {
    this.begin = begin;
    this.end = end;
  }

  public List<CountErrorPair> predict(double[] recalls, int topK) {
    List<CountErrorPair> pairs = new ArrayList<CountErrorPair>();
    for (int count = begin; count < end; count++) {
      pairs.add(new CountErrorPair(count, calcError(count, recalls)));
    }
    Collections.sort(pairs);
    return pairs.subList(0, Math.min(topK, pairs.size()));
  }

  private double calcError(int count, double[] recalls) {
    double errorSum = 0;
    for (double recall : recalls) {
      errorSum += calcError(count, recall);
    }
    return errorSum;
  }

  private double calcError(int count, double recall) {
    double estimateTp = count * recall;
    int roundTp = (int) Math.round(estimateTp);
    return (estimateTp - roundTp) * (estimateTp - roundTp);
  }

  public static void main(String[] args) {
    GsRelevanceCountPredictor predictor = new GsRelevanceCountPredictor(1, 10000);
    double[] socialRecalls = { 0.8708, 0.7025, 0.5725 };
    List<CountErrorPair> socialCountErrorPairs = predictor.predict(socialRecalls, 5);
    for (CountErrorPair pair : socialCountErrorPairs) {
      System.out.println(pair);
    }
    System.out.println();
    double[] newsRecalls = { 0.9013, 0.6181, 0.6095 };
    List<CountErrorPair> newsCountErrorPairs = predictor.predict(newsRecalls, 5);
    for (CountErrorPair pair : newsCountErrorPairs) {
      System.out.println(pair);
    }
    System.out.println();
    double[] linkRecalls = { 0.8807, 0.5191, 0.5411 };
    List<CountErrorPair> linkCountErrorPairs = predictor.predict(linkRecalls, 5);
    for (CountErrorPair pair : linkCountErrorPairs) {
      System.out.println(pair);
    }
  }
}

class CountErrorPair implements Comparable<CountErrorPair> {

  public int count;

  public double error;

  public CountErrorPair(int count, double error) {
    this.count = count;
    this.error = error;
  }

  @Override
  public int compareTo(CountErrorPair pair) {
    return Double.compare(error, pair.error);
  }

  @Override
  public String toString() {
    return count + ": " + error;
  }

}
