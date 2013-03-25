package edu.cmu.cs.ziy.util.bing;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.tmatesoft.sqljet.core.SqlJetException;

import com.google.common.collect.Maps;

import edu.cmu.cs.ziy.courses.expir.treckba.topics.BingExpandedKeytermSearcher;

public class NormalizedGoogleDistanceCalculater {

  private static final double logTotalWebPages = Math.log(14.48E9);

  private static final double logTotalNewsPages = Math.log(1E6);

  private BingExpandedKeytermSearcher searcher;

  private int pseudoCount;

  private Map<String, Double> query2logNumWebHits = Maps.newHashMap();

  private Map<String, Double> query2logNumNewsHits = Maps.newHashMap();

  public NormalizedGoogleDistanceCalculater(File dbFile, int pseudoCount) throws SqlJetException,
          IOException {
    this.pseudoCount = pseudoCount;
    this.searcher = new BingExpandedKeytermSearcher(dbFile);
  }

  public double calcWebDistance(String queryX, String queryY, String queryXY)
          throws ClientProtocolException, IOException, SqlJetException {
    return distance(getLogNumWebHits(queryX) + pseudoCount, getLogNumWebHits(queryY) + pseudoCount,
            getLogNumWebHits(queryXY) + pseudoCount, logTotalWebPages);
  }

  public double calcNewsDistance(String queryX, String queryY, String queryXY)
          throws ClientProtocolException, IOException, SqlJetException {
    return distance(getLogNumNewsHits(queryX) + pseudoCount, getLogNumNewsHits(queryY)
            + pseudoCount, getLogNumNewsHits(queryXY) + pseudoCount, logTotalNewsPages);
  }

  public double getLogNumNewsHits(String query) throws ClientProtocolException, IOException,
          SqlJetException {
    double logNumHits;
    if (query2logNumNewsHits.containsKey(query)) {
      logNumHits = query2logNumNewsHits.get(query);
    } else {
      logNumHits = Math.log(BingResults.readBingJsonResult(searcher.search(query)).getNewsTotal()
              + pseudoCount);
      query2logNumNewsHits.put(query, logNumHits);
    }
    return logNumHits;
  }

  public double getLogNumWebHits(String query) throws ClientProtocolException, IOException,
          SqlJetException {
    double logNumHits;
    if (query2logNumWebHits.containsKey(query)) {
      logNumHits = query2logNumWebHits.get(query);
    } else {
      logNumHits = Math.log(BingResults.readBingJsonResult(searcher.search(query)).getWebTotal()
              + pseudoCount);
      query2logNumWebHits.put(query, logNumHits);
    }
    return logNumHits;
  }

  public static double distance(int numHitsX, int numHitsY, int numHitsXY, int totalPages) {
    double logNumHitsX = Math.log(numHitsX);
    double logNumHitsY = Math.log(numHitsY);
    double logNumHitsXY = Math.log(numHitsXY);
    double logTotalPages = Math.log(totalPages);
    return distance(logNumHitsX, logNumHitsY, logNumHitsXY, logTotalPages);
  }

  private static double distance(double logNumHitsX, double logNumHitsY, double logNumHitsXY,
          double logTotalPages) {
    return (Math.max(logNumHitsX, logNumHitsY) - logNumHitsXY)
            / (logTotalPages - Math.min(logNumHitsX, logNumHitsY));
  }

}
