package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

import com.google.common.collect.Range;

import edu.cmu.cs.ziy.util.CalendarUtils;
import edu.cmu.cs.ziy.wiki.article.ExpandedWikipediaArticle;
import edu.cmu.cs.ziy.wiki.article.WikipediaArticleCache;
import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;

public class WikipediaKeytermExpanderAggregator {

  private Wiki wiki;

  private Range<Calendar> period;

  private WikipediaEntityExpander[] expanders;

  public WikipediaKeytermExpanderAggregator(String domain, int throttle, String earliestTimeStr,
          String latestTimeStr, String dateFormatPattern) throws FailedLoginException, IOException,
          ParseException {
    this.wiki = new Wiki(domain);
    this.wiki.setThrottle(throttle);
    this.period = Range.closedOpen(
            CalendarUtils.getGmtInstance(earliestTimeStr, dateFormatPattern),
            CalendarUtils.getGmtInstance(latestTimeStr, dateFormatPattern));
    this.expanders = new WikipediaEntityExpander[] { new CategoryEntityExpander(),
        new OutlinkEntityExpander(), new InlinkEntityExpander(), new RedirectEntityExpander() };
  }

  public ExpandedWikipediaArticle expandKeyterm(String title) throws IOException, ParseException {
    title = title.replace('_', ' ');
    ExpandedWikipediaArticle article = WikipediaArticleCache.loadExpandedArticle(title, period,
            expanders, wiki);

    for (WikipediaEntityExpander expander : expanders) {
      Set<WikipediaEntity> relatedEntities = expander.generateAndValidateExistence(title, period,
              wiki);
      article.addRelatedEntities(relatedEntities);
    }

    System.out.println(article.getSizeSummary());
    System.out.println(article.getRelatedEntities());

    return article;
  }

  public static void main(String[] args) throws IOException, FailedLoginException, ParseException,
          ClassNotFoundException {
    // Logger.getLogger("wiki").setLevel(Level.SEVERE);
    String domain = "en.wikipedia.org";
    int throttle = 5000;
    String earliestTimeStr = "2011-10-07-14";
    String latestTimeStr = "2012-05-02-00";
    String dateFormatPattern = "yyyy-MM-dd-HH";
    String cacheFilePath = "src/main/resources/data/wikipedia-articles.cache";
    WikipediaArticleCache.loadCache(cacheFilePath);

    // String topicName = "William_D._Cohan";
    WikipediaKeytermExpanderAggregator wke = new WikipediaKeytermExpanderAggregator(domain,
            throttle, earliestTimeStr, latestTimeStr, dateFormatPattern);
    // wke.expandKeyterm(topicName);

    String jsonPath = "data/trec-kba-ccr-2012-scorer-and-full-annotation/trec-kba-ccr-2012.filter-topics.json";
    BufferedReader jsonReader = new BufferedReader(new FileReader(jsonPath));
    TrecKbaTopics topics = TrecKbaTopics.readTrecKbaTopics(jsonReader);
    jsonReader.close();
    for (String topicName : topics.getTopicNames()) {
      wke.expandKeyterm(topicName);
    }

    WikipediaArticleCache.writeCache(cacheFilePath);
  }
}
