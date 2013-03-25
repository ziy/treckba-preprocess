package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.tmatesoft.sqljet.core.SqlJetException;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

import edu.cmu.cs.ziy.util.bing.NormalizedGoogleDistanceCalculater;

public class ExpandedKeytermBingDistanceCalculater {

  public static void main(String[] args) throws ClientProtocolException, IOException,
          SqlJetException {
    NormalizedGoogleDistanceCalculater calculater = new NormalizedGoogleDistanceCalculater(
            new File("bing-cache/bing-expanded-keyterm-searches.db3"), 1);
    List<String> lines = Files.readLines(new File("src/main/resources/expanded-keyterms.tsv"),
            Charset.defaultCharset());
    BufferedWriter similarityWriter = Files.newWriter(new File(
            "src/main/resources/bing-distance.tsv"), Charset.defaultCharset());
    Map<String, Double> sumWebDist = Maps.newHashMap();
    Map<String, Double> sumNewsDist = Maps.newHashMap();
    Map<String, Integer> queryCount = Maps.newHashMap();

    for (String line : lines) {
      String[] fields = line.split("\t");
      if (fields.length < 4) {
        continue;
      }
      String keyterm = fields[0];
      String original = fields[1];
      keyterm = keyterm.replaceAll("Category:", "");
      keyterm = keyterm.replaceAll("List of ", "");
      keyterm = keyterm.replaceAll("\\s*\\(.*?\\)\\s*", "");
      String combined = keyterm + " " + original;
      double webDist = calculater.calcWebDistance(keyterm, original, combined);
      double newsDist = calculater.calcNewsDistance(keyterm, original, combined);
      similarityWriter.write(keyterm + "\t" + original + "\t" + webDist + "\t"
              + calculater.getLogNumWebHits(keyterm) + "\t" + calculater.getLogNumWebHits(original)
              + "\t" + calculater.getLogNumWebHits(combined) + "\t" + newsDist + "\t"
              + calculater.getLogNumNewsHits(keyterm) + "\t"
              + calculater.getLogNumNewsHits(original) + "\t"
              + calculater.getLogNumNewsHits(combined) + "\n");
      sumWebDist.put(keyterm, sumWebDist.containsKey(keyterm) ? (sumWebDist.get(keyterm) + webDist)
              : webDist);
      sumNewsDist.put(keyterm,
              sumNewsDist.containsKey(keyterm) ? (sumNewsDist.get(keyterm) + newsDist) : newsDist);
      queryCount.put(keyterm, queryCount.containsKey(keyterm) ? (queryCount.get(keyterm) + 1) : 1);
    }
    similarityWriter.close();

    BufferedWriter accumulatedSimilarityWriter = Files.newWriter(new File(
            "src/main/resources/accumulated-bing-distance.tsv"), Charset.defaultCharset());
    for (String query : sumWebDist.keySet()) {
      accumulatedSimilarityWriter.write(query + "\t" + sumWebDist.get(query) + "\t"
              + sumWebDist.get(query) / queryCount.get(query) + "\t" + sumNewsDist.get(query)
              + "\t" + sumNewsDist.get(query) / queryCount.get(query) + "\t"
              + (sumWebDist.get(query) + sumNewsDist.get(query)) + "\t"
              + (sumWebDist.get(query) + sumNewsDist.get(query)) / queryCount.get(query) + "\n");
    }
    accumulatedSimilarityWriter.close();
  }
}
