package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;
import org.wikipedia.Wiki.Revision;

import com.google.common.collect.Collections2;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.TreeRangeSet;

import edu.cmu.cs.ziy.util.CalendarUtils;
import edu.cmu.cs.ziy.wiki.ExpandedWikipediaArticle;
import edu.cmu.cs.ziy.wiki.WikipediaArticle;
import edu.cmu.cs.ziy.wiki.WikipediaArticleCache;
import edu.cmu.cs.ziy.wiki.WikipediaEntity;
import edu.cmu.cs.ziy.wiki.WikipediaNamespacePredicate;

public class WikipediaKeytermExpander {

  private Wiki wiki;

  private Calendar earliestTime;

  private Calendar latestTime;

  public WikipediaKeytermExpander(String domain, int throttle, String earliestTimeStr,
          String latestTimeStr, String dateFormatPattern) throws FailedLoginException, IOException,
          ParseException {
    this.wiki = new Wiki(domain);
    this.wiki.setThrottle(throttle);
    this.earliestTime = CalendarUtils.getGmtInstance(earliestTimeStr, dateFormatPattern);
    this.latestTime = CalendarUtils.getGmtInstance(latestTimeStr, dateFormatPattern);
  }

  public ExpandedWikipediaArticle expandKeyterm(String topicName) throws IOException,
          ParseException {
    // System.out.println(topicName);
    ExpandedWikipediaArticle article = WikipediaArticleCache.loadExpandedArticle(topicName,
            Range.closedOpen(earliestTime, latestTime), wiki);

    // redirects
    HashSet<String> redirects = Sets.newHashSet(wiki.whatLinksHere(topicName, true,
            Wiki.MAIN_NAMESPACE));
    for (String redirect : redirects) {
      List<Revision> redirectRevisions = wiki.getPageHistoryWithInitialVersion(redirect,
              latestTime, earliestTime);
      // assert redirectRevisions.size() == 1;
      if (redirectRevisions.size() < 1) {
        continue;
      }
      RangeSet<Calendar> periods = TreeRangeSet.create();
      periods.add(Range.closedOpen(redirectRevisions.get(0).getTimestamp(), CalendarUtils.PRESENT));
      article.addRelatedEntity(new WikipediaEntity(redirect, WikipediaEntity.Relation.REDIRECT,
              periods));
    }

    // inlinks
    HashSet<String> inlinks = Sets.newHashSet(wiki.whatLinksHere(topicName, Wiki.MAIN_NAMESPACE));
    SetView<String> nonRedirectsInlinks = Sets.difference(inlinks, redirects);
    for (String inlink : nonRedirectsInlinks) {
      WikipediaArticle related = WikipediaArticleCache.loadArticle(inlink,
              Range.closedOpen(earliestTime, latestTime), wiki);
      RangeSet<Calendar> periods = TreeRangeSet.create();
      for (Entry<Range<Calendar>, String> revision : related.getPeriodicContent().asMapOfRanges()
              .entrySet()) {
        if (containsLink(revision.getValue(), inlink)) {
          periods.add(Range.closedOpen(revision.getKey().lowerEndpoint(), revision.getKey()
                  .lowerEndpoint()));
        }
      }
      article.addRelatedEntity(new WikipediaEntity(inlink, WikipediaEntity.Relation.INLINK, periods));
    }

    // categories
    HashSet<String> categories = Sets.newHashSet(wiki.getCategories(topicName));
    for (String category : categories) {
      RangeSet<Calendar> periods = TreeRangeSet.create();
      for (Entry<Range<Calendar>, String> revision : article.getPeriodicContent().asMapOfRanges()
              .entrySet()) {
        if (containsLink(revision.getValue(), topicName)) {
          periods.add(Range.closedOpen(revision.getKey().lowerEndpoint(), revision.getKey()
                  .upperEndpoint()));
        }
      }
      article.addRelatedEntity(new WikipediaEntity(category, WikipediaEntity.Relation.CATEGORY,
              periods));
    }

    // outlinks
    HashSet<String> outlinks = Sets.newHashSet(wiki.getLinksOnPage(topicName));
    HashSet<String> mainNsOutlinks = Sets.newHashSet(Collections2.filter(outlinks,
            new WikipediaNamespacePredicate(wiki, Wiki.MAIN_NAMESPACE)));
    for (String outlink : mainNsOutlinks) {
      RangeSet<Calendar> periods = TreeRangeSet.create();
      for (Entry<Range<Calendar>, String> revision : article.getPeriodicContent().asMapOfRanges()
              .entrySet()) {
        if (containsLink(revision.getValue(), topicName)) {
          periods.add(Range.closedOpen(revision.getKey().lowerEndpoint(), revision.getKey()
                  .upperEndpoint()));
        }
      }
      article.addRelatedEntity(new WikipediaEntity(outlink, WikipediaEntity.Relation.OUTLINK,
              periods));
    }

    System.out.println(topicName
            + "\t"
            + article.getPeriodicContent().asMapOfRanges().size()
            + "\t"
            + Sets.filter(article.getRelatedEntities(),
                    new WikipediaEntity.RelationPredicate(WikipediaEntity.Relation.CATEGORY))
                    .size()
            + "\t"
            + Sets.filter(article.getRelatedEntities(),
                    new WikipediaEntity.RelationPredicate(WikipediaEntity.Relation.OUTLINK)).size()
            + "\t"
            + Sets.filter(article.getRelatedEntities(),
                    new WikipediaEntity.RelationPredicate(WikipediaEntity.Relation.REDIRECT))
                    .size()
            + "\t"
            + Sets.filter(article.getRelatedEntities(),
                    new WikipediaEntity.RelationPredicate(WikipediaEntity.Relation.INLINK)).size());

    return article;
  }

  private boolean containsLink(String text, String topicName) {
    String regex = ".*\\[{2}" + topicName + "(?:\\]{2}|\\|).*";
    return text.matches(regex);
  }

  public static void main(String[] args) throws IOException, FailedLoginException, ParseException,
          ClassNotFoundException {
    Logger.getLogger("wiki").setLevel(Level.SEVERE);
    String domain = "en.wikipedia.org";
    int throttle = 5000;
    String earliestTimeStr = "2011-10-07-14";
    String latestTimeStr = "2012-05-02-00";
    String dateFormatPattern = "yyyy-MM-dd-HH";
    String cacheFilePath = "src/main/resources/data/wikipedia-articles.cache";
    WikipediaArticleCache.loadCache(cacheFilePath);

    // String topicName = "William_D._Cohan";
    WikipediaKeytermExpander wke = new WikipediaKeytermExpander(domain, throttle, earliestTimeStr,
            latestTimeStr, dateFormatPattern);
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
