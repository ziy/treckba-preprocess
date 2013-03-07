package edu.cmu.cs.ziy.courses.expir.treckba.topics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.wikipedia.Wiki;
import org.wikipedia.Wiki.Revision;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;

import edu.cmu.cs.ziy.courses.expir.treckba.topics.WikipediaEntity.Relation;
import edu.cmu.cs.ziy.util.CalendarUtils;
import edu.cmu.cs.ziy.util.DefaultPeriodicallyChangedObject;
import edu.cmu.cs.ziy.util.GuavaUtils;

public class WikipediaArticle extends DefaultPeriodicallyChangedObject<String> implements
        Serializable {

  private static final long serialVersionUID = 1L;

  protected WikipediaEntity entity;

  public static Map<String, WikipediaArticle> cache = Maps.newHashMap();

  @SuppressWarnings("unchecked")
  public static void loadCache(String inputFilePath) throws IOException, ClassNotFoundException {
    try {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFilePath));
      cache = (Map<String, WikipediaArticle>) ois.readObject();
      ois.close();
    } catch (FileNotFoundException e) {
      return;
    }
  }

  public static void writeCache(String outputFilePath) throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFilePath));
    oos.writeObject(cache);
    oos.close();
  }

  protected WikipediaArticle(String title) {
    super();
    this.entity = WikipediaEntity.getPresentInstance(title, Relation.ORIGINAL,
            CalendarUtils.BIG_BANG);
  }

  protected WikipediaArticle(String title, RangeMap<Calendar, String> period2content) {
    super(period2content);
    this.entity = new WikipediaEntity(title, Relation.ORIGINAL,
            GuavaUtils.toRangeSet(period2content));
  }

  public static WikipediaArticle newPeriodicalArticle(String title, Calendar latest,
          Calendar earliest, Wiki wiki) throws IOException {
    WikipediaArticle article = new WikipediaArticle(title);
    Calendar endTime = CalendarUtils.PRESENT;
    for (Revision revision : wiki.getPageHistoryWithInitialVersion(title, latest, earliest)) {
      article.addPeriodicContent(Range.closedOpen(revision.getTimestamp(), endTime),
              revision.getText());
      endTime = revision.getTimestamp();
    }
    return article;
  }

  public void addPeriodicContent(Range<Calendar> period, String content) {
    period2value.put(period, content);
    entity.addValidPeriod(period);
  }

  public WikipediaEntity getEntity() {
    return entity;
  }

  public RangeMap<Calendar, String> getPeriodicContent() {
    return period2value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    WikipediaArticle other = (WikipediaArticle) obj;
    if (entity == null) {
      if (other.entity != null)
        return false;
    } else if (!entity.equals(other.entity))
      return false;
    return true;
  }

}
