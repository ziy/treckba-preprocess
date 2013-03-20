package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.FailedLoginException;

import org.wikipedia.Wiki;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import edu.cmu.cs.ziy.util.CalendarUtils;
import edu.cmu.cs.ziy.wiki.article.ExpandedWikipediaArticle;
import edu.cmu.cs.ziy.wiki.article.WikipediaArticleCache;
import edu.cmu.cs.ziy.wiki.entity.BoldTextExpander;
import edu.cmu.cs.ziy.wiki.entity.CategoryNameExpander;
import edu.cmu.cs.ziy.wiki.entity.InlinkAnchorTextExpander;
import edu.cmu.cs.ziy.wiki.entity.InlinkTitleExpander;
import edu.cmu.cs.ziy.wiki.entity.OutlinkAnchorTextExpander;
import edu.cmu.cs.ziy.wiki.entity.OutlinkTitleExpander;
import edu.cmu.cs.ziy.wiki.entity.RedirectExpander;
import edu.cmu.cs.ziy.wiki.entity.WikipediaEntity;
import edu.cmu.cs.ziy.wiki.entity.WikipediaEntityExpander;

public class WikipediaKeytermExpanderAggregator {

  private Wiki wiki;

  private Range<Calendar> period;

  private WikipediaEntityExpander[] expanders;

  private File cacheDir;

  public WikipediaKeytermExpanderAggregator(String domain, int throttle, String earliestTimeStr,
          String latestTimeStr, String dateFormatPattern, String cacheDirPath)
          throws FailedLoginException, IOException, ParseException {
    this.wiki = new Wiki(domain);
    this.wiki.setThrottle(throttle);
    this.period = Range.closedOpen(
            CalendarUtils.getGmtInstance(earliestTimeStr, dateFormatPattern),
            CalendarUtils.getGmtInstance(latestTimeStr, dateFormatPattern));
    this.cacheDir = new File(cacheDirPath);
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    this.expanders = new WikipediaEntityExpander[] { new CategoryNameExpander(),
        new OutlinkTitleExpander(), new InlinkTitleExpander(), new RedirectExpander(),
        new OutlinkAnchorTextExpander(), new InlinkAnchorTextExpander(), new BoldTextExpander() };
  }

  public ExpandedWikipediaArticle expandKeyterm(String urlname) throws IOException, ParseException,
          ClassNotFoundException {
    File cacheFilePath = new File(cacheDir, urlname + ".articles");
    WikipediaArticleCache.loadCache(cacheFilePath);
    String title = urlname.replace('_', ' ');
    ExpandedWikipediaArticle article = WikipediaArticleCache.loadExpandedArticle(title, period,
            expanders, wiki);

/*// @formatter:off
    article.clearRelatedEntities();
    for (WikipediaEntityExpander expander : expanders) {
      Set<WikipediaEntity> relatedEntities = expander.generateAndValidateExistence(title, period,
              wiki);
      article.addRelatedEntities(relatedEntities);
    }
    WikipediaArticleCache.writeCache(cacheFilePath);
    
 */// @formatter:on
    System.out.println(article.getSizeSummary());

    return article;
  }

  public static void main(String[] args) throws IOException, FailedLoginException, ParseException,
          ClassNotFoundException {
    Logger.getLogger("wiki").setLevel(Level.SEVERE);
    String domain = "en.wikipedia.org";
    int throttle = 5000;
    String earliestTimeStr = "2011-10-07-14";
    String latestTimeStr = "2012-05-02-00";
    String dateFormatPattern = "yyyy-MM-dd-HH";
    String cacheDirPath = "keyterm-cache/";
    String keytermFile = "src/main/resources/expanded-keyterms.tsv";

    BufferedWriter writer = Files.newWriter(new File(keytermFile), Charset.defaultCharset());
    WikipediaKeytermExpanderAggregator wke = new WikipediaKeytermExpanderAggregator(domain,
            throttle, earliestTimeStr, latestTimeStr, dateFormatPattern, cacheDirPath);

/*// @formatter:off
    String topicName = "William_D._Cohan";
    ExpandedWikipediaArticle keyterms = wke.expandKeyterm(topicName);
    for (WikipediaEntity entity : keyterms.getRelatedEntities()) {
      writer.write(entity.getText() + "\t" + keyterms.getEntity().getText() + "\t"
              + entity.getRelation() + "\t"
              + CalendarUtils.rangeSetToString(entity.getValidPeriods(), CalendarUtils.YMDH_FORMAT)
              + "\n");
    }
*/// @formatter:on

    String jsonPath = "data/trec-kba-ccr-2012-scorer-and-full-annotation/trec-kba-ccr-2012.filter-topics.json";
    BufferedReader jsonReader = new BufferedReader(new FileReader(jsonPath));
    TrecKbaTopics topics = TrecKbaTopics.readTrecKbaTopics(jsonReader);
    jsonReader.close();
    for (String topicName : topics.getTopicNames()) {
      ExpandedWikipediaArticle keyterm = wke.expandKeyterm(topicName);
      HashSet<WikipediaEntity> allEntities = Sets.newHashSet(keyterm.getRelatedEntities());
      allEntities.add(keyterm.getEntity());
      for (WikipediaEntity entity : allEntities) {
        writer.write(entity.getText().replaceAll("\\s+", " ")
                + "\t"
                + keyterm.getEntity().getText()
                + "\t"
                + entity.getRelation()
                + "\t"
                + CalendarUtils.rangeSetToString(entity.getValidPeriods(),
                        CalendarUtils.YMDH_FORMAT) + "\n");
      }
    }

    writer.close();

  }
}
