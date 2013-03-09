package edu.cmu.cs.ziy.wiki.article;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.wikipedia.Wiki;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

public class WikipediaArticleCache {

  private static Table<String, Range<Calendar>, WikipediaArticle> articleCache;

  private static Table<String, Range<Calendar>, ExpandedWikipediaArticle> expandedArticleCache;

  @SuppressWarnings("unchecked")
  public static void loadCache(String inputFilePath) throws IOException, ClassNotFoundException {
    try {
      ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(
              inputFilePath)));
      articleCache = (Table<String, Range<Calendar>, WikipediaArticle>) ois.readObject();
      expandedArticleCache = (Table<String, Range<Calendar>, ExpandedWikipediaArticle>) ois
              .readObject();
      ois.close();
    } catch (FileNotFoundException e) {
      articleCache = HashBasedTable.create();
      expandedArticleCache = HashBasedTable.create();
    }
  }

  public static void writeCache(String outputFilePath) throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
            outputFilePath)));
    oos.writeObject(articleCache);
    oos.writeObject(expandedArticleCache);
    oos.close();
  }

  public static WikipediaArticle loadArticle(String title, Range<Calendar> period, Wiki wiki)
          throws IOException {
    if (articleCache.contains(title, period)) {
      return articleCache.get(title, period);
    }
    WikipediaArticle article = WikipediaArticle.newPeriodicalArticle(title, period.upperEndpoint(),
            period.lowerEndpoint(), wiki);
    articleCache.put(title, period, article);
    return article;
  }

  // TODO Need to include expanded keyterms
  public static ExpandedWikipediaArticle loadExpandedArticle(String title, Range<Calendar> period,
          Wiki wiki) throws IOException {
    if (expandedArticleCache.contains(title, period)) {
      return expandedArticleCache.get(title, period);
    }
    ExpandedWikipediaArticle expandedArticle = ExpandedWikipediaArticle.newPeriodicalArticle(title,
            period.upperEndpoint(), period.lowerEndpoint(), wiki);
    expandedArticleCache.put(title, period, expandedArticle);
    return expandedArticle;
  }
}
