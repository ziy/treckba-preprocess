package edu.cmu.cs.ziy.wiki.article;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.wikipedia.Wiki;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

import edu.cmu.cs.ziy.courses.expir.treckba.topics.WikipediaEntityExpander;
import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

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
    WikipediaArticle article;
    if (articleCache.contains(title, period)) {
      article = articleCache.get(title, period);
    } else {
      article = WikipediaArticle.newPeriodicalArticle(title, period.upperEndpoint(),
              period.lowerEndpoint(), wiki);
      articleCache.put(title, period, article);
    }
    return article;
  }

  // TODO Need to include expanded keyterms
  public static ExpandedWikipediaArticle loadExpandedArticle(String title, Range<Calendar> period,
          WikipediaEntityExpander[] expanders, Wiki wiki) throws IOException {
    ExpandedWikipediaArticle expandedArticle;
    if (expandedArticleCache.contains(title, period)) {
      expandedArticle = expandedArticleCache.get(title, period);
    } else {
      expandedArticle = ExpandedWikipediaArticle.newPeriodicalArticle(title,
              period.upperEndpoint(), period.lowerEndpoint(), wiki);
      expandedArticleCache.put(title, period, expandedArticle);
    }
    return expandedArticle;
  }
}